package goal.tools.unittest.result;

import goal.tools.unittest.result.testsection.ActionResult;
import goal.tools.unittest.result.testsection.AssertTestFailed;
import goal.tools.unittest.result.testsection.AssertTestResult;
import goal.tools.unittest.result.testsection.DoActionFailed;
import goal.tools.unittest.result.testsection.EvaluateInFailed;
import goal.tools.unittest.result.testsection.EvaluateInInterrupted;
import goal.tools.unittest.result.testsection.EvaluateInResult;
import goal.tools.unittest.result.testsection.TestSectionFailed;
import goal.tools.unittest.result.testsection.TestSectionInterupted;
import goal.tools.unittest.result.testsection.TestSectionResult;
import goal.tools.unittest.testcondition.executors.TestConditionEvaluator;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.List;
import java.util.Map.Entry;

import languageTools.program.test.AgentTest;
import languageTools.program.test.testcondition.Always;
import languageTools.program.test.testcondition.AtEnd;
import languageTools.program.test.testcondition.AtStart;
import languageTools.program.test.testcondition.Eventually;
import languageTools.program.test.testcondition.Never;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testcondition.Until;
import languageTools.program.test.testcondition.While;
import languageTools.program.test.testsection.AssertTest;
import languageTools.program.test.testsection.DoActionSection;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Formats a {@link UnitTestResult} in a concise and readable fashion.
 *
 * @author M.P. Korstanje
 */
public class UnitTestResultFormatter implements ResultFormatter<String> {
	private String indent() {
		return indent(1);
	}

	private String indent(int level) {
		String ret = "";
		for (int i = 0; i < level; i++) {
			ret += "\t";
		}
		return ret;
	}

	private String indent(int level, String text) {
		String ident = indent(level);
		return ident + text.replaceAll("\n", "\n" + ident).trim();
	}

	private String indent(String text) {
		return indent(1, text);
	}

	/**
	 * Formats a {@link UnitTestResult} by listing all agents that their test
	 * status. If an agent failed its tests, more details are printed.
	 *
	 * @param testRunResult
	 * @return
	 */
	@Override
	public String visit(UnitTestResult unitTestResult) {
		String ret = "";
		if (unitTestResult.isPassed()) {
			ret += "passed:\n";
		} else {
			ret += "failed:\n";
		}
		ret += indent() + "test: " + unitTestResult.getUnitTestFile() + "\n";
		ret += indent() + "mas : " + unitTestResult.getMasFile() + "\n";
		for (Entry<AgentTest, List<UnitTestInterpreterResult>> tr : unitTestResult
				.getResults().entrySet()) {
			List<UnitTestInterpreterResult> resultList = tr.getValue();
			AgentTest test = tr.getKey();
			if (test == null) {
				ret += indent(1, formatGroup("extras", resultList)) + "\n";
			} else {
				ret += indent(1, formatGroup(test.getAgentName(), resultList))
						+ "\n";
			}
		}
		return ret;
	}

	/**
	 * Formats a list of {@link AgentTestResult}s. If the list is empty the test
	 * did not run and a message if printed. If the test contains one result the
	 * list is printed concisely. Otherwise the full list is printed.
	 *
	 * @param groupName
	 * @param results
	 * @param indent
	 * @return
	 */
	private String formatGroup(String groupName,
			List<UnitTestInterpreterResult> results) {
		if (results.isEmpty()) {
			return groupName + ": did not run.\n";
		}
		if (results.size() == 1) {
			UnitTestInterpreterResult result = results.get(0);
			return this.visit(result);
		}
		String ret = groupName + ":\n";
		for (UnitTestInterpreterResult result : results) {
			ret += indent(1, this.visit(result)) + "\n";
		}
		return ret;
	}

	/**
	 * Formats a {@link AgentTestResult}. If passed the result is formatted as a
	 * single line. If the test failed all {@link AssertTestResult}s are
	 * printed.
	 *
	 * @param result
	 * @return
	 */
	@Override
	public String visit(UnitTestInterpreterResult result) {
		if (result.isPassed()) {
			return "passed: " + result.getId().getName() + "\n";
		}
		String retString = "failed: " + result.getId().getName() + "\n";
		if (result.getUncaughtThrowable() != null) {
			result.getUncaughtThrowable().printStackTrace(); // TEMP
			retString += indent(1, "exception: "
					+ result.getUncaughtThrowable().getMessage() + "\n");
			if (result.getUncaughtThrowable().getCause() != null) {
				retString += indent(1, "because: "
						+ result.getUncaughtThrowable().getCause().getMessage()
						+ "\n");
			}
		} else if (result.getResult() == null) {
			retString += indent() + "test did not run or timed out\n";
		} else {
			retString += indent(result.getResult().accept(this)) + "\n";
		}
		return retString;
	}

