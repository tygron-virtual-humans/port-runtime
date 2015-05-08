package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.result.testcondition.TestBoundaryException;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testcondition.While;

/**
 * While operator. When the mental state condition evaluated by this operator
 * does not hold at some point during the execution of the actions in the
 * EvaluateIn section, the corresponding agent is terminated.
 *
 * @author V.Koeman
 */
public class WhileExecutor extends TestConditionExecutor {
	private final While _while;

	public WhileExecutor(While _while, Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		super(substitution, runstate, parent);
		this._while = _while;
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	@Override
	public void evaluate(TestEvaluationChannel channel) {
		if (channel != null) { // UNTIL
			if (this._while.hasNestedCondition()) {
				// NOT POSSIBLE?!
			} else {
				final Set<Substitution> evaluation = evaluate();
				if (evaluation.isEmpty()) {
					setPassed(true);
					throw new TestBoundaryException("Boundary " + this._while
							+ " reached");
				}
			}
		}
	}

	@Override
	public TestCondition getCondition() {
		return this._while;
	}
}
