package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.Eventually;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Eventually operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should hold at some point during the
 * execution of the actions in the EvaluateIn section.
 *
 * @author mpkorstanje
 */
public class EventuallyExecutor extends TestConditionExecutor {
	private final Eventually eventually;

	public EventuallyExecutor(Eventually eventually, Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		super(substitution, runstate, parent);
		this.eventually = eventually;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	@Override
	public void evaluate(TestEvaluationChannel channel) {
		final Set<Substitution> evaluation = evaluate();
		if (this.eventually.hasNestedCondition()) {
			if (!evaluation.isEmpty()) {
				for (Substitution subst : evaluation) {
					this.parent.add(TestConditionExecutor
							.getTestConditionExecutor(
									this.eventually.getNestedCondition(),
									subst, this.runstate, this.parent));
				}
				setPassed(true);
			}
		} else if (!evaluation.isEmpty()) {
			setPassed(true);
		} else if (channel == TestEvaluationChannel.STOPTEST) {
			setPassed(false);
		}
	}

	@Override
	public TestCondition getCondition() {
		return this.eventually;
	}
}
