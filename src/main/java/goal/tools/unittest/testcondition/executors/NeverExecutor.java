package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.Never;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Never operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should never hold during the execution
 * of the actions in the EvaluateIn rule.
 *
 * @author V.Koeman
 */
public class NeverExecutor extends TestConditionExecutor {
	private final Never never;

	public NeverExecutor(Never never, Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		super(substitution, runstate, parent);
		this.never = never;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	@Override
	public void evaluate(TestEvaluationChannel channel)
			throws TestConditionFailedException {
		if (this.never.hasNestedCondition()) {
			// NOT POSSIBLE?!
		} else {
			final Set<Substitution> evaluation = evaluate();
			if (!evaluation.isEmpty()) {
				setPassed(false);
				throw new TestConditionFailedException("The condition "
						+ this.never + " did not hold.", this);
			} else if (channel == TestEvaluationChannel.STOPTEST) {
				setPassed(true);
			}
		}
	}

	@Override
	public TestCondition getCondition() {
		return this.never;
	}
}
