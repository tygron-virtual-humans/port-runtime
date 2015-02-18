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
package goal.core.program;

import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Various test cases for variable substitution in GOAL.
 *
 * @author W.Pasman
 *
 */
public class VarSubstitution extends SimpleProgramTest {
	@Test(expected = IllegalStateException.class)
	public void TestAdoptFreeVar() throws Exception {
		runAgent("src/test/resources/goal/core/program/adoptfreevar.goal");
	}

	@Test
	public void testModuleVarInListall() throws Exception {
		assertSame(
				RunResult.OK,
				runAgent("src/test/resources/goal/core/program/modulevarinlistall.goal"));
	}

	@Test
	public void testModuleVarInGoals() throws Exception {
		assertSame(
				RunResult.OK,
				runAgent("src/test/resources/goal/core/program/modulevaringoal.goal"));
	}

	@Test
	public void testModuleVarInActionSpec() throws Exception {
		assertSame(
				RunResult.OK,
				runAgent("src/test/resources/goal/core/program/modulevarinactionspec.goal"));
	}

	@Test
	public void testPostConditionUpdateVars() throws Exception {
		assertSame(
				RunResult.OK,
				runAgent("src/test/resources/goal/core/program/postconditionvars.goal"));

	}

	@Test
	public void testPostBeliefInModuleVar() throws Exception {
		assertSame(
				RunResult.OK,
				runAgent("src/test/resources/goal/core/program/beliefinmodulevar.goal"));

	}

	@Test
	public void testPostBeliefInAnonymousModule() throws Exception {
		assertSame(RunResult.OK,
				runAgent("src/test/resources/goal/core/program/anonymous.goal"));

	}
	
	@Test
	public void testVarsIntoModuleCall() throws Exception {
		assertSame(RunResult.OK,
				runAgent("src/test/resources/goal/core/program/varsintomodulecall.goal"));

	}
}
