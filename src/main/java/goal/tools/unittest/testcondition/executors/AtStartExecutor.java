package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.AtStart;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * AtStart operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should hold after the execution of the
 * specified module in the EvaluateIn rule.
 *
 * @author K.Hindriks
 */
public class AtStartExecutor extends TestConditionExecutor {
	private final AtStart atstart;

	public AtStartExecutor(AtStart atstart, Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		super(substitution, runstate, parent);
		this.atstart = atstart;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.atstart);
	}

	@Override
	public void evaluate(TestEvaluationChannel channel) {
		if (channel == TestEvaluationChannel.START) { // ATSTART
			final Set<Substitution> evaluation = evaluate();
			if (this.atstart.hasNestedCondition()) {
				if (!evaluation.isEmpty()) {
					for (Substitution subst : evaluation) {
						this.parent.add(TestConditionExecutor
								.getTestConditionExecutor(
										this.atstart.getNestedCondition(),
										subst, this.runstate, this.parent));
					}
				}
				setPassed(true);
			} else {
				if (evaluation.isEmpty()) {
					setPassed(false);
					throw new TestConditionFailedException("The condition "
							+ this.atstart + " did not hold.", this);
				} else {
					setPassed(true);
				}
			}
		}
	}

	@Override
	public TestCondition getCondition() {
		return this.atstart;
	}
}
