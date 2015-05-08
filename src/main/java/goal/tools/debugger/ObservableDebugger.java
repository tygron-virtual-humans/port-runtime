package goal.tools.debugger;

import goal.core.runtime.service.environmentport.EnvironmentPort;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import krTools.parser.SourceInfo;
import languageTools.program.agent.AgentId;

public class ObservableDebugger extends SteppingDebugger {
	/**
	 * Maintains a map of which observers have subscribed to which channels.
	 */
	protected final ConcurrentHashMap<Channel, Set<DebugObserver>> channelObservers = new ConcurrentHashMap<>();

	public ObservableDebugger(AgentId id, EnvironmentPort env) {
		this(id.getName(), env);
	}

	public ObservableDebugger(String id, EnvironmentPort env) {
		super(id, env);
		// Initialize channel to observer mapping.
		for (Channel channel : Channel.values()) {
			this.channelObservers.put(channel,
					new LinkedHashSet<DebugObserver>());
		}
	}

	@Override
	public void breakpoint(Channel channel, Object associateObject,
			SourceInfo associateSource, String message, Object... args) {
		// Only if there are observers for the channel, events need to be send.
		if (!this.channelObservers.get(channel).isEmpty()) {
			DebugEvent event = new DebugEvent(getRunMode(), getName(), channel,
					associateObject, associateSource, message, args);
			notifyObservers(channel, event);
		}

		super.breakpoint(channel, associateObject, associateSource, message,
				args);
	}

	/**
	 * Updates observers that subscribed to a channel with the debug information
	 * related to that channel.
	 *
	 * @param channel
	 *            Channel to send information on.
	 * @param event
	 *            Debug event provided to observers subscribed to the channel.
	 */
	protected void notifyObservers(Channel channel, DebugEvent event) {
		for (DebugObserver observer : this.channelObservers.get(channel)) {
			observer.notifyBreakpointHit(event);
		}
	}

	@Override
	public synchronized void setRunMode(RunMode mode) {
		// notify observers of run mode change.
		if (mode != getRunMode()) {
			notifyObservers(Channel.RUNMODE, new DebugEvent(mode, getName(),
					Channel.RUNMODE, mode, null, "Run mode = %s", mode));
		}
		super.setRunMode(mode);
	}

	/**
	 * add channel to viewed channels. Your observer will be notified when debug
	 * event happens on that channel.
	 *
	 * @param observer
	 * @param channel
	 *
	 * @throws NullPointerException
	 *             If the given observer is not subscribed to this
	 *             {@link SteppingDebugger} (or the given observer is a
	 *             {@link BreakpointObserver}).
	 */
	public void subscribe(DebugObserver observer, Channel channel) {
		this.channelObservers.get(channel).add(observer);
	}

	/**
	 * <p>
	 * Removes the observer from the list of registered observers.
	 * </p>
	 *
	 * @param observer
	 *            observer to be removed from registered observer list.
	 */
	public void unsubscribe(DebugObserver observer) {
		for (Channel channel : this.channelObservers.keySet()) {
			this.channelObservers.get(channel).remove(observer);
		}
	}

	/**
	 * remove channel from viewed channels. Your observer not will be notified
	 * anymore when debug event happens on that channel.
	 *
	 * @param observer
	 *            is observer that wants to stop viewing the channel
	 * @param channel
	 *            is channel to be removed from view.
	 *
	 * @throws NullPointerException
	 *             If the given observer is not subscribed to this
	 *             {@link SteppingDebugger} (or the given observer is a
	 *             {@link BreakpointObserver}).
	 */
	public void unsubscribe(DebugObserver observer, Channel channel) {
		this.channelObservers.get(channel).remove(observer);
	}

	/**
	 * Check if observer is viewing given channel.
	 *
	 * @param observer
	 *            is observer that might be viewing
	 * @param channel
	 *            is channel that might be under observation.
	 * @return true if under observation, false if not.
	 *
	 * @throws NullPointerException
	 *             If the given observer is not subscribed to this
	 *             {@link SteppingDebugger} (or the given observer is a
	 *             {@link BreakpointObserver}).
	 */
	public boolean isViewing(DebugObserver observer, Channel channel) {
		return this.channelObservers.get(channel).contains(observer);
	}

	@Override
	protected boolean checkUserBreakpointHit(SourceInfo source, String message,
			Object... args) {
		boolean hit = super.checkUserBreakpointHit(source, message, args);
		if (hit) {
			DebugEvent event = new DebugEvent(getRunMode(), getName(),
					Channel.BREAKPOINTS, null, source,
					"Hit user defined breakpoint on %s", source);
			notifyObservers(Channel.BREAKPOINTS, event);
		}
		return hit;
	}

	@Override
	public String toString() {
		return super.toString() + "\nObservers per channel:\n"
				+ this.channelObservers.toString();
	}
}
