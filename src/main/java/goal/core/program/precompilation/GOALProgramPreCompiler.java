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

import goal.core.program.GOALProgram;
import goal.core.program.Module;
import goal.core.program.rules.Rule;

/**
 * {@link PreCompiler} for {@link GOALProgram}s.<br>
 *
 * @author K.Hindriks
 *
 */
public class GOALProgramPreCompiler extends PreCompiler<GOALProgram> {

	/**
	 * Goes through all modules to precompile all rules.
	 */
	@Override
	protected void doPreCompile(GOALProgram subject) {
		for (Module module : subject.getAllModules()) {
			for (Rule rule : module.getRuleSet()) {
				RulePreCompiler rulePreCompiler = new RulePreCompiler();
				rulePreCompiler.setKRlanguage(subject.getKRLanguage());
				rulePreCompiler.preCompile(rule);
			}
		}
	}

}
