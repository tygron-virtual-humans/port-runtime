package goal.tools.eclipse;

import goal.core.agent.Agent;
import goal.core.agent.GOALInterpreter;
import goal.core.executors.MentalStateConditionExecutor;
import goal.core.mentalstate.MentalState;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.GOALException;
import goal.tools.errorhandling.exceptions.GOALUserError;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Set;

import krTools.KRInterface;
import krTools.errors.exceptions.ParserException;
import krTools.language.Substitution;
import languageTools.analyzer.agent.AgentValidator;
import languageTools.errors.Message;
import languageTools.parser.GOAL;
import languageTools.parser.GOAL.ActionContext;
import languageTools.parser.GOAL.MentalStateConditionContext;
import languageTools.parser.GOALLexer;
import languageTools.program.agent.actions.Action;
import languageTools.program.agent.actions.MentalAction;
import languageTools.program.agent.actions.UserSpecAction;
import languageTools.program.agent.msc.MentalStateCondition;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class QueryTool {
	private final Agent<? extends GOALInterpreter<?>> agent;
	private final KRInterface kr;

	public QueryTool(final Agent<? extends GOALInterpreter<?>> agent) {
		this.agent = agent;
		this.kr = agent.getController().getProgram().getKRInterface();
	}

	public String doquery(String userEnteredQuery) throws GOALUserError {
		MentalStateCondition mentalStateCondition;
		try {
			mentalStateCondition = parseMSC(userEnteredQuery);
		} catch (Exception e) {
			throw new GOALUserError("Parsing of " + userEnteredQuery
					+ " failed: " + e.getMessage(), e);
		}
		// Perform query: get the agent's mental state and evaluate the query.
		MentalState mentalState = this.agent.getController().getRunState()
				.getMentalState();
		try {
			// use a dummy debugger
			Set<Substitution> substitutions = new MentalStateConditionExecutor(
					mentalStateCondition).evaluate(mentalState,
					new SteppingDebugger("query", null));
			String resulttext = "";
			if (substitutions.isEmpty()) {
				resulttext = "No solutions";
			} else {
				for (Substitution s : substitutions) {
					resulttext = resulttext + s + "\n";
				}
			}
			return resulttext;
		} catch (Exception e) {
			throw new GOALUserError("Query entered in query area in "
					+ "introspector of agent " + this.agent.getId()
					+ " failed: " + e.getMessage(), e);
		}
	}

	public String doaction(String userEnteredAction) throws GOALUserError {
		MentalState mentalState = this.agent.getController().getRunState()
				.getMentalState();
		if (mentalState == null) {
			throw new GOALUserError("Agent has not yet "
					+ "initialized its databases");
		}
		try {
			Action<?> action = parseAction(userEnteredAction);
			if (action.isClosed()) {
				// Perform the action.
				this.agent.getController().doPerformAction(action);
				return "Executed action " + action;
			} else {
				return "Action is not closed and cannot be executed";
			}

		} catch (Exception e) {
			throw new GOALUserError("Action entered in query area in "
					+ "introspector of agent " + this.agent.getId()
					+ " failed: " + e.getMessage(), e);
		}
	}

	/**
	 * Creates an embedded GOAL parser that can parse the given string.
	 *
	 * @param pString
	 *            is the string to be parsed.
	 * @return a GOALWAlker that can parse text at the GOAL level.
	 */
	private GOAL prepareGOALParser(String pString) {
		try {
			ANTLRInputStream charstream = new ANTLRInputStream(
					new StringReader(pString));
			GOALLexer lexer = new GOALLexer(charstream);
			CommonTokenStream stream = new CommonTokenStream(lexer);
			return new GOAL(stream);
		} catch (IOException e) {
			throw new GOALBug("internal error while setting up GOAL parser", e);
		}
	}

	/**
	 * DOC
	 *
	 * @param mentalStateCondition
	 *            Input string that should represent a mental state condition.
	 * @return The mental state condition that resulted from parsing the input
	 *         string.
	 * @throws GOALException
	 *             When the parser throws a RecognitionException, which should
	 *             have been buffered and ignored.
	 * @throws ParserException
	 *             DOC
	 */
	public MentalStateCondition parseMSC(String mentalStateCondition)
			throws GOALException, ParserException {
		// Try to parse the MSC.
		GOAL parser = prepareGOALParser(mentalStateCondition);
		MentalStateConditionContext mscContext = parser.mentalStateCondition();
		AgentValidator test = new AgentValidator("inline");
		test.setKRInterface(this.kr);
		MentalStateCondition msc = test.visitMentalStateCondition(mscContext);
		checkParserErrors(test, mentalStateCondition, "mental state condition ");
		// TODO? macros are not resolved, not clear how to do that anyways.
		return msc;
	}

	/**
	 * check if any error has occurred. (only print lexer errors when there are
	 * no parser errors) Aggregate the error messages into a string, such that
	 * it can be put into the message of an exception to be thrown. The caller
	 * prints that message to the query console.
	 *
	 * @param walker
	 *            is the GOALWalker used to parse the string
	 * @param query
	 *            is the string that was fed to the GOALParser and that failed.
	 * @pparam desc is a string describing what was attempted to parse. Used for
	 *         error message generation if the parse failed.
	 * @throws GOALUserError
	 *             if error occured..
	 * @throws ParserException
	 */
	private void checkParserErrors(AgentValidator walker, String query,
			String desc) throws GOALUserError, ParserException {
		List<Message> errors = walker.getErrors();
		errors.addAll(walker.getSyntaxErrors());
		String errMessage = "";
		if (!errors.isEmpty()) {
			for (Message err : errors) {
				errMessage += err.toString() + "\n";
			}
		}

		// if any error has occurred, throw a UserError
		if (!errMessage.isEmpty()) {
			throw new GOALUserError("Term " + query + " failed to parse as "
					+ desc + ";\n" + errMessage);
		}
	}

	/**
	 * Parse string as a mental action.
	 *
	 * @author W.Pasman.
	 * @throws ParserException
	 * @modified N.Kraayenbrink - GOAL parser does not print errors any more.
	 * @modified W.Pasman 8feb2012 now also UserSpecActions can be parsed.
	 * @modified K.Hindriks if UserOrFocusAction action must be UserSpecAction.
	 */
	private Action<?> parseAction(String action) throws GOALException,
			ParserException {
		GOAL parser = prepareGOALParser(action);
		ActionContext actionContext = parser.action();
		AgentValidator test = new AgentValidator("inline");
		test.setKRInterface(this.kr);
		Action<?> act = test.visitAction(actionContext);
		checkParserErrors(test, action, "action");

		// TODO: this code implements a rather naive approach to getting the
		// action specification(s)
		// of the action. In particular, it does not take scope into account. If
		// an action is specified
		// e.g. at top level and again within a module, then this approach is
		// not able to determine
		// which action specification should be associated with the action.
		// If the user intends to execute the action in the module, we should
		// allow for expressions of
		// the form: module.action(parameters) so that the user is able to refer
		// to the right scope.

		// Store the actionspecification matching the given action in the
		// action.
		// Since there is no such reference in Action anymore, this seems not
		// necessary anymore.
		// if (act instanceof UserSpecOrModuleCall) {
		// // Must be user-specified action.
		// // TODO: now sets by default that action is EXTERNAL and should be
		// // sent to environment.
		// act = new UserSpecAction(act.getName(),
		// ((UserSpecOrModuleCall) act).getParameters(), true, null,
		// null, null);
		// // Search for all corresponding action specifications.
		// for (Module module : this.agent.getController().getProgram()
		// .getModules()) {
		// for (ActionSpecification specification : module
		// .getActionSpecifications()) {
		// if (act.getName().equals(
		// specification.getAction().getName())
		// && (((UserSpecAction) act).getParameters().size() == specification
		// .getAction().getParameters().size())) {
		// try {
		//
		// ((UserSpecAction) act).
		// .addSpecification(specification);
		// } catch (KRInitFailedException e) {
		// throw new GOALUserError(
		// "Failed to associate specification with action: "
		// + e.getMessage(), e);
		// }
		// }
		// }
		// }
		// }

		// module calls would be dangerous.
		if (!(act instanceof MentalAction)
				&& (!(act instanceof UserSpecAction))) {
			throw new GOALUserError(
					"Action "
							+ action
							+ " must be either a built-in mental action or a user-specified action (found "
							+ act.getClass() + " instead).");
		}

		return act;
	}
}
