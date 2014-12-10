package goal.tools;

import goal.core.agent.GOALInterpreter;
import goal.tools.adapt.Learner;
import languageTools.program.agent.AgentProgram;

public class IDEGOALInterpreter extends GOALInterpreter<IDEDebugger> {
	public IDEGOALInterpreter(AgentProgram program, IDEDebugger debugger,
			Learner learner) {
		super(program, debugger, learner);
	}
}
