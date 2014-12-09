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
import goal.core.agent.AbstractAgentFactory;
import goal.core.agent.Agent;
import goal.core.runtime.MessagingService;
import goal.tools.IDEDebugger;
import goal.tools.IDEGOALInterpreter;
import goal.tools.adapt.Learner;
import goal.tools.errorhandling.exceptions.GOALLaunchFailureException;
import krTools.errors.exceptions.KRInitFailedException;
import languageTools.program.agent.AgentProgram;
import localmessaging.LocalMessaging;
import nl.tudelft.goal.messaging.Messaging;
import nl.tudelft.goal.messaging.exceptions.MessagingException;

import org.junit.Test;

public class SendToVar extends ProgramTest {

	@Test
	public void sendToVar() throws Exception {
		assertSame(RunResult.OK,
				runAgent("src/test/resources/goal/core/program/sendtovar.goal"));
	}

	@Override
	protected Agent<IDEGOALInterpreter> buildAgent(String id,
			AgentProgram program) throws GOALLaunchFailureException,
			MessagingException, KRInitFailedException {
		Messaging messaging = new LocalMessaging();
		MessagingService messagingService = new MessagingService("localhost",
				messaging);
		AbstractAgentFactory<IDEDebugger, IDEGOALInterpreter> factory = new AbstractAgentFactory<IDEDebugger, IDEGOALInterpreter>(
				messagingService) {
			@Override
			protected IDEDebugger provideDebugger() {
				return new IDEDebugger(this.agentId, this.program, null);
			}

			@Override
			protected IDEGOALInterpreter provideController(
					IDEDebugger debugger, Learner learner) {
				return new IDEGOALInterpreter(this.program, debugger, learner);
			}
		};
		return factory.build(program, id, null);
	}
}
