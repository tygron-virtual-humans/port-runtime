package goal.tools.debugger;

import goal.core.agent.AgentId;
import goal.core.runtime.service.environmentport.EnvironmentPort;
import goal.parser.IParsedObject;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
			channelObservers.put(channel, new LinkedHashSet<DebugObserver>());
		}
	}

	@Override
	public void breakpoint(Channel channel, Object associate, String message,
			Object... args) {
		// Only if there are observers for the channel, events need to be send.
		if (!channelObservers.get(channel).isEmpty()) {
			// Create event and notify observers.
			DebugEvent event = new DebugEvent(getRunMode(), getName(),
					String.format(message, args), channel, associate);
			notifyObservers(channel, event);
		}

		super.breakpoint(channel, associate, message, args);
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
		for (DebugObserver observer : channelObservers.get(channel)) {
			observer.notifyBreakpointHit(event);
		}
	}

	@Override
	public synchronized void setRunMode(RunMode mode) {
		// notify observers of run mode change.
		if (mode != getRunMode()) {
			notifyObservers(Channel.RUNMODE, new DebugEvent(mode, getName(),
					"Run mode = " + mode, Channel.RUNMODE));
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
		channelObservers.get(channel).add(observer);
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
		for (Channel channel : channelObservers.keySet()) {
			channelObservers.get(channel).remove(observer);
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
		channelObservers.get(channel).remove(observer);
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
		return channelObservers.get(channel).contains(observer);
	}

	@Override
	protected boolean checkUserBreakpointHit(Object associatedObject,
			String message, Object... args) {
		boolean hit = super.checkUserBreakpointHit(associatedObject, message,
				args);
		if (hit) {
			IParsedObject parsedObject = (IParsedObject) associatedObject;
			DebugEvent event = new DebugEvent(getRunMode(), getName(),
					"Hit user defined breakpoint on " + parsedObject + "("
							+ parsedObject.getSource() + ")",
							Channel.BREAKPOINTS, associatedObject);
			notifyObservers(Channel.BREAKPOINTS, event);
		}
		return hit;
	}

	@Override
	public String toString() {
		return super.toString() + "\nObservers per channel:\n"
				+ channelObservers.toString();
	}
}
