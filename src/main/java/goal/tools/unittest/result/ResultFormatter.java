package goal.tools.unittest.result;

import goal.tools.unittest.result.testsection.ActionResult;
import goal.tools.unittest.result.testsection.AssertTestFailed;
import goal.tools.unittest.result.testsection.AssertTestResult;
import goal.tools.unittest.result.testsection.EvaluateInFailed;
import goal.tools.unittest.result.testsection.EvaluateInInterrupted;
import goal.tools.unittest.result.testsection.EvaluateInResult;
import goal.tools.unittest.result.testsection.TestSectionInterupted;
import goal.tools.unittest.testsection.AssertTest;
import goal.tools.unittest.testsection.DoActionSection;
import goal.tools.unittest.testsection.EvaluateIn;
import goal.tools.unittest.testsection.testconditions.Always;
import goal.tools.unittest.testsection.testconditions.AtEnd;
import goal.tools.unittest.testsection.testconditions.AtStart;
import goal.tools.unittest.testsection.testconditions.Eventually;
import goal.tools.unittest.testsection.testconditions.Never;
import goal.tools.unittest.testsection.testconditions.TestConditionEvaluator;
import goal.tools.unittest.testsection.testconditions.Until;
import goal.tools.unittest.testsection.testconditions.While;

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

	T visit(TestConditionEvaluator result);

	T visit(DoActionSection action);

	T visit(UnitTestResult result);

	T visit(AtStart atStart);

	T visit(Always always);

	T visit(Never never);

	T visit(AtEnd atEnd);

	T visit(Eventually eventually);

	T visit(Until until);

	T visit(While whil);

	T visit(AssertTest assertTest);

	T visit(TestSectionInterupted ruleInterupted);
}
