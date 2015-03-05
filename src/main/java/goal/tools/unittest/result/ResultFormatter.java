package goal.tools.unittest.result;

import goal.tools.unittest.result.testsection.ActionResult;
import goal.tools.unittest.result.testsection.AssertTestFailed;
import goal.tools.unittest.result.testsection.AssertTestResult;
import goal.tools.unittest.result.testsection.DoActionFailed;
import goal.tools.unittest.result.testsection.EvaluateInFailed;
import goal.tools.unittest.result.testsection.EvaluateInInterrupted;
import goal.tools.unittest.result.testsection.EvaluateInResult;
import goal.tools.unittest.result.testsection.TestSectionInterupted;
import goal.tools.unittest.testcondition.executors.TestConditionExecutor;
import languageTools.program.test.testcondition.Always;
import languageTools.program.test.testcondition.AtEnd;
import languageTools.program.test.testcondition.Eventually;
import languageTools.program.test.testcondition.Never;
import languageTools.program.test.testcondition.Until;
import languageTools.program.test.testcondition.While;
import languageTools.program.test.testsection.AssertTest;
import languageTools.program.test.testsection.DoActionSection;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Walks the unit test result tree.
 *
 * @author mpkorstanje
 *
 * @param <T>
 *            The formatter type
 */
public interface ResultFormatter<T> {
	T visit(EvaluateIn evaluateIn);

	T visit(UnitTestInterpreterResult result);

	T visit(AgentTestResult result);

	T visit(TestResult result);

	T visit(ActionResult result);

	T visit(AssertTestResult result);

	T visit(EvaluateInResult result);

	T visit(AssertTestFailed result);

	T visit(EvaluateInFailed result);

	T visit(EvaluateInInterrupted result);

	T visit(DoActionFailed result);

	T visit(TestConditionExecutor result);

	T visit(DoActionSection action);

	T visit(UnitTestResult result);

	T visit(Always always);

	T visit(Never never);

	T visit(AtEnd atEnd);

	T visit(Eventually eventually);

	T visit(Until until);

	T visit(While whil);

	T visit(AssertTest assertTest);

	T visit(TestSectionInterupted ruleInterupted);
}
