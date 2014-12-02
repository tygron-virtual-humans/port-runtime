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

/**
 * Represents different event channels to which debug observers can subscribe.
 *
 * @author KH
 * @author N.Kraayenbrink Rearranged 10apr2011
 */
public enum Channel {
	/**
	 * Special channel for the separator between reasoning cycles.
	 */
	REASONING_CYCLE_SEPARATOR(
			"The reasoning cycle separator",
			ChannelState.VIEW),

	/**
	 * Channel for reports on received mails.
	 */
	MAILS("Mails received", ChannelState.NONE),

	/**
	 * Channel for reporting percepts inserted into percept base, but only if
	 * PERCEPTS channel has VIEW state.
	 */
	MAILS_CONDITIONAL_VIEW(
			"Changes to the mailbox",
			ChannelState.CONDITIONALVIEW),

	/**
	 * Channel for reports on received percepts.
	 */
	PERCEPTS("Percepts processed", ChannelState.NONE),

	/**
	 * Channel for reporting percepts inserted into percept base, but only if
	 * PERCEPTS channel has VIEW state.
	 */
	PERCEPTS_CONDITIONAL_VIEW(
			"Changes to the percept base",
			ChannelState.CONDITIONALVIEW),

	/**
	 * Channel for reporting on the entry of the init module.
	 */
	INIT_MODULE_ENTRY("Entry of the init module", ChannelState.VIEWPAUSE),
	/**
	 * Channel for reporting on the entry of the main module.
	 */
	MAIN_MODULE_ENTRY("Entry of the main module", ChannelState.VIEWPAUSE),
	/**
	 * Channel for reporting on the entry of the event module.
	 */
	EVENT_MODULE_ENTRY("Entry of the event module", ChannelState.VIEWPAUSE),
	/**
	 * Channel for reporting the entry of a user-defined module.
	 */
	USER_MODULE_ENTRY("Entry of a user-defined module", ChannelState.VIEW),
	/**
	 * Channel for reporting on the exit of the init module.
	 */
	INIT_MODULE_EXIT("Exit of the init module", ChannelState.VIEW),
	/**
	 * Channel for reporting on the exit of the main module.
	 */
	MAIN_MODULE_EXIT("Exit of the main module", ChannelState.VIEW),
	/**
	 * Channel for reporting on the exit of the event module.
	 */
	EVENT_MODULE_EXIT("Exit of the event module", ChannelState.VIEW),
	/**
	 * Channel for reporting the exit of a user-defined module.
	 */
	USER_MODULE_EXIT("Exit of a user-defined module", ChannelState.VIEW),

	/**
	 * Channel for reporting the call (before-entry) of any module.
	 */
	CALL_MODULE("Call to a module", ChannelState.PAUSE),

	/**
	 * Reports whether rule is applicable, and, if so, for which instantiations
	 * if has been applied.
	 */
	RULE_APPLICATION_SUMMARY("Rule application summary", ChannelState.NONE),

	/**
	 * Channel for reporting on the evaluation of a rule's condition.
	 */
	RULE_CONDITION_EVALUATION(
			"Evaluation of rule conditions",
			ChannelState.PAUSE),

	/**
	 * Channel for reporting on the evaluation of a rule's condition. Passes a
	 * different object, used for conditional breakpoints!
	 */
	HIDDEN_RULE_CONDITION_EVALUATION(
			"Evaluation of rule conditions (for breakpoints)",
			ChannelState.HIDDEN),

	/**
	 * Channel for reporting that a rule is being evaluated
	 */
	RULE_CONDITIONAL_VIEW(
			"Starting evaluation of rule",
			ChannelState.CONDITIONALVIEW),

	/**
	 * Channel for reports on actions going to be executed.
	 */
	ACTION_PRECOND_EVALUATION(
			"Evaluation of action preconditions",
			ChannelState.NONE),

	/**
	 * Channel for reports on user-spec actions going to be executed. Only
	 * actions with a precondition should report here.
	 */
	ACTION_PRECOND_EVALUATION_USERSPEC(
			"Evaluation of user-specified action preconditions",
			ChannelState.PAUSE),

	/**
	 * Channel for reports on built-in actions that have been executed.
	 */
	ACTION_EXECUTED_BUILTIN(
			"Built-in actions that have been executed",
			ChannelState.VIEW),

	/**
	 * Channel for reports on user-spec actions that have been executed.
	 */
	ACTION_EXECUTED_USERSPEC(
			"User-specified actions that have been executed",
			ChannelState.VIEW),

																					/**
	 * Channel for reports on complete actioncombos that have been executed.
	 */
	ACTIONCOMBO_FINISHED(
			"Action combos that have been executed",
			ChannelState.NONE),

	/**
	 * Channel for reports on additions to / deletions from the belief base.
	 */
	BB_UPDATES("Changes to the belief base", ChannelState.NONE),

	/**
	 * Channel for reports on additions to / deletions from the goal base.
	 */
	GB_UPDATES("Changes to the goal base", ChannelState.NONE),

	/**
	 * Channel for reports on any changes to the goal base. This is a channel
	 * similar to {@link #GB_UPDATES} but hidden.
	 */
	GB_CHANGES("Changes to the goal base", ChannelState.HIDDEN),