	@Override
	public String visit(AgentTestResult result) {
		String ret = "";
		if (!result.getTestResult().equals(TestResult.EMPTY)) {
			ret += result.getTestResult().accept(this) + "\n";
		}
		return ret;
	}

	@Override
	public String visit(TestResult result) {
		String ret = result.getTestProgramID() + " {\n";
		for (TestSectionResult r : result.getRuleResults()) {
			ret += indent(r.accept(this)) + "\n";
		}
		if (result.getRuleFailed() != null) {
			TestSectionFailed failed = result.getRuleFailed();
			ret += indent(failed.accept(this));
		}
		ret += "\n}\n";
		return ret;
	}

	@Override
	public String visit(ActionResult actionResult) {
		return "executed: " + actionResult.getAction().accept(this);
	}

	@Override
	public String visit(AssertTestResult result) {
		String ret;
		if (result.isPassed()) {
			ret = "passed: ";
		} else {
			ret = "failed: ";
		}
		ret += "assert " + result.getMentalStateTest();
		if (result.getMessage() != null && result.getMessage().length() > 1) {
			String message = result.getMessage();
			ret += ": " + message.substring(1, message.length() - 1);
		}
		return ret;
	}

	@Override
	public String visit(AssertTestFailed result) {
		return "failed: " + result.getTest().accept(this) + getCause(result);
	}

	@Override
	public String visit(EvaluateInResult result) {
		String ret = "executed: evaluate {\n";
		for (TestConditionEvaluator evaluator : result.getEvaluators()) {
			ret += indent(evaluator.accept(this)) + "\n";
		}
		ret += "} in " + result.getEvaluateIn().getAction() + ".";
		return ret;
	}

	@Override
	public String visit(EvaluateInFailed result) {
		String ret = "failed: evaluate {\n";
		for (TestConditionEvaluator evaluator : result.getEvaluators()) {
			String evalRet = evaluator.accept(this);
			if (evaluator.equals(result.getFirstFailureCause())) {
				evalRet += " <-- first failure";
			}
			ret += indent(evalRet) + "\n";
		}
		EvaluateIn section = (EvaluateIn) result.getEvaluateIn().getSection();
		ret += "} in " + section.getAction() + ".\n";
		ret += getCause(result);
		return ret;
	}

	@Override
	public String visit(DoActionFailed result) {
		String ret = "failed: ";
		DoActionSection section = (DoActionSection) result.getDoAction()
				.getSection();
		ret += section + "\n";
		ret += getCause(result);
		return ret;
	}

	@Override
	public String visit(EvaluateInInterrupted result) {
		String ret = "interrupted: evaluate {\n";
		for (TestConditionEvaluator evaluator : result.getEvaluators()) {
			ret += indent(evaluator.accept(this)) + "\n";
		}
		// Must be interrupted EvaluateIn section
		EvaluateIn section = (EvaluateIn) ((EvaluateInExecutor) result
				.getTestSection()).getSection();
		ret += "} in " + section.getAction() + ".";
		ret += getCause(result);
		return ret;
	}

	@Override
	public String visit(TestConditionEvaluator result) {
		return result.getSummaryReport() + ": "
				+ result.getConditionExecutor().getCondition();
	}

	@Override
	public String visit(DoActionSection action) {
		return "do " + action.getAction();
	}

	@Override
	public String visit(AtStart atStart) {
		return "atstart " + atStart.getQuery();
	}

	@Override
	public String visit(Always always) {
		return "always " + always.getQuery();
	}

	@Override
	public String visit(Never never) {
		return "never " + never.getQuery();
	}

	@Override
	public String visit(AtEnd atEnd) {
		return "atend " + atEnd.getQuery();
	}

	@Override
	public String visit(Eventually eventually) {
		return "eventually " + eventually.getQuery();
	}

	@Override
	public String visit(Until until) {
		return "until " + until.getQuery() + ".";
	}

	@Override
	public String visit(While whil) {
		return "while " + whil.getQuery() + ".";
	}

	@Override
	public String visit(EvaluateIn evaluateIn) {
		String ret = "evaluate {\n";
		for (TestCondition query : evaluateIn.getQueries()) {
			ret += indent(query.toString()) + "\n";
		}
		ret += "} in " + evaluateIn.getAction();
		if (evaluateIn.getBoundary() != null) {
			ret += evaluateIn.getBoundary();
		}
		return ret;
	}

	@Override
	public String visit(AssertTest assertTest) {
		return "assert " + assertTest.getMentalStateTest();
	}

	@Override
	public String visit(TestSectionInterupted testSection) {
		return "interrupted: " + testSection.getTestSection().accept(this);
	}

	private String getCause(TestSectionFailed tsf) {
		String ret = "";
		Throwable cause = tsf.getCause();
		if (cause != null) {
			ret += "\nbecause: " + cause.getMessage();
			if (cause.getCause() != null) {
				ret += "\nbecause: " + cause.getCause().getMessage();
			}
		}
		return ret;
	}
}
