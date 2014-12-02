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

package goal.tools.mc.examples;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BlocksWorldGenerator {

	public static void main(String[] args) {
		int n = 6;
		// int[] nArray = {5,10,20,30,45,60,80,100};
		// for (int i = 0; i < nArray.length; i++) {
		// for (n = 4; n < 21; n++) {
		// n = nArray[i];
		Scenario bb = Scenario.TWO_EQUAL_TOWERS;
		Scenario gb = Scenario.ALL_ON_TABLE;
		BlocksWorldGenerator gen = new BlocksWorldGenerator(n, bb, gb);
		String path = "./src/goal/tools/mc/examples/misc/";
		String file = path + "blocksToTableAgent" + n + ".goal";
		gen.generateGOAL_AOT_Propositional(file);
		// gen.generate(path, n + "blocks");
		// gen.analyze(file + ".info");
		// }
	}

	/* Class variables */

	private final int n;
	private final List<Block> blocks;
	private final List<Block> bb;
	private final List<Block> gb;
	private final Scenario bbScenario;
	private final Scenario gbScenario;
	private String GOAL;
	private String MAUDE;
	private String MCAPL;
	private final String[] reserved = { "bb", "gb", "is" };

	public static enum Scenario {
		RANDOM, ALL_ON_TABLE, ONE_TOWER, ONE_SMALL_TOWER, TWO_EQUAL_TOWERS
	}

	/* Public methods */

	/**
	 * Creates a Blocks World generator, and generates the Blocks World,
	 * according to the specified belief-base and goal-base scenario. A boolean
	 * flag represents whether a propositional program is required or not.
	 *
	 * @param n
	 *            - The number of blocks in the blocks world.
	 * @param bbScenario
	 *            - The scenario of the belief-base.
	 * @param gbScenario
	 *            - The scenario of the goal-base.
	 * @param propositional
	 *            - Boolean flag indicating whether the program should be
	 *            propositional or not.
	 */
	public BlocksWorldGenerator(int n, Scenario bbScenario, Scenario gbScenario) {
		if (n > 26 * 26 || n < 1) {
			System.err.println("[ERROR] n should be between 1 and 675");
			System.exit(0);
		}
		this.n = n;
		this.blocks = createBlocks(this.n);
		this.bbScenario = bbScenario;
		this.gbScenario = gbScenario;
		this.bb = applyScenario(bbScenario);
		this.gb = applyScenario(gbScenario);
	}

	public void generate(String dir, String name) {
		String path = dir + name;
		generateGOAL_AOT(path + ".goal");
		analyze(dir + name + ".info");
		System.exit(0);
		if (bbScenario == Scenario.ONE_SMALL_TOWER
				&& gbScenario == Scenario.ALL_ON_TABLE) {
			generateGOAL_AOT(path + ".goal");
			generateMAUDE_AOT(path + ".maude");
			generateMCAPL_AOT(path + ".mcapl");
		} else {
			generateGOAL(path + ".goal");
		}
		analyze(dir + name + ".info");
	}

	public void generateGOAL(String file) {

		/* Knowledge */
		String output = "";
		output += "main: ag\n" + "{\n" + "	knowledge {\n"
				+ "		clear(X) :- block(X) , not(on(Y,X)) .\n"
				+ "		clear(table) .\n" + "		tower([X]) :- on(X,table) .\n"
				+ "		tower([X,Y|T]) :- on(X,Y) , tower([Y|T]) .\n";
		for (Block b : blocks) {
			output += "		" + b.toString("block") + " .\n";
		}
		output += "	}\n";

		/* Beliefs */
		output += "	beliefs{\n";
		for (Block b : bb) {
			output += "		" + b.toString("on") + " .\n";
		}
		output += "	}\n";

		/* Goals */
		output += "	goals{\n";
		output += "		";
		for (Block b : gb) {
			output += b.toString("on") + " , ";
		}
		output = output.substring(0, output.length() - 2);
		output += ".\n";
		output += "	}\n";

		/* Program, actions, percepts */
		output += "	program {\n"
				+ "		if a-goal(tower([X|T])) then move(X,table) .\n"
				+ "		if bel(tower([Y|T])) , a-goal(tower([X,Y|T])) then move(X,Y) .\n"
				+ "	}\n"
				+ "	actionspec {\n"
				+ "		move(X,Y){\n"
				+ "			pre{ clear(X) , on(X,Z) , clear(Y) , not(on(X,Y)) , not(X=Y) }\n"
				+ "			post{ not(on(X,Z)), on(X,Y) }\n" + "		}\n" + "	}\n"
				+ "	perceptrules {\n" + "		if bel(foo) then insert(bar).\n"
				+ "	}\n" + "}";

		/* Write to file */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
			System.out.println("GOAL agent generated, and stored as " + file);
			GOAL = output;
		} catch (Exception e) {
		}
	}

	public void generateGOAL_AOT(String file) {

		/* Knowledge */
		String output = "";
		output += "main: ag\n" + "{\n" + "	knowledge {\n";
		for (Block b : blocks) {
			output += "		" + b.toString("block") + " .\n";
		}
		output += "	}\n";

		/* Beliefs */
		output += "	beliefs{\n";
		for (Block b : bb) {
			output += "		" + b.toString("on") + " .\n";
			boolean clear = true;
			for (Block b2 : bb) {
				clear = clear && (b2.onTable || b2.on != b);
			}
			if (clear) {
				output += "		clear(" + b.name + ") .\n";
			}
		}
		output += "	}\n";

		/* Goals */
		output += "	goals{\n";
		// output += "		on(ac,table) .\n";
		output += "		";
		for (Block b : gb) {
			output += b.toString("on") + " , ";
		}
		output = output.substring(0, output.length() - 2);
		output += ".\n";
		output += "	}\n";

		/* Program, actions, percepts */
		output += "	program {\n"
				+
				// "		if goal(on(ab,table)) , bel(on(X,Y)) , bel(clear(X)) then moveXfromYtoTable(X,Y) .\n"
				// +
				// "		if goal(on(ac,table)) , bel(on(X,Y)) , bel(clear(X)) then moveXfromYtoTable(X,Y) .\n"
				// +
				"		if goal(on(X,table)) , bel(on(X,Y)) , bel(clear(X)) then moveXfromYtoTable(X,Y) .\n"
				+ "	}\n" + "	actionspec {\n" + "		moveXfromYtoTable(X,Y) {\n"
				+ "			pre {block(Y)}\n"
				+ "			post {not(on(X,Y)) , on(X,table) , clear(Y)}" + "\n"
				+ "		}\n" + "	}\n" + "	perceptrules {\n"
				+ "		if bel(foo) then insert(bar).\n" + "	}\n" + "}";

		/* Write to file */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
			System.out.println("GOAL agent generated, and stored as " + file);
			GOAL = output;
		} catch (Exception e) {
		}
	}

	public void generateGOAL_AOT_Propositional(String file) {

		/* Knowledge */
		String output = "";
		output += "main: ag\n" + "{\n" + "	knowledge {\n" + "	}\n";

		/* Beliefs */
		output += "	beliefs {\n";
		for (Block b : bb) {
			output += "		" + b.toString("on") + " .\n";
			boolean clear = true;
			for (Block b2 : bb) {
				clear = clear && (b2.onTable || b2.on != b);
			}
			if (clear) {
				output += "		clear(" + b.name + ") .\n";
			}
		}
		output += "	}\n";

		/* Goals */
		output += "	goals {\n";
		// output += "		on(ac,table) .\n";
		output += "		";
		for (Block b : gb) {
			output += b.toString("on") + " , ";
		}
		output = output.substring(0, output.length() - 2);
		output += ".\n";
		output += "	}\n";

		String programrules = "";
		for (Block block1 : blocks) {
			String b1 = block1.name;
			for (Block block2 : blocks) {
				String b2 = block2.name;
				// String programrule = "		if goal(on(" + b1 +
				// ",table)) , bel(clear(" + b1 + ")) , bel(on(" + b1 + "," + b2
				// + ")) then moveXfromYtoTable(" + b1 + "," + b2 + ") . \n";
				String programrule = "		if bel(clear(" + b1 + ")) , bel(on("
						+ b1 + "," + b2 + ")) then moveXfromYtoTable(" + b1
						+ "," + b2 + ") . \n";
				programrules += programrule;
			}
		}

		/* Program, actions, percepts */
		output += "	program {\n" + programrules + "	}\n" + "	actionspec {\n"
				+ "		moveXfromYtoTable(X,Y) {\n" + "			pre {true}\n"
				+ "			post {not(on(X,Y)) , on(X,table) , clear(Y)}" + "\n"
				+ "		}\n" + "	}\n" + "	perceptrules{\n"
				+ "		if bel(foo) then insert(bar).\n" + "	}\n" + "}";

		/* Write to file */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
			System.out
			.println("Propositional GOAL agent generated, and stored as "
					+ file);
			GOAL = output;
		} catch (Exception e) {
		}
	}

	public void generateMAUDE(String file) {

		/* Header */
		String output = "";
		output += "in model-checker.maude\n"
				+ "in querylanguage2.maude\n"
				+ "in additionals.maude\n"
				+ "in goal-sem.maude\n"
				+ "mod BLOCK-GOAL is\n"
				+ "	protecting SYNTACTICAL-DEFS .\n"
				+ "	protecting GOAL-PREDS .\n"
				+ "	sort Block Tower .\n"
				+ "	subsort Block < Tower .\n"
				+ "	subsorts GroundTerm < Block . subsort GroundTermList <  Tower .\n"
				+ "	vars X Y Z ";
		String X3_Xnplus4 = "";
		for (int i = 1; i < n + 4; i++) {
			output += "X" + i + " ";
			if (i >= 3) {
				X3_Xnplus4 += "X" + i;
				if (i != n + 3) {
					X3_Xnplus4 += ", ";
				}
			}
		}
		output += ": Block .\n" + "	vars BB BB' GB U G G' : BeliefBase .\n"
				+ "	vars R R' : Tower .\n" + "	vars S S' : Substitution .\n"
				+ "	var LS : ListSubstitution .\n" + "	ops ";
		for (Block b : blocks) {
			output += b.name + " ";
		}
		output += "table : -> Block .\n"
				+ "	op _,_ : Tower Tower -> Tower [ctor ditto] .\n"
				+ "	op on : Block Block -> Belief .\n"
				+ "	op clear : Block Block -> Belief .\n"
				+ "	op tower : Tower -> Belief .\n" + "	\n";

		/* Initial belief base */
		output += "	eq bb = ";
		for (Block b : bb) {
			output += b.toString("on") + "; ";
		}
		output += "on(table, table) .\n" + "	\n";

		/* Initial goal base */
		output += "	ops g1 : -> BeliefBase . op gb : -> GoalBase .\n"
				+ "	eq g1 = ";
		for (Block b : gb) {
			output += b.toString("on") + "; ";
		}
		output += "on(table, table) .\n" + "	eq gb = g1 .\n" + "	\n";

		/* Knowledge base */
		output += "	op kb : Tower -> KnowledgeBase .\n"
				+
				// "	eq kb((X, (Y, R))) = (clear(X) :- (~ on(Y, X)));\n" +
				// "		(tower((X)) :- (on(X, table)));\n" +
				// "		(tower((X, Y, R)) :- (on(X, Y) /\\ tower((Y, R)))) .\n" +
				"	op towerS : Tower Substitution -> Query .\n"
				+ "	crl [step0.1] : solve1(towerS((X), S), U) => sol(top, S)\n"
				+ "		if allMatches(on(X , table), U, U) == none .\n"
				+ "	crl [step0.2] : solve1(towerS((X), S), U) => sol(top, S ; S')\n"
				+ "		if (S' & LS) := allMatches(on(X , table), U, U) /\\ S' =/= none .\n"
				+ "	rl [step1.1] : solve1(towerS((table, R), S), U) => sol(top, S) .\n"
				+ "	crl [step1.2] : solve1(towerS((X, R), S), U) => solve1(towerS((downTerm(substitute(upTerm(R), S'), 'err.['Tower])), S ; S'), U)\n"
				+ "		if (Y, R') := R /\\ (S' & LS) := allMatches(on(X , Y), U, U) /\\ S' =/= none .\n"
				+ "	crl [step1.3] : solve1(towerS((X, R), S), U) => solve1(towerS(R, S), U)\n"
				+ "		if (Y, R') := R /\\ allMatches(on(X , Y), U, U) == none .\n"
				+ "	crl [step1.11] : solve1(towerS((X, Y, R), S), U) => deadBlock if allMatches(on(X , Y), U, U) == noMatch .\n"
				+ "	rl [clear] : solve1(clear(table, X1), U) => sol(top, none) .\n"
				+ "	crl [clear] : solve1(clear(X, X1), U) => sol(top, none) if allMatches(on(X1,X), U, U) == noMatch .\n"
				+ "	crl [on] : solve1(on(X, Y), U) => sol(top, none) if matches(on(X, Y), U) == none .\n"
				+ "	crl [on] : solve1(on(X, Z), U) => sol(top, S) if (S & LS) := allMatches(on(X,Z), U, U) /\\ S =/= none .\n"
				+ "	\n";

		/* Basic actions */
		output += "	op move : Block Block Block Block Block -> B-Action .\n"
				+ "	eq [b-act] : move(X, Z, Y, X1, X2) = [~ on(X,Y) , neg on(X,Z) ;; on(X,Y)] .\n";

		/* Conditional actions */
		output += "	ops c c1 : Tower BeliefBase -> C-Action .\n"
				+ "	ops c2 : Tower BeliefBase -> C-Action .\n"
				+ "	eq [c-act] : c1((X, Y, Z, X1, X2, " + X3_Xnplus4
				+ "), GB) =\n" + "		{a-goal(towerS((X, Y, " + X3_Xnplus4
				+ "), none)) /\\ bel(towerS((Y, " + X3_Xnplus4 + "), none))\n"
				+ "		/\\ bel(clear(X, X1))\n" + "		/\\ bel(clear(Y, X1))\n"
				+ "		/\\ bel(on(X,Z))\n" + "		} do(move(X, Z, Y, X1, X2)) .\n"
				+ "	eq [c-act] : c2((X, Z, X1, X2, " + X3_Xnplus4
				+ "), GB) =\n" + "		{a-goal(towerS((X, " + X3_Xnplus4
				+ "), none))\n" + "		/\\ bel(clear(X, X1))\n"
				+ "		/\\ bel(on(X,Z))\n"
				+ "		} do(move(X, Z, table, X1, X2)) .\n" + "	\n";

		/* Initial mental state */
		output += "	ops lb lb2 lb3 lb4 : -> GMentalState .\n"
				+ "	eq [by2] : lb2 = << bLabel(empty), bb, gb  >> .\n" + "	\n";

		/* Properties */
		output += "	ops termin1 termin2 termin3 p1 p2 p3 : -> Prop .\n"
				+ "	eq << bLabel('removed-goal), BB, GB:GoalBase >> |= termin1 = true .\n"
				+ "	eq << bLabel('remove-last-goal), BB, empty >> |= termin2 = true .\n"
				+ "endm\n" + "\n";

		/* Rewrite */
		output += "set verbose on .\n" + "red modelCheck(lb2, <> termin2) .\n";

		/* Write to file */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
			System.out.println("MAUDE agent generated, and stored as " + file);
			MAUDE = output;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateMAUDE_AOT(String file) {

		/* Static */
		String output = "";
		output += "in model-checker.maude\n"
				+ "in querylanguage2.maude\n"
				+ "in additionals.maude\n"
				+ "in goal-sem.maude\n"
				+ "mod BLOCK-GOAL is\n"
				+ "	protecting SYNTACTICAL-DEFS .\n"
				+ "	protecting GOAL-PREDS .\n"
				+ "	sort Block .\n"
				+ "	subsorts GroundTerm < Block .\n"
				+ "	vars X Y Z : Block .\n"
				+ "	vars BB GB U : BeliefBase .\n"
				+ "	vars S : Substitution .\n"
				+ "	var LS : ListSubstitution .\n"
				+ "	op on : Block Block -> Belief .\n"
				+ "	op clear : Block -> Belief .\n"
				+ "	op block : Block -> Belief .\n"
				+ "	crl [on] : solve1(on(X, Y), U) => sol(top, none) if matches(on(X, Y), U) == none .\n"
				+ "	crl [on] : solve1(on(X, Z), U) => sol(top, S) if (S & LS) := allMatches(on(X,Z), U, U) /\\ S =/= none .\n"
				+ "	crl [clear] : solve1(clear(X), U) => sol(top, none) if matches(clear(X), U) == none .\n"
				+ "	crl [clear] : solve1(clear(X), U) => sol(top, S) if (S & LS) := allMatches(clear(X), U, U) /\\ S =/= none .\n"
				+ "	crl [block] : solve1(block(X), U) => sol(top, none) if matches(block(X), U) == none .\n"
				+ "	crl [block] : solve1(block(X), U) => sol(top, S) if (S & LS) := allMatches(block(X), U, U) /\\ S =/= none .\n"
				+ "	op move : Block Block -> B-Action .\n"
				+ "	eq [b-act] : move(X, Z) = [block(Z) , neg on(X,Z) ;; on(X,table) ;; clear(Z)] .\n"
				+ "	op moveConditional : Block Block -> C-Action .\n"
				+ "	eq [c-act] : moveConditional(X, Z) = {goal(on(ac,table)) /\\ bel(on(X,Z)) /\\ bel(clear(X))} do(move(X,Z)) .\n"
				+ "	op lb2 : -> GMentalState .\n"
				+ "	eq [by2] : lb2 = << bLabel(empty), bb, gb  >> .\n"
				+ "	ops property : -> Prop .\n"
				+ "	ceq << L:Label, BB, GB >> |= property = true if matches(on(ab,table), BB) == none .\n";

		/* Dynamic */
		output += "	ops ";
		for (Block b : blocks) {
			output += b.name + " ";
		}
		output += "table : -> Block . \n";
		String bels = "	eq bb = ";
		for (int i = 0; i < bb.size(); i++) {
			Block b = bb.get(i);
			bels += b.toString("on") + " ; " + "block(" + b.name + ") ; ";
			if (i == bb.size() - 1 || bb.get(i + 1).on != b) {
				bels += "clear(" + b.name + ") ; ";
			}
		}
		bels = bels.substring(0, bels.length() - 2);
		output += bels + " .\n";
		output += "	op gb : -> GoalBase .\n" + "	op g1 : -> BeliefBase .\n"
				+ "	eq g1 = on(ac,table) .\n" + "	eq gb = g1 .\n";

		// String ops = "	ops ";
		// String cacts = "";
		// for (Block block1 : blocks) {
		// String b1 = block1.name;
		// for (Block block2 : blocks) {
		// String b2 = block2.name;
		// String action = "move" + b1 + "from" + b2 + "totable";
		// ops += action + " ";
		// cacts += "	eq [c-act] : " + action +
		// " = {goal(on(ab,table)) /\\ bel(on(" + b1 + "," + b2 +
		// ")) /\\ bel(clear(" + b1 + "))} do(move(" + b1 + "," + b2 + ")) .\n";
		// }
		// }
		// ops += ": -> C-Action .\n";
		// output += ops + cacts;

		/* Finalize */
		output += "endm\n"
				+ "*** set profile off . set profile on . rew lb2 . show profile . set profile off .\n"
				+ "set verbose on .\n" + "red modelCheck(lb2, <> property) .\n";

		/* Write to file */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
			System.out
			.println("Propositional MAUDE agent generated, and stored as "
					+ file);
			MAUDE = output;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateMAUDE_AOT_Propositional(String file) {

		/* Static */
		String output = "";
		output += "in model-checker.maude\n"
				+ "in querylanguage2.maude\n"
				+ "in additionals.maude\n"
				+ "in goal-sem.maude\n"
				+ "mod BLOCK-GOAL is\n"
				+ "	protecting SYNTACTICAL-DEFS .\n"
				+ "	protecting GOAL-PREDS .\n"
				+ "	sort Block .\n"
				+ "	subsorts GroundTerm < Block .\n"
				+ "	vars X Y Z : Block .\n"
				+ "	vars BB GB U : BeliefBase .\n"
				+ "	op on : Block Block -> Belief .\n"
				+ "	op clear : Block -> Belief .\n"
				+ "	crl [on] : solve1(on(X, Y), U) => sol(top, none) if matches(on(X, Y), U) == none .\n"
				+ "	crl [clear] : solve1(clear(X), U) => sol(top, none) if matches(clear(X), U) == none .\n"
				+ "	op move : Block Block -> B-Action .\n"
				+ "	eq [b-act] : move(X, Z) = [top , neg on(X,Z) ;; on(X,table) ;; clear(Z)] .\n"
				+ "	op lb2 : -> GMentalState .\n"
				+ "	eq [by2] : lb2 = << bLabel(empty), bb, gb  >> .\n"
				+ "	ops property : -> Prop .\n"
				+ "	ceq << L:Label, BB, GB >> |= property = true if matches(on(ab,table), BB) == none .\n";

		/* Dynamic */
		output += "	ops ";
		for (Block b : blocks) {
			output += b.name + " ";
		}
		output += "table : -> Block . \n";
		String bels = "	eq bb = ";
		for (int i = 0; i < bb.size(); i++) {
			Block b = bb.get(i);
			bels += b.toString("on") + " ; ";
			if (i == bb.size() - 1 || bb.get(i + 1).on != b) {
				bels += "clear(" + b.name + ") ; ";
			}
		}
		bels = bels.substring(0, bels.length() - 2);
		output += bels + " .\n";
		output += "	op gb : -> GoalBase .\n" + "	op g1 : -> BeliefBase .\n"
				+ "	eq g1 = on(ac,table) .\n" + "	eq gb = g1 .\n";
		// for (int i = 0; i < gb.size(); i++) {
		// Block b = gb.get(i);
		// output += b.toString("on") + " ; ";
		// if (i == gb.size() - 1 || gb.get(i + 1).on != b) {
		// output += "clear(" + b.name + ") ; ";
		// }
		// }
		// output += " .\n";

		String ops = "	ops ";
		String cacts = "";
		for (Block block1 : blocks) {
			String b1 = block1.name;
			for (Block block2 : blocks) {
				String b2 = block2.name;
				String action = "move" + b1 + "from" + b2 + "totable";
				ops += action + " ";
				cacts += "	eq [c-act] : " + action
						+ " = {goal(on(ac,table)) /\\ bel(on(" + b1 + "," + b2
						+ ")) /\\ bel(clear(" + b1 + "))} do(move(" + b1 + ","
						+ b2 + ")) .\n";
			}
		}
		ops += ": -> C-Action .\n";
		output += ops + cacts;

		/* Finalize */
		output += "endm\n"
				+ "*** set profile off . set profile on . rew lb2 . show profile . set profile off .\n"
				+ "set verbose on .\n" + "red modelCheck(lb2, <> property) .\n";

		/* Write to file */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
			System.out
			.println("Propositional MAUDE agent generated, and stored as "
					+ file);
			MAUDE = output;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateMCAPL_AOT(String file) {

		/* Header */
		String output = "";
		output += ":name: org\n" + ":Brute Facts: \n" + ":Effect Rules: \n"
				+ ":CountsAs Rules: \n" + ":Sanction Rules: \n" + "GOAL\n"
				+ ":name: ag\n" + ":Initial Goals: on(ac,table) \n"
				+ ":Initial Beliefs: \n";

		/* Initial beliefs */
		List<Block> clearBlocks = new ArrayList<Block>(bb);
		for (Block b : bb) {
			output += "	" + b.toString("on") + "\n";
			if (!b.onTable) {
				clearBlocks.remove(b.on);
			}
		}
		for (Block b : clearBlocks) {
			output += "	clear(" + b.name + ")\n";
		}
		for (Block b : bb) {
			output += "	block(" + b.name + ")\n";
		}

		/*
		 * Initial capabilities and conditional actions (and a hack to detect
		 * whether all blocks are on the table)
		 */
		// String condacts = "";
		// for (Block block1 : blocks) {
		// String b1 = block1.name;
		// for (Block block2 : blocks) {
		// String b2 = block2.name;
		// String condact = "	G on(ac,table) , B clear(" + b1 + ") , B on(" + b1
		// + "," + b2 + ") |> do(moveXfromYtoTable(" + b1 + "," + b2 + "))\n";
		// condacts += condact;
		// }
		// }
		output += ":Capabilities: \n" +
				// "	moveXfromYtoTable(X,Y) moveXfromYtoTable(X,Y) {True} {-on(X,Y) , on(X,table) , clear(Y)}\n";
				"	moveXfromYtoTable(X,Y) moveXfromYtoTable(X,Y) {B block(Y)} {-on(X,Y) , on(X,table) , clear(Y)}\n";
		// output += ":Conditional Actions: \n" + condacts;
		output += ":Conditional Actions: \n"
				+ "	G on(ac,table) , B on(X,Y) , B clear(X) |> do(moveXfromYtoTable(X,Y))";

		/* Write to file */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
			System.out.println("MCAPL agent generated, and stored as " + file);
			MCAPL = output;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void generateBlocksCounter(String file) {
		/* Knowledge */
		String output = "";
		output += "main: ag\n" + "{\n" + "	knowledge {\n";
		for (Block b : blocks) {
			output += "		" + b.toString("block") + " .\n";
		}
		output += "	}\n";

		/* Beliefs */
		output += "	beliefs{\n" + "		current(0).\n";
		for (Block b : bb) {
			output += "		" + b.toString("on") + " .\n";
			boolean clear = true;
			for (Block b2 : bb) {
				clear = clear && (b2.onTable || b2.on != b);
			}
			if (clear) {
				output += "		clear(" + b.name + ") .\n";
			}
		}
		output += "	}\n";

		/* Goals */
		output += "	goals{\n		";
		for (Block b : blocks) {
			output += "on(" + b.name + ",table) , ";
		}
		output = output.substring(0, output.length() - 2) + ".\n";
		output += "	}\n";

		/* Program, actions, percepts */
		output += "	program {\n"
				+
				// "		if a-goal(on(X,table)) , bel(on(X,Y)) , bel(clear(X)) then moveXfromYtoTable(X,Y) .\n"
				// +
				// "		if bel(current(J) , K is J+1 , on(X,table) , not(counted(X))) then updateCurrent(J,K) + insert(counted(X)) .\n"
				// +
				"		if a-goal(on(X,table)) , bel( on(X,Y), clear(X), block(Y) ) then insert(on(X,table), clear(Y)) + delete(on(X,Y)) + increment(X).\n"
				+ "	}\n"
				+ "	actionspec {\n"
				+
				// "		moveXfromYtoTable(X,Y) {\n" +
				// "			pre {block(Y)}\n" +
				// "			post {not(on(X,Y)) , on(X,table) , clear(Y)}" + "\n" +
				// "		}\n" +
				// "		updateCurrent(J,K) {\n" +
				// "			pre { true }\n" +
				// "			post { not(current(J)) , current(K) }\n" +
				// "		}\n"+
				"		increment(X) {\n" + "			pre{ current(J), K is J + 1 }\n"
				+ "			post{ not(current(J)), current(K) , moved(X,K) }\n"
				+ "		}\n" + "	}\n" + "}";

		/* Write to file */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
			System.out.println("GOAL agent generated, and stored as " + file);
			GOAL = output;
		} catch (Exception e) {
		}
	}

	/**
	 * Analyzes the generated Blocks World instance, and outputs this both as a
	 * file and to the console.
	 *
	 * @param file
	 *            - The file to write the analysis to.
	 */
	public void analyze(String file) {
		String output = "";
		output += "Belief Base scenario: " + bbScenario + "\n";
		output += "Goal Base scenario: " + gbScenario + "\n";
		int[] heights = new int[blocks.size()];
		int index = 0;
		int count = 1;
		for (int i = 0; i < bb.size(); i++) {
			Block b = bb.get(i);
			if (b.onTable) {
				heights[index] = count;
				index++;
				count = 1;
			} else {
				count++;
			}
		}
		output += "Tower heights: ";
		heights[index] = count;
		int stacked = 0;
		int configurations = 1;
		for (int i = 1; i < heights.length; i++) {
			if (heights[i] > 0) {
				output += heights[i] + " ";
				stacked += heights[i] - 1;
				configurations = configurations * heights[i];
			}
		}
		output += "\nStacked blocks: " + stacked;
		output += "\nStates: " + configurations;

		/* Write to file */
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(output);
			out.close();
			System.out.println(output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printGOAL() {
		if (GOAL != null) {
			System.out.println(GOAL);
		}
	}

	public void printMAUDE() {
		if (MAUDE != null) {
			System.out.println(MAUDE);
		}
	}

	public void printMCAPL() {
		if (MCAPL != null) {
			System.out.println(MCAPL);
		}
	}

	/* Private methods */

	private List<Block> createBlocks(int n) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (int i = 97; i < 97 + 26; i++) {
			for (int j = 97; j < 97 + 26; j++) {
				Block b = new Block(Character.toString((char) i)
						+ Character.toString((char) j));
				boolean valid = true;
				for (String element : reserved) {
					if (b.name.equals(element)) {
						valid = false;
					}
				}
				if (valid) {
					blocks.add(b);
				}
				if (blocks.size() == n) {
					return blocks;
				}
			}
		}
		return blocks;
	}

	private List<Block> copyBlocks() {
		ArrayList<Block> clone = new ArrayList<Block>();
		for (Block b : blocks) {
			clone.add(new Block(b.name));
		}
		return clone;
	}

	private List<Block> applyScenario(Scenario scenario) {
		List<Block> xbase = new ArrayList<Block>();
		switch (scenario) {
		case RANDOM:
			xbase = applyRandom();
			break;
		case ALL_ON_TABLE:
			xbase = copyBlocks();
			for (Block b : xbase) {
				b.on = null;
				b.onTable = true;
			}
			break;
		case ONE_TOWER:
			xbase = copyBlocks();
			for (int i = 0; i < xbase.size(); i++) {
				Block b = xbase.get(i);
				if (i == 0) {
					b.on = null;
					b.onTable = true;
				} else {
					b.on = xbase.get(i - 1);
					b.onTable = false;
				}
			}
			break;
		case ONE_SMALL_TOWER:
			xbase = copyBlocks();
			if (blocks.size() > 3) {
				for (int i = 0; i < xbase.size(); i++) {
					Block b = xbase.get(i);
					if (i != 0 && i <= 3) {
						b.on = xbase.get(i - 1);
						b.onTable = false;
					} else {
						b.on = null;
						b.onTable = true;
					}
				}
			} else {
				System.err.println("[ERROR] n should be at least 4 for the "
						+ "ONE_SMALL_TOWER scenario.");
				System.exit(0);
			}
			break;
		case TWO_EQUAL_TOWERS:
			xbase = copyBlocks();
			if (blocks.size() % 2 == 0) {
				for (int i = 0; i < xbase.size() / 2; i++) {
					Block b = xbase.get(i);
					if (i != 0) {
						b.on = xbase.get(i - 1);
						b.onTable = false;
					} else {
						b.on = null;
						b.onTable = true;
					}
				}
				for (int i = xbase.size() / 2; i < xbase.size(); i++) {
					Block b = xbase.get(i);
					if (i != xbase.size() / 2) {
						b.on = xbase.get(i - 1);
						b.onTable = false;
					} else {
						b.on = null;
						b.onTable = true;
					}
				}
			} else {
				System.err.println("[ERROR] n must be even for the "
						+ "TWO_EQUAL_TOWERS scenario.");
				System.exit(0);
			}
			break;
		}
		return xbase;
	}

	private List<Block> applyRandom() {
		ArrayList<Block> xbase = new ArrayList<Block>();
		Collections.shuffle(blocks);
		Random random = new Random();
		int nPartitions = random.nextInt(blocks.size());
		while (nPartitions == 0) {
			nPartitions = random.nextInt(blocks.size());
		}
		int[] partitionSizes = new int[nPartitions];
		int remaining = blocks.size();
		int index = 0;
		while (remaining > 0) {
			if (random.nextBoolean() || partitionSizes[index] == 0) {
				partitionSizes[index]++;
				remaining--;
			}
			index = (index + 1) % nPartitions;
		}
		index = 0;
		for (int i = 0; i < nPartitions; i++) {
			for (int j = 0; j < partitionSizes[i]; j++) {
				Block b = new Block(blocks.get(index).name);
				if (j == 0) {
					b.onTable = true;
				} else {
					b.on = xbase.get(xbase.size() - 1);
				}
				xbase.add(b);
				index++;
			}
		}
		return xbase;
	}

	/* Private classes */

	private class Block {

		String name;
		Block on;
		boolean onTable;

		public Block(String s) {
			name = s;
		}

		public String toString(String function) {
			if (function.equals("block")) {
				return "block(" + name + ")";
			}
			if (function.equals("on")) {
				return "on(" + name + "," + (onTable ? "table" : on.name) + ")";
			}
			return null;
		}
	}
}
