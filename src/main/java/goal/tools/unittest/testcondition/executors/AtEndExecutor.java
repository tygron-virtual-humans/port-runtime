package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.AtEnd;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * AtEnd operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should hold after the execution of the
 * module in the EvaluateIn rule.
 *
 * @author mpkorstanje
 */
public class AtEndExecutor extends TestConditionExecutor {
	private final AtEnd atend;

	public AtEndExecutor(AtEnd atend, Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		super(substitution, runstate, parent);
		this.atend = atend;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.atend);
	}

	@Override
	public void evaluate(TestEvaluationChannel channel) {
		if (channel == TestEvaluationChannel.MODULE_EXIT) { // ATEND
			if (this.atend.hasNestedCondition()) {
				// NOT POSSIBLE?!
			} else if (this.current.getAssociatedObject().equals(
					this.parent.getSection().getAction().getTarget())) {
				final Set<Substitution> evaluation = evaluate();
				if (evaluation.isEmpty()) {
					setPassed(false);
					throw new TestConditionFailedException("The condition "
							+ this.atend + " did not hold.", this);
				} else {
					setPassed(true);
				}
			}
		}
	}

	@Override
	public TestCondition getCondition() {
		return this.atend;
	}
}
