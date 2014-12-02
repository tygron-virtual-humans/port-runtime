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

package goal.tools.mc.explorer.tauriainen;

import goal.tools.mc.core.State;

import java.util.Iterator;

/**
 * Provides a series of static methods for reporting about the exploration
 * process.
 * 
 * @author sungshik.
 *
 */
public class Reporter {

	//
	// Public fields
	//

	/**
	 * The interval at which statistics need be reported.
	 */
	public static long interval = 10000;

	/**
	 * The next time point at which statistics need be reported.
	 */
	public static long next = System.currentTimeMillis() + interval;

	//
	// Public methods
	//

	/**
	 * Prints the specified counterexample.
	 * 
	 * @param ce
	 *            - The counterexample to be printed.
	 */
	public static void printCounterexample(Counterexample ce) {

		try {

			/* Print output */
			System.out.println("\n[COUNTEREXAMPLE] Length: " + ce.getLength());
			System.out.print("\n Path to ");
			switch (ce.cyc.size()) {
			case 0:
				System.out.println("violation\n");
				break;
			case 1:
				System.out.println("stuttering state\n");
				break;
			default:
				System.out.println("cycle\n");
				break;
			}
			System.out.println(" 1:");

			/* Counter */
			int count = 1;

			/* Print prefix */
			for (State q : ce.pre.keySet()) {
				if (count > 1) {
					System.out.println(count % 5 == 0 ? " " + count + ":"
							: " -- ");
				}
				System.out.println(q.toString(4));
				System.out.println("    Next action: "
						+ (count == 1 ? "dummy-action" : ce.pre.get(q)));
				count++;
			}

			/* Return if no cycle exists */
			if (ce.cyc.isEmpty()) {
				return;
			}

			/* Print cycle */
			System.out.println("\n "
					+ (ce.cyc.size() == 1 ? "Stuttering state" : "Cycle head")
					+ "\n");
			System.out.println(" " + count + ":");
			Iterator<State> iter = ce.cyc.keySet().iterator();
			State head = iter.next();
			System.out.println(head.toString(4));
			System.out.println("    Next action: " + ce.cyc.get(head));
			if (ce.cyc.size() > 2) {
				int headIndex = count;
				count++;
				System.out.println("\n         Cycle body\n");
				while (iter.hasNext()) {
					State q = iter.next();
					if (iter.hasNext()) {
						System.out.println(count % 5 == 0 ? " " + count + ":"
								: " -- ");
						System.out.println(q.toString(12));
						System.out.println("    Next action: " + ce.cyc.get(q));
						count++;
					}
				}
				System.out.println("\n Cycle tail (= cycle head)\n");
				System.out.println(" " + count + " = " + headIndex + ":");
				System.out.println(head.toString(4));
			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reports on the current state of the search. That is, the number of stored
	 * states is printed, as well as statistics on research consumption.
	 */
	public static void printReport(int nProdStates, int nProgStates,
			Statistics stats) {

		System.out.println("\n[REPORT] Stored " + nProdStates
				+ " product states (1 dummy), computed " + nProgStates
				+ " program states (1 dummy)");
		printResourceConsumption(stats);
	}

	/**
	 * Prints the memory consumption and time elapsed since the start of the
	 * search. Assumes that memory consumption is monotonically rising.
	 */
	public static void printResourceConsumption(Statistics stats) {

		try {

			/* Memory */
			System.out.println("\nJava memory:     " + stats.inMemory + " B = "
					+ (stats.inMemory / 1024) + " KB = "
					+ (stats.inMemory / (1024 * 1024)) + " MB");
			System.out.println("External memory: " + stats.exMemory + " B = "
					+ (stats.exMemory / 1024) + " KB = "
					+ (stats.exMemory / (1024 * 1024)) + " MB");
			long totMemory = stats.inMemory + stats.exMemory;
			System.out.println("Total memory:    " + totMemory + " B = "
					+ (totMemory / 1024) + " KB = "
					+ (totMemory / (1024 * 1024)) + " MB");

			/* Time */
			long elapsedTime = System.currentTimeMillis() - stats.start;
			System.out.println("\nElapsed time:  " + elapsedTime + " ms = "
					+ Math.round(((float) elapsedTime) / ((float) 1000))
					+ " s = " + (elapsedTime / (1000 * 60)) + " min");
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the specified state, indented at the specified depth.
	 * 
	 * @param q
	 *            - The state to be printed.
	 * @param indent
	 *            - The margin with which the state should be printed.
	 */
	public static void printState(ProductState q, int indent) {

		try {
			System.out.println("\n" + q.qProg().toString(indent));
			String margin = "";
			for (int i = 0; i < indent; i++) {
				margin += " ";
			}
			System.out.println(margin + "Cur. Oblig.: " + q.qProp());
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determines whether the time point as stored in {@link #next} has expired,
	 * in which case a report should be printed (but this method does not invoke
	 * {@link #printReport}). If the time indeed has expired, it is reset to the
	 * next time point.
	 * 
	 * @return <code>true</code> if a report should be printed;
	 *         </code>false</code> otherwise.
	 */
	public static boolean reportNow() {
		if (System.currentTimeMillis() > next) {
			next = System.currentTimeMillis() + interval;
			return true;
		} else {
			return false;
		}
	}
}