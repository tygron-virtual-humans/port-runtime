package goal.core.performance;

import goal.core.mentalstate.BeliefBase;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.NOPDebugger;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;

import java.lang.management.ManagementFactory;
import java.rmi.activation.UnknownObjectException;
import java.util.LinkedList;

import krTools.errors.exceptions.KRDatabaseException;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.errors.exceptions.KRQueryFailedException;
import krTools.language.DatabaseFormula;
import languageTools.program.agent.AgentId;
import languageTools.program.agent.AgentProgram;
import mentalState.BASETYPE;
import mentalstatefactory.MentalStateFactory;
import swiprolog.SWIPrologInterface;
import swiprolog.language.JPLUtils;
import swiprolog.language.PrologDBFormula;

public class TestMentalState {

	// @Test
	public void testInsert_SWIProlog3_performance()
			throws GOALLaunchFailureException, KRInitFailedException,
			KRDatabaseException, KRQueryFailedException, UnknownObjectException {
		mentalState.MentalState state = MentalStateFactory
				.getInterface(SWIPrologInterface.class);
		BeliefBase kb = new BeliefBase(BASETYPE.KNOWLEDGEBASE, state,
				new LinkedList<DatabaseFormula>(), new AgentProgram(null),
				new AgentId(""));

		for (int j = 0; j < 1; j++) {
			BeliefBase bb = new BeliefBase(BASETYPE.BELIEFBASE, state,
					new LinkedList<DatabaseFormula>(), new AgentProgram(null),
					new AgentId("agent" + j));

			int nrOfInserts = 250000;
			long start = ManagementFactory.getThreadMXBean()
					.getCurrentThreadCpuTime();

			insertFactsSWIProlog3(bb, nrOfInserts);

			long finish = ManagementFactory.getThreadMXBean()
					.getCurrentThreadCpuTime();

			System.out.println("Inserting " + nrOfInserts + " facts took: "
					+ (finish - start) / 1000000 + " milliseconds");
		}
	}

	private void insertFactsSWIProlog3(BeliefBase bb, int nrOfInserts) {
		Debugger debugger = new NOPDebugger("insertFactsSWIProlog3");
		for (int i = 0; i < nrOfInserts; i++) {
			// SWIPrologLanguage3
			jpl.Integer nr = new jpl.Integer(i);
			jpl.Term fact = JPLUtils.createCompound("agent", nr);
			jpl.Term insert = JPLUtils.createCompound("assert", fact);
			PrologDBFormula formula = new PrologDBFormula(insert, null);

			bb.insert(formula, debugger);
		}
	}
}
