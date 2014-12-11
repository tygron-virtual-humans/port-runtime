package goal.core.executors;

import eis.iilang.Identifier;
import eis.iilang.Parameter;
import goal.core.mentalstate.MentalState;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.tools.errorhandling.exceptions.GOALRuntimeErrorException;

import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import krTools.KRInterface;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Var;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.rules.Rule;
import languageTools.program.agent.selector.Selector;
import mentalstatefactory.MentalStateFactory;

public class ExecuteTools {
	/**
	 * utility class
	 */
	private ExecuteTools() {
	}

	/**
	 * Combines all given substitutions into a single {@link Term}.
	 *
	 * @param substitutions
	 *            A set of substitutions to be mapped onto a single term.
	 * @param language
	 *            The KR language.
	 * @return A new term for the {@link #variable}. The substitution will be a
	 *         list of all values for that var in the given set of
	 *         {@link Substitution}s.
	 * @throws KRInitFailedException
	 * @throws UnknownObjectException
	 */
	public static Term substitutionsToTerm(Set<Substitution> substitutions,
			KRInterface language, Rule rule) {
		mentalState.MentalState state;
		try {
			state = MentalStateFactory.getInterface(language.getClass());
		} catch (UnknownObjectException e) {
			throw new GOALBug(
					"Runtime can't get interface to running language "
							+ language.getName());
		}
		// First make single terms from each substitution.
		List<Term> substsAsTerms = new ArrayList<>(substitutions.size());
		// Get the variables from the condition of the rule; bindings for those
		// variables will be turned into a list.
		Set<Var> boundVar = rule.getCondition().getFreeVar();
		List<Term> subTerms;
		for (Substitution substitution : substitutions) {
			subTerms = new LinkedList<>();
			for (Var v : boundVar) {
				// if (!v.isAnonymous()) { FIXME
				subTerms.add(substitution.get(v));
				// }
			}
			// if there is only one bound var, we shouldn't make lists of them.
			// the end result should simply be a list of values instead of a
			// list of singleton lists.
			if (subTerms.size() == 1) {
				substsAsTerms.add(subTerms.get(0));
			} else if (subTerms.size() > 1) {
				substsAsTerms.add(state.makeList(subTerms));
			}
			// if empty, do not add anything.
			// it means there is no substitution, so we want the end result to
			// be '[]' (and not '[[]]')
		}

		// Second combine the substitutions turned into terms into a single list
		// term.
		return state.makeList(substsAsTerms);
	}

	/**
	 * Resolves the selector to agent names by expanding quantors. If a fixed
	 * list of agent names has been set, returns that instead.
	 *
	 * Notice that we are handling Strings as agent names. This means the EIS
	 * strings that can contain upper case characters etc. Because the
	 * selectExpressions that we have are language dependent Terms, they will
	 * have to be converted with language dependent translator. This
	 * particularly happens when the eis entities have name starting with upper
	 * case character, and the PrologTerm in that case has quotes around it.
	 *
	 * @param mentalState
	 *            The mental state of the agent who runs the code containing
	 *            this selector.
	 * @return The set of agent names that this selector refers to.
	 * @throws IllegalArgumentException
	 * @throws KRInitFailedException
	 * @throws GOALRuntimeErrorException
	 *             If a SelectExpression is found that is not closed.
	 */
	@SuppressWarnings("fallthrough")
	public static Set<AgentId> resolve(Selector selector,
			MentalState mentalState) throws IllegalArgumentException,
			KRInitFailedException {
		KRInterface kr = mentalState.getOwner().getKRInterface();
		mentalState.MentalState state;
		try {
			state = MentalStateFactory.getInterface(kr.getClass());
		} catch (UnknownObjectException e) {
			throw new GOALBug(
					"Runtime can't get interface to running language "
							+ kr.getName());
		}
		// Resolve the selector expressions.
		HashSet<AgentId> agentNames = new HashSet<>();
		switch (selector.getType()) {
		case ALL:
		case SOME:
			agentNames.addAll(mentalState.getKnownAgents());
			break;
		case ALLOTHER:
		case SOMEOTHER:
			agentNames.addAll(mentalState.getKnownAgents());
			agentNames.remove(mentalState.getAgentId());
			break;
		case PARAMETERLIST:
			for (Term term : selector.getParameters()) {
				Parameter param = state.convert(term);
				if (param instanceof Identifier) {
					agentNames
					.add(new AgentId(((Identifier) param).getValue()));
				} else {
					throw new KRInitFailedException(
							"Trying to send to non-agent: " + term);
				}
			}
			break;
		default:
		case THIS:
		case SELF:
			agentNames.add(mentalState.getAgentId());
			break;
		}
		return agentNames;
	}
}