	/**
	 * Channel for reports on goals that have been achieved (and not dropped).
	 */
	GOAL_ACHIEVED("Goals that have been achieved", ChannelState.VIEW),

	/**
	 * Channel for reports on queries by atoms. Reports should contain both the
	 * query and the result. a-goal and goal-a should not be separated into
	 * smaller atoms.
	 */
	ATOM_QUERIES("Results of mental atom queries", ChannelState.NONE),

	/**
	 * Middleware reports here. For now we hide this.
	 */
	MIDDLEWARE_AGENT("Middleware action", ChannelState.HIDDEN),

	/**
	 * Channel to report on changes in run mode. Internal use only.
	 */
	RUNMODE("Run mode changes of agent", ChannelState.HIDDEN),

	/**
	 * Channel to report that agent has gone to sleep.
	 */
	SLEEP("Going to sleep mode", ChannelState.HIDDENVIEW),

	/**
	 * Special channel for notifying the debugger for user-defined breakpoints.
	 */
	BREAKPOINTS("User-defined breakpoints", ChannelState.HIDDENPAUSE);

	/**
	 * text string used to explain channel in debug preference pane.
	 */
	private String explanation;
	/**
	 * The default state of this Channel.
	 */
	private ChannelState defaultState;

	Channel(String explanation, ChannelState defaultState) {
		this.explanation = explanation;
		this.defaultState = defaultState;
	}

	/**
	 * Return explanation text for channel.
	 *
	 * @return text that explains function of channel.
	 */
	public String getExplanation() {
		return explanation;
	}

	/**
	 * @return The default {@link ChannelState} of this {@link Channel}.
	 */
	public ChannelState getDefaultState() {
		return this.defaultState;
	}

	public static Channel getConditionalChannel(Channel channel) {
		switch (channel) {
		case PERCEPTS:
			return PERCEPTS_CONDITIONAL_VIEW;
		case RULE_CONDITION_EVALUATION:
			return RULE_CONDITIONAL_VIEW;
		case ACTION_PRECOND_EVALUATION:
			return RULE_CONDITIONAL_VIEW;
		default:
			// return channel itself it is does not have a related condition
			// channel.
			return channel;
		}
	}

	/**
	 * The state of a Channel.
	 * <p>
	 * It seems that this is mainly a mix of (1) initial state (both for PAUSING
	 * and for VIEW column) for the channel in the breakpoint preferences panel
	 * (2) whether the channel is visible at all in that panel.
	 *
	 * @author N.Kraayenbrink
	 */
	public enum ChannelState {
		/**
		 * Hidden channels will never be displayed to the user (in the debug
		 * preference panel). They only serve as internal event notifications.
		 */
		HIDDEN("Internal"),
		/**
		 * Same as {@link #HIDDEN}, but the debugger will always pause on the
		 * channel. Useful for breakpoints.
		 */
		HIDDENPAUSE("Internal Pause"),
		/**
		 * Same as {@link #HIDDEN}, but the debugger will always display debug
		 * messages on the channel.
		 */
		HIDDENVIEW("Internal Display"),
		/**
		 * Same as {@link #HIDDEN}, but the debugger will always display debug
		 * messages on the channel AND pause.
		 */
		HIDDENVIEWPAUSE("Internal Display and Pause"),
		/**
		 * Same as {@link #HIDDEN}, but the debugger may present debug messages
		 * on the channel to the user.
		 */
		CONDITIONALVIEW("Internal Conditional"),
		/**
		 * Channels in the NONE state will not be displayed in the debug tracer,
		 * and will not be paused upon when stepping.
		 */
		NONE("Don't log or break"),
		/**
		 * Channels in the VIEW state will be displayed in the debug tracer, but
		 * will not be paused upon when stepping.
		 */
		VIEW("Log"),
		/**
		 * Channels in the PAUSE state will be paused upon when stepping.
		 */
		PAUSE("Break"),
		/**
		 * Channels in the VIEWPAUSE state will be displayed in the debug
		 * tracer, and will also be paused upon when stepping.
		 */
		VIEWPAUSE("Log and break");

		/**
		 * text string used to explain channelstate in debug preference pane.
		 */
		private String explanation;

		ChannelState(String explanation) {
			this.explanation = explanation;
		}

		/**
		 * Used to hide channels in breakpoint preference pane.
		 *
		 * @return {@code true} if this is a hidden channel state.
		 */
		public boolean isHidden() {
			return this == HIDDEN || this == HIDDENPAUSE || this == HIDDENVIEW
					|| this == HIDDENVIEWPAUSE || this == CONDITIONALVIEW;
		}

		/**
		 * @return {@code true} if the user should see this in the debug tracer.
		 */
		public boolean canView() {
			return this == VIEW || this == VIEWPAUSE || this == HIDDENVIEW
					|| this == HIDDENVIEWPAUSE;
		}

		/**
		 * @return {@code true} if we should step on this channel state.
		 */
		public boolean shouldPause() {
			return this == PAUSE || this == VIEWPAUSE || this == HIDDENPAUSE
					|| this == HIDDENVIEWPAUSE;
		}

		/**
		 * Return explanation text for channelstate.
		 *
		 * @return text that explains function of channelstate.
		 */
		public String getExplanation() {
			return explanation;
		}
	}
}
