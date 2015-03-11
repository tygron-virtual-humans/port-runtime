package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestBoundaryException;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testcondition.Until;

/**
 * Until operator. When the mental state condition evaluated by this operator
 * holds at some point during the execution of the actions in the EvaluateIn
 * section, the corresponding agent is terminated.
 *
 * @author V.Koeman
 */
public class UntilExecutor extends TestConditionExecutor {
	private final Until until;

	public UntilExecutor(Until until, Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		super(substitution, runstate, parent);
		this.until = until;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.until);
	}

	@Override
	public void evaluate(TestEvaluationChannel channel) {
		if (channel != null) { // UNTIL
			if (this.until.hasNestedCondition()) {
				// NOT POSSIBLE?!
			} else {
				final Set<Substitution> evaluation = evaluate();
				if (!evaluation.isEmpty()) {
					setPassed(true);
					throw new TestBoundaryException("Boundary " + this.until
							+ " reached");
				}
			}
		}
	}

	@Override
	public TestCondition getCondition() {
		return this.until;
	}
}
