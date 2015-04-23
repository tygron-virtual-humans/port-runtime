package goal.tools.unittest.testcondition.executors;

import goal.core.runtime.service.agent.RunState;
import goal.tools.debugger.Debugger;
import goal.tools.unittest.result.ResultFormatter;
import goal.tools.unittest.testsection.executors.EvaluateInExecutor;

import java.util.HashSet;
import java.util.Set;

import krTools.language.Substitution;
import languageTools.program.test.testcondition.TestCondition;
import languageTools.program.test.testcondition.Watch;
import languageTools.program.test.testsection.EvaluateIn;

/**
 * Watch operator for LTL queries in {@link EvaluateIn}. The mental state
 * condition evaluation of this operator is simply printed.
 */
public class WatchExecutor extends TestConditionExecutor {
	private final Watch watch;
	private Set<Substitution> evaluation;

	public WatchExecutor(Watch watch, Substitution substitution,
			RunState<? extends Debugger> runstate, EvaluateInExecutor parent) {
		super(substitution, runstate, parent);
		this.watch = watch;
		this.evaluation = new HashSet<>(0);
		setPassed(true);
	}

	@Override
	public <T> T accept(ResultFormatter<T> formatter) {
		return formatter.visit(this);
	}

	@Override
	public void evaluate(TestEvaluationChannel channel) {
		Set<Substitution> evaluation = evaluate();
		if (!evaluation.isEmpty()) {
			this.evaluation = evaluation;
			System.out.println(this.watch + ": " + this.evaluation);
		}
	}

	public Set<Substitution> getEvaluation() {
		return this.evaluation;
	}

	@Override
	public TestCondition getCondition() {
		return this.watch;
	}
}
