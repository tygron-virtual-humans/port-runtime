package goal.core.program.literals;

import goal.core.kr.language.Query;
import goal.core.kr.language.Substitution;
import goal.core.program.Selector;
import goal.parser.InputStreamPosition;

public class GoalALiteral extends MentalLiteral {

	public GoalALiteral(boolean posLiteral, Query formula, Selector selector,
			InputStreamPosition source) {
		super(posLiteral, formula, selector, source);
	}

	public GoalALiteral(InputStreamPosition source) {
		super(source);
	}

	@Override
	public MentalLiteral applySubst(Substitution pSubst) {
		return new GoalALiteral(this.polarity, this.formula.applySubst(pSubst),
				this.selector.applySubst(pSubst), this.getSource());
	}

	@Override
	public String getLiteralTypeString() {
		return "goal-a";
	}

}
