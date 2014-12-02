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

package goal.core.program.precompilation;

import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalStateCond;
import goal.core.program.rules.Rule;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.GOALParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link PreCompiler} for {@link Rule}s.<br>
 *
 * @author K.Hindriks
 *
 */
public class RulePreCompiler extends PreCompiler<Rule> {

	@Override
	protected void doPreCompile(Rule subject) {
		// Collect all literals first. If there is anything, we may be able to
		// compile.
		MentalStateCond precond;
		MentalStateCond extendedcondition = subject.getCondition();
		List<MentalFormula> newformulae = new ArrayList<MentalFormula>(subject
				.getCondition().getLiterals());
		try {
			precond = subject.getAction().getAction(0)
					.getPrecondition(this.getKRlanguage());
			newformulae.addAll(precond.getLiterals());
		} catch (GOALParseException e) {
			// We're simply going to ignore the exception because we know that
			// something went wrong parsing the "true" precondition of the
			// DeleteAction, .... Also see: Action.java.
			new Warning(String.format(Resources
					.get(WarningStrings.FAILED_PRECOMPILE_PARSE_PRECOND),
					subject.getAction().toString()));
		}
		extendedcondition = new MentalStateCond(newformulae,
				subject.getSource());

		if (getKRlanguage().getCompiler() == null) {
			return;
		}

		// TODO integrate ALL action preconditions
		// Check if there are conditions at all. If not, we can't compile. See
		// also #1989
		subject.setPreCompiledQuery(getKRlanguage().getCompiler().compile(
				extendedcondition));
	}

}
