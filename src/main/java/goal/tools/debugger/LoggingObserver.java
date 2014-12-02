package goal.tools.debugger;

import goal.preferences.DebugPreferences;
import goal.tools.logging.InfoLog;

/**
 * This is a default observer for Debugger that forwards breakpoint hit info to
 * the {@link InfoLog}.
 *
 * @author vincent
 * @modified Wouter added javadoc
 */
public class LoggingObserver implements DebugObserver {

	private final ObservableDebugger debugger;

	/**
	 * By calling this, an observer is attached to given debugger. It forwards
	 * breakpoint hit info into the {@link InfoLog} log.
	 *
	 * @param debugger
	 *            The current debugger.
	 */
	public LoggingObserver(ObservableDebugger debugger) {
		this.debugger = debugger;
		this.subscribe();
	}

	/**
	 * Subscribe to all channels that were selected for viewing by the user.
	 */
	private void subscribe() {
		for (Channel channel : Channel.values()) {
			if (DebugPreferences.getChannelState(channel).canView()) {
				this.addViewChannel(channel);
			}
		}
	}

	/**
	 * Adds a channel to the channels which the observer wants to view.
	 *
	 * @param channel
	 *            Channel which is to be viewed.
	 */
	private void addViewChannel(Channel channel) {
		debugger.subscribe(this, channel);
		if (!Channel.getConditionalChannel(channel).equals(channel)) {
			this.debugger.subscribe(this,
					Channel.getConditionalChannel(channel));
		}
	}

	@Override
	public String getObserverName() {
		return "LoggingObserver";
	}

	@Override
	public void notifyBreakpointHit(DebugEvent event) {
		InfoLog.getLogger().log(event);
	}

}
