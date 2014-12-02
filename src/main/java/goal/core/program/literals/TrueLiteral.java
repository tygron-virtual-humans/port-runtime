package goal.core.program.literals;

import goal.core.kr.language.Substitution;
import goal.core.kr.language.Var;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;

import java.util.HashSet;
import java.util.Set;

public class TrueLiteral extends ParsedObject implements MentalFormula {

	public TrueLiteral(InputStreamPosition source) {
		super(source);
	}

	@Override
	public MentalFormula applySubst(Substitution pSubst) {
		return this;
	}

	@Override
	public Set<Var> getFreeVar() {
		return new HashSet<>(0);
	}

	@Override
	public String toString() {
		return "true";
	}

}
