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

package goal.tools.debugger;

import eis.iilang.EnvironmentState;
import goal.core.agent.Agent;
import krTools.parser.SourceInfo;
import languageTools.parser.InputStreamPosition;
import languageTools.program.agent.AgentId;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.preferences.PMPreferences;
import goal.tools.IDEGOALInterpreter;
import goal.tools.LaunchManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * <p>
 * Provides two functions:
 * <ul>
 * <li>Provides control over the run mode of an agent.
 * <li>Provides listener functionality so that others can hear about debug
 * events happening here.
 * </ul>
 * </p>
 * <p>
 * There is exactly 1 debugger for each agent, but at several places we create
 * temporary Debuggers when we do some "dirty" coding by directly calling the
 * agent's databases. These temporary debuggers avoid the user seeing
 * breakpoints occur when he for example makes a manual query in the debugger,
 * or when the initialization is being done of the databases.
 * </p>
 * <p>
 * In the core, various breakpoints have been defined for agents. At each of
 * these breakpoints, an agent will call
 * {@link SteppingDebugger#breakpoint(String, Channel)}. Upon such calls, the
 * debugger checks whether any {@link DebugObserver} that subscribed to the
 * debugger wants to view messages associated with the breakpoint.
 * <p>
 *
 * If someone (not necessarily a {@link DebugObserver}) wants to pause on a
 * breakpoint, the debugger enforces this and makes the agent pause on the next
 * breakpoint.
 * </p>
 * <p>
 * The debugger is used to view debug events as follows:<br>
 * <ol>
 * <li>Create a {@link DebugObserver} and implement its interface.
 * <li> {@link #subscribe(DebugObserver)} the {@link DebugObserver} to interact
 * with the debugger of the agent.
 * <li>Call {@link #addPause(DebugObserver, Channel)} and
 * {@link #subscribe(DebugObserver, Channel)} as necessary.
 * <li>Run the agent.
 * </ol>
 * </p>
 * See also the notes with {@link #breakpoint(String, Channel)}
 * <p>
 * We currently do not use a standard Observer/Observable pattern because agents
 * may not be interested in all events, depending on the debug settings.
 * </p>
 * <p>
 * To control the debugger and the agent running, you can directly call
 * {@link #finestep()}, {@link #step()} and {@link #run()}. To kill the agent,
 * call {@link #kill()}.
 *
 *
 * @author W.Pasman June 2008
 * @modified KH 091224 clean up, simplified, changed logic (channel
 *           subscriptions instead of levels)
 * @modified W.Pasman 29jul10
 * @modified W.Pasman 15feb2011 major code revision, to fix #1178.
 * @modified W.Pasman 9jul13 major revision, fixing #2551
 */
public class SteppingDebugger implements Debugger {
	/**
	 * Possible run modes of the debugger.
	 */
	public static enum RunMode {
		/**
		 * Agent runs and does not pause on any of the breakpoints.
		 * <p>
		 * Note that although breakpoints are passed over, associated messages
		 * of a breakpoint are independently reported from the observer's run
		 * mode, based on viewing subscriptions to a channel.
		 * </p>
		 */
		RUNNING(2),
		/**
		 * Agent is in stepping mode. In this mode, agent will be PAUSED at the
		 * next breakpoint that is set with {@link Debugger#addPause(Channel)}.
		 * Note that it will effectively be running if no breakpoints were set.
		 */
		STEPPING(4),
		/**
		 * Like stepping, but in this mode the agent will be paused at ANY next
		 * breakpoint (and not just on the ones set with
		 * {@link Debugger#addPause(Channel)}). When the agent actually reached
		 * the breakpoint the state changes to {@link #PAUSED}.
		 */
		FINESTEPPING(5),
		/**
		 * Agent has stepped onto a breakpoint and is not running anymore. Call
		 * {@link Debugger#step()} or {@link Debugger#finestep()} to step to
		 * next breakpoint.
		 */
		PAUSED(6),
		/**
		 * Corresponding agent process has been killed. Agent has been
		 * terminated. Debugger will immediately throw a {@link ThreadDeath}
		 * when it discovers this run mode.
		 */
		KILLED(10),
		/**
		 * Process state is unknown, may be any of above. This is actually a
		 * hack, only used by the environment to indicate that we don't know the
		 * environment's runstate.
		 */
		UNKNOWN(0),
		/**
		 * Process is remote, unknown. prio is 0, indicating everything more
		 * informative than this should be preferred.
		 */
		REMOTEPROCESS(0);

		private int priority;

		RunMode(int p) {
			priority = p;
		}

		/**
		 * @param othermode
		 *            another mode
		 * @return the mode with the highest prio, given this mode and another
		 *         mode.
		 */
		public RunMode merge(RunMode othermode) {
			if (priority > othermode.getPriority()) {
				return this;
			}
			return othermode;
		}

		/**
		 * @return the prio of this run mode.
		 */
		public int getPriority() {
			return priority;
		}
	}

	/**
	 * Every debugger is typically associated with a unique agent. Other names
	 * are also used, e.g. for temporary debuggers.
	 */
	protected final String name;
	/**
	 * set of channels, indicating that the debugger will pause on when hit.
	 */
	private final Set<Channel> pausingChannels = new CopyOnWriteArraySet<>();
	/**
	 * The default run mode of the debugger: run without stopping anywhere.
	 */
	protected RunMode runMode;
	/**
	 * If set to true, any encountered breakpoint will be ignored
	 */
	protected boolean keepRunning = false;
	/**
	 * If an ID (see {@link InputStreamPosition#getID()} is in this set, we
	 * break on it.
	 */
	private final Set<Integer> breakpointIds = new HashSet<>();
	private final Map<Integer, Integer> had = new HashMap<>();

	/**
	 * Creates debugger for given label. Names other than agent names are used
	 * for temporary debuggers. By default a new debugger has
	 * {@link RunMode#RUNNING} and {@link Channel#REASONING_CYCLE_SEPARATOR}
	 * enabled.
	 *
	 * @param name
	 *            The name of the debugger.
	 * @param env
	 *            The current environment (if any), used when the 'new agents
	 *            copy environment run state' option is enabled.
	 */
	public SteppingDebugger(String name, EnvironmentPort env) {
		this.name = name;
		this.runMode = getInitialRunMode();
		if (PMPreferences.getAgentCopyEnvRunState()) {
			final EnvironmentState state = (env == null) ? null : env
					.getEnvironmentState();
			if (state == null || state.equals(EnvironmentState.RUNNING)) {
				this.runMode = RunMode.RUNNING;
			} else if (state.equals(EnvironmentState.PAUSED)) {
				this.runMode = RunMode.STEPPING;
			} // killed or initializing? leave the default...
		}
	}

	/**
	 * Creates debugger for given agent. By default a new debugger has
	 * {@link RunMode#RUNNING} and {@link Channel#REASONING_CYCLE_SEPARATOR}
	 * enabled.
	 *
	 * @param id
	 *            The relevant agent (its name will be used for the debugger).
	 * @param env
	 *            The current environment (if any), used when the 'new agents
	 *            copy environment run state' option is enabled.
	 */
	public SteppingDebugger(AgentId id, EnvironmentPort env) {
		this(id.getName(), env);
	}

	/**
	 * @return The initial run mode for the debugger. This is RUNNING by
	 *         default, but subclasses can override this.
	 */
	public RunMode getInitialRunMode() {
		return RunMode.RUNNING;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see goal.tools.debugger.IDebugger#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the overall run mode.
	 *
	 * @return overall run mode of debugger.
	 */
	public synchronized RunMode getRunMode() {
		return runMode;
	}

	/**
	 * Changes the run mode. Only notifies observers of a change in run mode.
	 * <p>
	 * Also wakes up the debugged thread of the agent, to check if it can
	 * proceed running.
	 * </p>
	 *
	 * @param mode
	 *            The run mode for the debugger.
	 */
	public synchronized void setRunMode(RunMode mode) {
		if (runMode != mode) {
			runMode = mode;
			// wake up any processes that have been paused.
			synchronized (this) {
				notifyAll();
			}
		}
	}

	public synchronized void setKeepRunning(boolean bool) {
		this.keepRunning = bool;
	}

	/***************************************************************/
	/************** FUNCTIONS TO ENFORCE DEBUGGING. ***************/
	/********** TO BE CALLED BY THREAD TO BE DEBUGGED. ************/
	/**************************************************************/

	@SuppressWarnings("fallthrough")
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * goal.tools.debugger.IDebugger#breakpoint(goal.tools.debugger.Channel,
	 * java.lang.Object, java.lang.String, java.lang.Object)
	 */
	@Override
	public void breakpoint(Channel channel, Object associate, String message,
			Object... args) {
		if (this.keepRunning) {
			return;
		}
		if (checkUserBreakpointHit(associate, message, args)) {
			setRunMode(RunMode.PAUSED);
		}

		// Process special hidden run mode channel.
		switch (getRunMode()) {
		case KILLED:
			throw new DebuggerKilledException();
		case RUNNING:
			break; // just continue.
		case STEPPING:
			if (!pausingChannels.contains(channel)) {
				break;
			}
			// we need to pause on this channel, fall through;
		case FINESTEPPING:
			// pause on any channel that we encounter when in fine stepping
			// mode.
			setRunMode(RunMode.PAUSED);
		case PAUSED:
			// Wait for agent to wake up.
			awaitUnPause();
			break;
		case REMOTEPROCESS:
			// we do not have access to run mode of remote process.
			break;
		default:
		case UNKNOWN:
			// nothing we can do.
			break;
		}
		return;
	}

	/**
	 * Waits as long as we are in {@link RunMode#PAUSED} mode.
	 */
	private void awaitUnPause() {
		// TODO use a better mechanism for this.
		synchronized (this) {
			while (getRunMode() == RunMode.PAUSED) {
				try {
					wait();
				} catch (InterruptedException e) {
					throw new DebuggerKilledException();
				}
			}

			// We can either be interrupted while waiting
			// or the run mode can be changed.
			// When killed this thread has to break out ASAP.
			// Which is right now.
			if (getRunMode() == RunMode.KILLED) {
				throw new DebuggerKilledException();
			}
		}
	}

	/**
	 * Returns when killed mode is entered.
	 *
	 * TODO: work in progress; we need to create some method to be sure that the
	 * agent has been completely terminated, i.e., the thread is gone (or,
	 * ThreadDeath has been thrown which is as close as we can get I guess).
	 *
	 * The idea here is to introduce another RunMode state called TOBEKILLED (at
	 * which point an InterruptException is thrown); KILLED state then is
	 * reached at the next breakpoint where the run mode is updated again (and
	 * ThreadDeath is thrown).
	 */
	public void awaitKilled() {
		throw new UnsupportedOperationException("Not yet implemented");
		// // TODO use a better mechanism for this.
		//
		// synchronized (this) {
		// while (getRunMode() != RunMode.KILLED) {
		// try {
		// wait();
		// } catch (InterruptedException e) {
		// throw new ThreadDeath();
		// }
		// }
		// }
	}

	// ******************** notification methods ***************************/

	/***************************************************************/
	/************** FUNCTIONS TO CONTROL DEBUGGING *****************/
	/***************************************************************/

	/******************** control methods ***************************/

	/**
	 * Puts the {@link SteppingDebugger} in {@link RunMode#STEPPING} mode. The
	 * debugger will then halt on the first breakpoint that someone is listening
	 * to.
	 */
	public synchronized void step() {
		setRunMode(RunMode.STEPPING);
	}

	/**
	 * Puts the {@link SteppingDebugger} in {@link RunMode#FINESTEPPING} mode.
	 */
	public synchronized void finestep() {
		setRunMode(RunMode.FINESTEPPING);
	}

	/**
	 * Puts the {@link SteppingDebugger} into {@link RunMode#RUNNING} mode.
	 */
	public synchronized void run() {
		setRunMode(RunMode.RUNNING);
	}

	/**
	 * Set the agent's run mode to {@link RunMode#KILLED} and set status of
	 * current thread to interrupted.
	 * <p>
	 * Interrupting the thread does not completely kill the agent; the idea
	 * instead is to throw {@link ThreadDeath} when the agent hits the next
	 * breakpoint (or is already at a breakpoint), which should terminate the
	 * {@link Thread} in which the agent runs.
	 * </p>
	 * <p>
	 * Assumes the agent (and its debugger) are running in its own thread.
	 * </p>
	 *
	 */
	@Override
	public void kill() {
		setRunMode(RunMode.KILLED);
	}

	/**
	 * add channel to pause channels. Thread will be paused when agent hits
	 * breakpoint on this channel. See also {@link DebugSettingSynchronizer}.
	 *
	 * @param channel
	 *            is channel that causes agent to pause.
	 */
	public void addPause(Channel channel) {
		pausingChannels.add(channel);
	}

	/**
	 * remove channel from pause channels. Agent will not be paused anymore when
	 * agent hits breakpoint on this channel.
	 *
	 * @param channel
	 *            is channel that causes agent to pause.
	 */
	public void removePause(Channel channel) {
		pausingChannels.remove(channel);
	}

	/**
	 * Returns a brief description of the {@link SteppingDebugger}, including:
	 * its name, observers per channel, and channels on which to pause when in
	 * stepping mode. Details of the exact representation or format are not
	 * specified here.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Debugger name = ");
		builder.append(name);
		builder.append("\nPausing channels:\n");
		builder.append(pausingChannels.toString());
		builder.append("\nRunmode = ");
		builder.append(runMode);
		return builder.toString();
	}

	/********************************************************************/
	/********************* Breakpoint Handling **************************/
	/********************************************************************/
	/**
	 * Check if we hit a user set breakpoint. Notify observers if that is the
	 * case
	 */
	@SuppressWarnings("deprecation")
	protected boolean checkUserBreakpointHit(Object associatedObject,
			String message, Object... args) {
		// make sure there is a source attached to the object.
		// just ignore the event if there isn't
		SourceInfo source = null;
		if (associatedObject instanceof SourceInfo) {
			source = ((SourceInfo) associatedObject);
		}
		if (source != null && breakpointIds.contains(source.getID())) {
			final Agent<IDEGOALInterpreter> agent = LaunchManager.getCurrent()
					.getRuntimeManager().getAgent(new AgentId(this.name));
			if (agent != null) {
				final int round = agent.getController().getRunState()
						.getRoundCounter();
				final Integer get = this.had.get(round);
				if (get != null && get.intValue() == source.getID()) {
					return false;
				}
				this.had.put(round, source.getID());
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * set breakpoints for this observer to a given set of {@link IParsedObject}
	 * s.
	 *
	 * @param breakpoints
	 *            The set of breakpoints.
	 */
	public void setBreakpoints(Set<SourceInfo> breakpoints) {
		breakpointIds.clear();
		if (breakpoints != null) {
			for (SourceInfo breakpoint : breakpoints) {
				this.setBreakpoint(breakpoint.getID());
			}
		}
	}

	/**
	 * Sets a breakpoint on the breakpoint-object with the given ID.
	 *
	 * @param id
	 *            The ID.
	 */
	public void setBreakpoint(int id) {
		breakpointIds.add(id);
	}

	/**
	 * Unsets a breakpoint from the breakpoint-object with the given ID.
	 *
	 * @param id
	 *            The ID.
	 */
	public void unsetBreakpoint(int id) {
		breakpointIds.remove(id);
	}

	@Override
	public void reset() {
		setRunMode(getInitialRunMode());
	}

	@Override
	public void dispose() {
		// Does nothing.
	}
}
