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

package goal.tools.mc;

import goal.tools.mc.core.Controller;
import goal.tools.mc.core.Directives;
import goal.tools.mc.program.goal.prolog.trans.PrologEquals;
import goal.tools.mc.program.goal.trans.TauAnalyzer;

public class Main {

	public static void main(String[] args) {

		/* Input */
		String s, dir, name, path;
		try {
			String[] input;
			INPUT scenario = null;
			// scenario = INPUT.SECT_7_3_1;
			switch (scenario) {
			case SECT_4_3_1:
				input = sect_4_3_1();
				break;
			case SECT_4_3_2:
				input = sect_4_3_2();
				break;
			case SECT_4_3_3:
				input = sect_4_3_3();
				break;
			case SECT_4_3_4:
				input = sect_4_3_4();
				break;
			case SECT_4_4:
				input = sect_4_4();
				break;
			case SECT_6_4_1:
				input = sect_6_4_1();
				break;
			case SECT_6_4_2:
				input = sect_6_4_2();
				break;
			case SECT_6_4_3:
				input = sect_6_4_3();
				break;
			case SECT_7_3_1:
				input = sect_7_3_1();
				break;
			case SECT_7_3_2:
				input = sect_7_3_2();
				break;
			case CLIMAXI:
				input = climaxi();
				break;
			default:
				throw new Exception();
			}
			s = input[0];
			path = input[1];
		} catch (Exception e) {
			s = "G [ ~ bel(foo) ]";
			// s = "F [ ~ bel(on(X,Y),not(Y=table)) ]";
			// s = "F [ ~ goal(on(X,Y)) ]";
			// s = "F bel(sufficientOranges) ";
			// s = "F bel(sufficientStrawberries) ";
			// s =
			// "G [ ~ bel(added(Q1,strawberry),added(Q2,strawberry),not(Q1=Q2))]";
			s = "G [ ~ bel(current(I),I>10) ]";
			// s = "F bel(current(10))";
			dir = "./src/goal/tools/mc/examples/misc/";
			name = "exampleAgent";
			path = dir + name + ".goal";
		}

		/* Settings */
		Directives.set("PROG_ON_THE_FLY", true);
		Directives.set("PROP_ON_THE_FLY", false);
		Directives.set("PRINT_TREE", false);
		Directives.set("SLICING", false);
		Directives.set("POR", false);
		TauAnalyzer.ample = 2;
		PrologEquals.useThetaEquality();

		/* Run */
		try {
			Controller controller = new Controller(s, path);
			controller.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static enum INPUT {
		SECT_4_3_1,
		SECT_4_3_2,
		SECT_4_3_3,
		SECT_4_3_4,
		SECT_4_4,
		SECT_6_4_1,
		SECT_6_4_2,
		SECT_6_4_3,
		SECT_7_3_1,
		SECT_7_3_2,
		CLIMAXI
	}

	private static String[] sect_4_3_1() {
		String s = "", dir = "", name = "";
		s = "F [ bel(on(ab,table)) ]]";
		dir = "./src/goal/tools/mc/examples/comparison/exp1/goal/";
		name = "10blocks";
		name = "20blocks";
		name = "30blocks";
		name = "45blocks";
		name = "60blocks";
		name = "80blocks";
		name = "100blocks";
		name = "200blocks";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] sect_4_3_2() {
		String s = "", dir = "", name = "";
		s = "F [ bel(on(aasdb,table)) ]]";
		dir = "./src/goal/tools/mc/examples/comparison/exp2/goal/";
		name = "10blocks";
		name = "20blocks";
		name = "30blocks";
		name = "45blocks";
		name = "60blocks";
		name = "80blocks";
		name = "100blocks";
		name = "200blocks";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] sect_4_3_3() {
		String s = "", dir = "", name = "";
		s = "F [ bel(current(10)) ]]";
		dir = "./src/goal/tools/mc/examples/comparison/exp3/";
		name = "counter";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] sect_4_3_4() {
		String s = "", dir = "", name = "";
		s = "F [ bel(current(10)) ]]";
		dir = "./src/goal/tools/mc/examples/comparison/exp4/";
		name = "counter";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] sect_4_4() {
		String s = "", dir = "", name = "";
		s = "F [ ~ bel(on(X,Y),not(Y=table)) ]]";
		dir = "./src/goal/tools/mc/examples/comparison/";
		name = "200blocks";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] sect_6_4_1() {
		String s = "", dir = "", name = "";
		s = "G [ bel(recipe(bananas,Qr),added(bananas,Q),Q=<Qr)]";
		dir = "./src/goal/tools/mc/examples/blender/";
		name = "blenderAgent";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] sect_6_4_2() {
		String s = "", dir = "", name = "";
		s = "G [bel(on(ab,table)) -> [~ bel(on(X,Y),not(Y=table))]]";
		s = "G [bel(current(K),target(T),not(K>T))]";
		s = "[F [G [ bel(current(5)) ]]] & [F [~ goal(counted(X)),bel(counted(X))]]"; // no
																						// slicing
		dir = "./src/goal/tools/mc/examples/blocksCounter";
		name = "aBlocksCounterAgent";
		name = "anotherBlocksCounterAgent";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] sect_6_4_3() {
		String s = "", dir = "", name = "";
		s = "G [ bel(hasarrow) | [ ~ bel(wumpusisalive) ]]";
		s = "G [ [ ~ goal(getoutofthiscave) ] -> bel(hasgold) ]";
		dir = "./src/goal/tools/mc/examples/wumpus/";
		name = "01-Agent007Goldfinder";
		name = "02-bobwonder";
		name = "03-wiwo";
		name = "04-agentpeace";
		name = "05-wumpusagent";
		name = "06-duffy";
		name = "07-safety_first";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] sect_7_3_1() {
		String s = "", dir = "", name = "";
		s = "F bel(filled)";
		s = "G [ bel(ticked(bananas)) -> bel(recipe(bananas,Qr),added(bananas,Qr)) ]";
		s = "G [ bel(ticked(oranges)) -> bel(recipe(oranges,Qr),added(oranges,Qr)) ]";
		dir = "./src/goal/tools/mc/examples/blender/";
		name = "blenderAgent";
		name = "buggyBlenderAgent";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] sect_7_3_2() {
		String s = "", dir = "", name = "";
		s = "F [ ~ bel(on(X,Y),not(Y=table)) ]]";
		dir = "./src/goal/tools/mc/examples/blocksCounterAgent";
		name = "blocksCounterAgent";
		return new String[] { s, dir + name + ".goal" };
	}

	private static String[] climaxi() {
		String s = "", dir = "", name = "";
		s = "G [ ~ bel(position(133,89)) ]"; // cf. G [ ~ bel(position(33,11)) ]
		s = "F [ bel(has(gold)) ] ";
		s = "G [ ~ bel(has(gold)) ] ";
		dir = "./src/goal/tools/mc/examples/wumpus/";
		name = "clima-random";
		name = "clima-smart";
		/* Sojourner; unpublished */
		// s = "F [ ~ bel(garbageAt(X,Y)) ]";
		// s = "F [ bel(not(checking(slots))) & [F bel(checking(slots)) ]]";
		// s = "G [ bel(not(checking(slots))) -> [F bel(checking(slots)) ]]";
		// s = "G [ bel(garbage(r2)) -> [ F bel(not(garbage(r2))) ] ]";
		return new String[] { s, dir + name + ".goal" };
	}
}