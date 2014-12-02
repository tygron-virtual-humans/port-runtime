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

package goal.tools.mc.core;

import goal.tools.errorhandling.exceptions.KRInitFailedException;
import goal.tools.mc.core.lmhashset.LMHashSet;
import goal.tools.mc.evaluator.goal.GOALEvaluator;
import goal.tools.mc.explorer.tauriainen.Tauriainen;
import goal.tools.mc.program.goal.prolog.GOALPrologProg;
import goal.tools.mc.property.ltl.Formula;
import goal.tools.mc.property.ltl.Negation;
import goal.tools.mc.property.ltl2aut.LTL2AUTProp;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nl.tudelft.goal.messaging.exceptions.MessagingException;

/**
 * Represents the controller component of GOAL's single-agent model checker,
 * which uses LTL2AUT as LTL translation algorithm, and Tauriainen's generalized
 * NDFS algorithm for on-the-fly exploration of the state space. Environments
 * are not taken into consideration.
 *
 * @author sungshik
 *
 */
public class Controller implements Runnable {

	//
	// Private fields
	//

	/**
	 * The property component.
	 */
	private final LTL2AUTProp prop;

	/**
	 * The program component.
	 */
	private GOALPrologProg prog;

	/**
	 * The evaluator component.
	 */
	private final GOALEvaluator eval;

	/**
	 * The exploration component.
	 */
	private final Tauriainen expl;

	//
	// Constructors
	//

	/**
	 * Constructs an instance of <code>Controller</code>.
	 *
	 * @param fString
	 *            - The LTL formula to be verified.
	 * @param pString
	 *            - The full path to the GOAL agent file.
	 */
	public Controller(String fString, String pString) throws Exception {

		/* Initialize property component */
		Formula f;
		try {
			f = Formula.parse(fString);
		} catch (Exception e) {
			throw new Exception("Could not parse the provided property. "
					+ "Please rephrase.");
		}
		f = new Negation(f).nnf();
		prop = new LTL2AUTProp(f);

		/* Initialize program component */
		try {
			prog = new GOALPrologProg(pString, f, this, Directives.POR);
		} catch (Exception e) {
			throw new Exception("Could not parse the selected agent. Please "
					+ "resolve these syntax issue before model checking. " + e);
		}

		/* Initialize evaluator component */
		eval = new GOALEvaluator(prog.getConv());

		/* Print output */
		System.out.println("\n == Model checker == ");
		System.out.println("\nNegated property: " + f.toString());

		/* Handle directives */
		if (!Directives.PROP_ON_THE_FLY) {
			prop.generate();
		}
		if (!Directives.PROG_ON_THE_FLY) {
			prog.generate();
		}
		if (Directives.SLICING) {
			prog.slice();
		}

		/* Initialize explorer */
		expl = new Tauriainen(prog, prop, eval);
	}

	//
	// Public methods
	//

	/**
	 * Gets the search path (starting from the initial state of the product
	 * automaton) that the exploration component currently is traversing.
	 *
	 * @return The current search path as an unordered set.
	 */
	public LMHashSet<State> getFirstSearchPath() {
		return expl.getFirstSearchPath();
	}

	/**
	 * Performs a series of checks to determine whether running the model
	 * checker for the provided property, agent, and directives is opportune.
	 * Specifically, the following checks are performed:
	 * <ul>
	 * <li>Do percept rules occur in the agent?
	 * <li>Do communication primitives occur in the agent?
	 * <li>Do modules occur in the agent, and is PBS or POR enabled?
	 * </ul>
	 *
	 * @return A list of warnings to be displayed to the user.
	 */
	public List<String> preCheck() {

		try {
			ArrayList<String> messages = new ArrayList<String>();

			/* Percept rules */
			if (prog.checkPerceptRules()) {
				messages.add("The agent to be verified contains percept "
						+ "rules, which might not be taken into account during "
						+ "verification (the \"percept(..)\" primitive is not "
						+ "supported). Please note that this may render model "
						+ "checking results meaningless.");
			}

			/* Communication */
			if (prog.checkCommunication()) {
				messages.add("The agent to be verified uses communication "
						+ "primitives, which are not supported. Please note "
						+ "that this may render model checking results "
						+ "meaningless.");
			}

			/* Modules combined with slicing / POR */
			if (prog.checkNestedModules()
					&& (Directives.SLICING || Directives.POR)) {
				messages.add("The agent to be verified contains nested "
						+ "modules. Modules are not supported by the current "
						+ "implementation of slicing and POR. Please note that "
						+ "verification with either of these reduction "
						+ "techniques enabled may render model checking "
						+ " results meaningless in this case.");
			}

			/* Return */
			return messages;
		}

		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void run() {

		try {

			/* Run the model checker */
			expl.start();

			/* Print output */
			System.out.println("\n == Done == ");

			if (listener != null) {
				listener.actionPerformed(new ActionEvent(this, 0, "stop"));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ActionListener listener;

	public void setListener(ActionListener listener) {
		this.listener = listener;
	}

	/**
	 * free up all resources and close.
	 *
	 * @throws MessagingException
	 * @throws KRInitFailedException
	 */
	public void dispose() throws MessagingException, KRInitFailedException {
		prog.dispose();
		prog = null;
	}
}