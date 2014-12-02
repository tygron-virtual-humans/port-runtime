package goal.core.performance;

import goal.core.agent.AgentId;
import goal.core.kr.language.DatabaseFormula;
import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.BeliefBase;
import goal.tools.debugger.Debugger;
import goal.tools.debugger.SteppingDebugger;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.lang.management.ManagementFactory;
import java.util.HashSet;

import swiprolog3.engines.SWIPrologLanguage;
import swiprolog3.language.JPLUtils;
import swiprolog3.language.PrologDBFormula;

public class TestMentalState {

	// @Test
	public void testInsert_SWIProlog3_performance()
			throws GOALLaunchFailureException, KRInitFailedException {
		SWIPrologLanguage language = SWIPrologLanguage.getInstance();
		BeliefBase kb = new BeliefBase(BASETYPE.KNOWLEDGEBASE, language,
				new HashSet<DatabaseFormula>(), new AgentId(""),
				new AgentId(""));

		for (int j = 0; j < 1; j++) {
			BeliefBase bb = new BeliefBase(BASETYPE.BELIEFBASE, language,
					new HashSet<DatabaseFormula>(), new AgentId("agent" + j),
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
		Debugger debugger = new SteppingDebugger("insertFactsSWIProlog3", null);
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
