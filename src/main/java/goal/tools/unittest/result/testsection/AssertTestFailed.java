package goal.tools.unittest.result.testsection;

import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.AssertTest;
import languageTools.program.agent.msc.MentalStateCondition;

public class AssertTestFailed extends TestSectionFailed {

	@Override
	public String toString() {
		return "AssertTestFailed [test=" + test + "]";
	}

	private final AssertTest test;

	public AssertTest getTest() {
		return test;
	}

	public AssertTestFailed(AssertTest test) {
		this.test = test;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	public MentalStateCondition getMentalStateTest() {
		return test.getMentalStateTest();
	}

}
