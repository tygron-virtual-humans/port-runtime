package goal.core.executors;

import goal.tools.errorhandling.exceptions.GOALBug;

import java.rmi.activation.UnknownObjectException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import krTools.KRInterface;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Var;
import languageTools.program.agent.rules.Rule;
import mentalstatefactory.MentalStateFactory;

public class ExecuteTools {
	/**
	 * Combines all given substitutions into a single {@link Term}.
	 *
	 * @param substitutions
	 *            A set of substitutions to be mapped onto a single term.
	 * @param language
	 *            The KR language.
	 * @return A new term for the {@link #variable}. The substitution will be a
	 *         list of all values for that var in the given set of
	 *         {@link Substitution}s.
	 * @throws KRInitFailedException
	 * @throws UnknownObjectException
	 */
	public static Term substitutionsToTerm(Set<Substitution> substitutions,
			KRInterface language, Rule rule) {

		mentalState.MentalState state;
		try {
			state = MentalStateFactory.getInterface(language.getClass());
		} catch (UnknownObjectException e) {
			throw new GOALBug(
					"Runtime can't get interface to running language "
							+ language.getName());
		}
		// First make single terms from each substitution.
		List<Term> substsAsTerms = new ArrayList<>(substitutions.size());
		// Get the variables from the condition of the rule; bindings for those
		// variables will be turned into a list.
		Set<Var> boundVar = rule.getCondition().getFreeVar();
		List<Term> subTerms;
		for (Substitution substitution : substitutions) {
			subTerms = new LinkedList<>();
			for (Var v : boundVar) {
				// if (!v.isAnonymous()) { FIXME
				subTerms.add(substitution.get(v));
				// }
			}
			// if there is only one bound var, we shouldn't make lists of them.
			// the end result should simply be a list of values instead of a
			// list of singleton lists.
			if (subTerms.size() == 1) {
				substsAsTerms.add(subTerms.get(0));
			} else if (subTerms.size() > 1) {
				substsAsTerms.add(state.makeList(subTerms));
			}
			// if empty, do not add anything.
			// it means there is no substitution, so we want the end result to
			// be '[]' (and not '[[]]')
		}

		// Second combine the substitutions turned into terms into a single list
		// term.
		return state.makeList(substsAsTerms);
	}

}