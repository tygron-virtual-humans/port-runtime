package goal.core.program.literals;

import goal.core.kr.language.Query;
import goal.core.kr.language.Substitution;
import goal.core.program.Selector;
import goal.parser.InputStreamPosition;

public class AGoalLiteral extends MentalLiteral {

	public AGoalLiteral(boolean posLiteral, Query formula, Selector selector,
			InputStreamPosition source) {
		super(posLiteral, formula, selector, source);
	}

	public AGoalLiteral(InputStreamPosition source) {
		super(source);
	}

	@Override
	public MentalLiteral applySubst(Substitution pSubst) {
		return new AGoalLiteral(this.polarity, this.formula.applySubst(pSubst),
				this.selector.applySubst(pSubst), this.getSource());
	}

	@Override
	public String getLiteralTypeString() {
		return "a-goal";
	}

}
