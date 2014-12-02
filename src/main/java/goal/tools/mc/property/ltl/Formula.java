/**
 * GOAL interpreter that facilitates developing and executing GOAL multi-agent
 * programs. Copyright (C) 2011 K.V. Hindriks, W. Pasman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package goal.tools.mc.property.ltl;

import goal.tools.errorhandling.exceptions.KRInitFailedException;
import goal.tools.errorhandling.exceptions.ParserException;
import goal.tools.mc.core.lmhashset.LMHashSet;

import java.io.IOException;

import org.antlr.runtime.RecognitionException;

/**
 * Represents an LTL formula. This class should not be used on its own (except
 * for its static methods), but rather should be extended for more specific
 * (temporal) operators.
 *
 * @author sungshik
 */
public abstract class Formula {

	//
	// Abstract methods
	//

	/**
	 * Get all arguments of this formula.
	 *
	 * @return The arguments.
	 */
	public abstract LMHashSet<Formula> getArgs();

	//
	// Public methods
	//

	/**
	 * Gets all propositions occurring in this formula, by recursively calling
	 * this method on its arguments.
	 *
	 * @return The propositions ocurring in this formula.
	 */
	public LMHashSet<Proposition> getPropositions() {

		try {

			/* Return variable */
			LMHashSet<Proposition> propositions = new LMHashSet<Proposition>();

			/* If this is a proposition itself, return this */
			if (this instanceof Proposition) {
				propositions.add((Proposition) this);
			}

			/*
			 * If this is not a proposition, recursively call this method on
			 * arguments
			 */
			else {
				for (Formula f : getArgs()) {
					propositions.addAll(f.getPropositions());
				}
			}

			/* Return */
			return propositions;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int hashCode() {
		return -1;
	}

	/**
	 * Returns the literals that occur in the specified set. Assumes the
	 * formulas in the specified set are in NNF such that negations occur only
	 * before propositions.
	 *
	 * @param set
	 *            - The formulas to extract the literals from.
	 * @return The literals.
	 */
	public static LMHashSet<Formula> literals(LMHashSet<Formula> set) {

		try {
			LMHashSet<Formula> literals = new LMHashSet<Formula>();
			for (Formula f : set) {
				if (f instanceof Proposition || (f instanceof Negation)) {
					literals.add(f);
				}
			}
			return literals;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns a set of formulas containing the arguments of all the next
	 * formulas in the specified set
	 *
	 * @param set
	 *            - The formulas to extract the arguments from.
	 * @return The arguments of next formulas.
	 */
	public static LMHashSet<Formula> nextArgs(LMHashSet<Formula> set) {

		try {
			LMHashSet<Formula> nextArgs = new LMHashSet<Formula>();
			for (Formula f : set) {
				if (f instanceof Next) {
					nextArgs.add(((Next) f).getArg());
				}
			}
			return nextArgs;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converts this formula to negation normal form.
	 *
	 * @return The NNF of this formula.
	 */
	public Formula nnf() {

		/* If f is negated, work this negation inwards */
		if (this instanceof Negation) {
			Formula negated = ((Negation) this).getArg();
			if (negated instanceof Negation) {
				return ((Negation) negated).getArg().nnf();
			}
			if (negated instanceof Next) {
				return new Next(new Negation(((Next) negated).getArg()).nnf());
			}
			if (negated instanceof Conjunction) {
				LMHashSet<Formula> nnfArgs = new LMHashSet<Formula>();
				for (Formula arg : ((Conjunction) negated).getArgs()) {
					nnfArgs.add(new Negation(arg).nnf());
				}
				return new Disjunction(nnfArgs);
			}
			if (negated instanceof Disjunction) {
				LMHashSet<Formula> nnfArgs = new LMHashSet<Formula>();
				for (Formula arg : ((Disjunction) negated).getArgs()) {
					nnfArgs.add(new Negation(arg).nnf());
				}
				return new Conjunction(nnfArgs);
			}
			if (negated instanceof Until) {
				return new Release(
						new Negation(((Until) negated).getLeftArg()).nnf(),
						new Negation(((Until) negated).getRightArg()).nnf());
			}
			if (negated instanceof Release) {
				return new Until(
						new Negation(((Release) negated).getLeftArg()).nnf(),
						new Negation(((Release) negated).getRightArg()).nnf());
			}
			if (negated instanceof True) {
				return new False();
			}
			if (negated instanceof False) {
				return new True();
			}
		}

		/*
		 * In all other cases (if f is not a negation), proceed by NNF-ing the
		 * arguments of this formula
		 */
		if (this instanceof Next) {
			return new Next(((Next) this).getArg().nnf());
		}
		if (this instanceof Conjunction) {
			LMHashSet<Formula> nnfArgs = new LMHashSet<Formula>();
			for (Formula arg : ((Conjunction) this).getArgs()) {
				nnfArgs.add(arg.nnf());
			}
			return new Conjunction(nnfArgs);
		}
		if (this instanceof Disjunction) {
			LMHashSet<Formula> nnfArgs = new LMHashSet<Formula>();
			for (Formula arg : ((Disjunction) this).getArgs()) {
				nnfArgs.add(arg.nnf());
			}
			return new Disjunction(nnfArgs);
		}
		if (this instanceof Until) {
			return new Until(((Until) this).getLeftArg().nnf(), ((Until) this)
					.getRightArg().nnf());
		}
		if (this instanceof Release) {
			return new Release(((Release) this).getLeftArg().nnf(),
					((Release) this).getRightArg().nnf());
		}

		/* If this is a proposition, return this */
		return this;
	}

	/**
	 * Parses the specified string to an LTL formula, using the specified parser
	 * for propositions (this could be null).
	 *
	 * @param s
	 *            - The string to be parsed.
	 * @param pp
	 *            - The PropositionParser to be used. If null, all propositions
	 *            are regarded as strings.
	 * @return The parsed string.
	 * @throws IOException
	 * @throws RecognitionException
	 * @throws ParseException
	 * @throws KRInitFailedException
	 */
	public static Formula parse(String s) throws IOException,
	RecognitionException, ParserException, KRInitFailedException {

		/*
		 * Use GOAL's standard parser to parse LTL formulas. #2374 Reader
		 * stringReader = new StringReader(s); ANTLRReaderStream charStream =
		 * new ANTLRReaderStream(stringReader); GOALLexer lexer = new
		 * GOALLexer(charStream); lexer.initialize(null); LinkedListTokenSource
		 * linker = new LinkedListTokenSource(lexer); LinkedListTokenStream
		 * tokenStream = new LinkedListTokenStream(linker); GOALParser parser =
		 * new GOALParser(tokenStream); parser.initialize(null,
		 * SWIPrologLanguage.getInstance());// "GOALPropositionParser");
		 * parser.setInput(lexer, charStream); Formula res = parser.tlformula();
		 * if (parser.hasErrors()) { throw new ParseException("parse failed:" +
		 * parser.getErrors().get(0)); } return res;
		 */
		return null; // FIXME: this does not belong in the (new) GOAL parser?!

		// try {
		// LTLLexer lex = new LTLLexer(new ANTLRStringStream(s));
		// CommonTokenStream tokens = new CommonTokenStream(lex);
		// LTLParser parser = new LTLParser(tokens);
		// parser.setPropositionParser(pp);
		// return parser.top();
		// }
		//
		// catch (Exception e) {
		// if (out) {
		// e.printStackTrace();
		// }
		// return null;
		// }
	}

	/**
	 * Returns all the until formulas that are sub-formulas of this formula.
	 * Works by recursively calling this method on this' arguments.
	 *
	 * @return The until formulas.
	 */
	public LMHashSet<Until> untils() {

		try {
			LMHashSet<Until> untils = new LMHashSet<Until>();
			if (this instanceof Negation) {
				untils.addAll(((Negation) this).getArg().untils());
			}
			if (this instanceof Next) {
				untils.addAll(((Next) this).getArg().untils());
			}
			if (this instanceof Conjunction) {
				for (Formula arg : ((Conjunction) this).getArgs()) {
					untils.addAll(arg.untils());
				}
			}
			if (this instanceof Disjunction) {
				for (Formula arg : ((Disjunction) this).getArgs()) {
					untils.addAll(arg.untils());
				}
			}
			if (this instanceof Until) {
				Until until = (Until) this;
				untils.add(until);
				untils.addAll(until.getLeftArg().untils());
				untils.addAll(until.getRightArg().untils());
			}
			if (this instanceof Release) {
				Release rel = (Release) this;
				untils.addAll(rel.getLeftArg().untils());
				untils.addAll(rel.getRightArg().untils());
			}
			return untils;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
