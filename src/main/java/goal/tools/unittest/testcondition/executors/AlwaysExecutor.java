package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestConditionFailedException;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.HashSet;
import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.Always;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Always operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluated by this operator should always hold during the execution
 * of the actions in the EvaluateIn rule.
 *
 * @author mpkorstanje
 */
public class AlwaysExecutor extends TestConditionExecutor {
	private final Always always;
	private Set<Substitution> evaluation;

	public AlwaysExecutor(Always always, Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		super(substitution, runstate, parent);
		this.always = always;
		this.evaluation = new HashSet<>(0);
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this.always);
	}

	@Override
	public void evaluate(TestEvaluationChannel channel) {
		Set<Substitution> prev = new HashSet<>(this.evaluation);
		this.evaluation = evaluate();

		if (this.always.hasNestedCondition()) {
			if (channel == TestEvaluationChannel.STOPTEST) {
				setPassed(true);
			} else if (!this.evaluation.isEmpty()
					&& !equals(prev, this.evaluation)) {
				for (Substitution subst : this.evaluation) {
					this.parent.add(TestConditionExecutor
							.getTestConditionExecutor(
									this.always.getNestedCondition(), subst,
									this.runstate, this.parent));
				}
			}
		} else {
			if (this.evaluation.isEmpty()) {
				setPassed(false);
				throw new TestConditionFailedException("The condition "
						+ this.always + " did not hold.", this);
			} else if (channel == TestEvaluationChannel.STOPTEST) {
				setPassed(true);
			}
		}
	}

	private static boolean equals(Set<Substitution> set1, Set<Substitution> set2) {
		// Regular equals on the sets does not seem to work...
		if (set1.size() != set2.size()) {
			return false;
		} else {
			for (Substitution s1 : set1) {
				boolean found = false;
				for (Substitution s2 : set2) {
					if (s1.equals(s2)) {
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}
			return true;
		}
	}

	@Override
	public TestCondition getCondition() {
		return this.always;
	}
}
