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

package goal.tools.mc.program.goal.prolog;

import goal.core.kr.language.Var;
import goal.core.mentalstate.BASETYPE;
import goal.core.program.GOALProgram;
import goal.tools.mc.core.Controller;
import goal.tools.mc.program.goal.GOALProg;
import goal.tools.mc.program.goal.prolog.trans.PrologAtom;
import goal.tools.mc.program.goal.prolog.trans.PrologTauAnalyzer;
import goal.tools.mc.program.goal.trans.MscSet;
import goal.tools.mc.program.goal.trans.TauAnalyzer;
import goal.tools.mc.property.ltl.Formula;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jpl.Atom;
import swiprolog3.engines.SWIQuery;
import swiprolog3.language.PrologSubstitution;
import swiprolog3.language.PrologTerm;
import swiprolog3.language.PrologUpdate;

/**
 * Represents a program component for a single-agent GOAL system in the absence
 * of an environment that is based on the Prolog KRT.
 *
 * @author sungshik
 *
 */
public class GOALPrologProg extends
		GOALProg<PrologTerm, PrologAtom, PrologUpdate> {

	/**
	 * Constructs a {@link GOALPrologProg} object according to the agent file at
	 * the specified path, given the formula under investigation, the controller
	 * that invokes this constructor, and a flag indicating whether partial
	 * order reduction should be applied.
	 *
	 * @param path
	 *            - The path at which the agent file should be found.
	 * @param f
	 *            - The formula under investigation.
	 * @param cont
	 *            - The controller that controls the object to be created.
	 * @param por
	 *            - Flag indicating whether POR should be applied.
	 */
	public GOALPrologProg(String path, Formula negphi, Controller cont,
			boolean por) throws Exception {
		super(path, negphi, cont, por);
	}

	//
	// Public methods
	//

	@Override
	protected TauAnalyzer<PrologTerm, PrologAtom, PrologUpdate> getAnalyzer(
			GOALProgram program, MscSet voc) {
		return new PrologTauAnalyzer(program, voc);
	}

	@Override
	public long getMemoryConsumption() {
		return getPLStats().get("heapused");
	}

	/**
	 * Requests Prolog statistics, and returns those as a HashMap. The returned
	 * statistics are the number of inferences, atoms, functors, predicates,
	 * modules, and used heap.
	 *
	 * @return The statistics.
	 */
	public Map<String, Long> getPLStats() {

		try {

			/* Create variables */
			HashMap<String, Long> map = new HashMap<String, Long>();
			Set<PrologSubstitution> temp;
			Var var;
			String val;

			/* Get inferences */
			temp = SWIQuery.rawquery(new Atom("statistics(inferences,I)"));
			var = temp.iterator().next().getVariables().iterator().next();
			val = temp.iterator().next().get(var).toString();
			map.put("inferences", Long.parseLong(val));

			/* Get atoms */
			temp = SWIQuery.rawquery(new Atom("statistics(atoms,I)"));
			var = temp.iterator().next().getVariables().iterator().next();
			val = temp.iterator().next().get(var).toString();
			map.put("atoms", Long.parseLong(val));

			/* Get functors */
			temp = SWIQuery.rawquery(new Atom("statistics(functors,I)"));
			var = temp.iterator().next().getVariables().iterator().next();
			val = temp.iterator().next().get(var).toString();
			map.put("functors", Long.parseLong(val));

			/* Get predicates */
			temp = SWIQuery.rawquery(new Atom("statistics(predicates,I)"));
			var = temp.iterator().next().getVariables().iterator().next();
			val = temp.iterator().next().get(var).toString();
			map.put("predicates", Long.parseLong(val));

			/* Get modules */
			temp = SWIQuery.rawquery(new Atom("statistics(modules,I)"));
			var = temp.iterator().next().getVariables().iterator().next();
			val = temp.iterator().next().get(var).toString();
			map.put("modules", Long.parseLong(val));

			/* Get heap */
			temp = SWIQuery.rawquery(new Atom("statistics(heapused,I)"));
			var = temp.iterator().next().getVariables().iterator().next();
			val = temp.iterator().next().get(var).toString();
			map.put("heapused", Long.parseLong(val));

			/* Return */
			return map;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Prints Prolog statistics to console. The printed statistics are the
	 * number of inferences, atoms, functors, predicates, modules and used heap.
	 */
	public void printPLStatistics() {
		getMentalState().getOwnBase(BASETYPE.BELIEFBASE).showStatistics();
	}

}
