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

import goal.tools.debugger.SteppingDebugger.RunMode;
import goal.tools.logging.GOALLogRecord;
import goal.tools.logging.SingleLineFormatter;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Stores information about a (debug) event.
 *
 * @author W.Pasman June 2008
 * @modified KH
 * @modified N.Kraayenbrink extends GOALLogRecord, so that the events can be
 *           logged and buffered
 */
@SuppressWarnings("serial")
public class DebugEvent extends GOALLogRecord {
	/**
	 *
	 */
	private static final long serialVersionUID = -3206247375363310299L;
	private final RunMode mode;
	private final String source; // name of the source that generated the event
	/**
	 * The channel for which this event was created.
	 */
	private final Channel channel;
	/**
	 * The object (or an instance of the object) referred to in the breakpoint.
	 */
	private final Object associatedObject;

	/**
	 * Creates a debug event for some channel, with no associated object.
	 *
	 * @param mode
	 *            The run mode of the debugger that generated the event.
	 *            (RUNNING, STEPPING, PAUSED or null; should not be KILLED)
	 * @param source
	 *            The source (typically a debugger) that created the event
	 * @param message
	 *            Some description of the event. Usually a breakpoint message.
	 * @param channel
	 *            The channel on which the event is published.
	 */
	public DebugEvent(RunMode mode, String source, String message,
			Channel channel) {
		this(mode, source, message, channel, null);
	}

	/**
	 * Creates a debug event for some channel, with some associated object.
	 *
	 * @param mode
	 *            The run mode of the debugger that generated the event.
	 *            (RUNNING, STEPPING, PAUSED or null; should not be KILLED)
	 * @param source
	 *            The source (typically a debugger) that created the event
	 * @param message
	 *            Some description of the event. Usually a breakpoint message.
	 * @param channel
	 *            The channel on which the event is published.
	 * @param association
	 *            The object to associate with this {@link DebugEvent}. May be
	 *            null if the event is not associated with any object.
	 */
	public DebugEvent(RunMode mode, String source, String message,
			Channel channel, Object association) {
		super(Level.INFO, message, null);
		this.mode = mode;
		this.source = source;
		this.channel = channel;
		this.associatedObject = association;
	}

	/**
	 * Returns the run mode reported by the event.
	 *
	 * @return RUNNING, STEPPING, PAUSED or null (cannot be KILLED as a KILLED
	 *         agent does not generate events).
	 */
	public RunMode getRunMode() {
		return this.mode;
	}

	/**
	 * DOC
	 *
	 * @return
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * @return The channel on which this event was published
	 */
	public Channel getChannel() {
		return this.channel;
	}

	/**
	 * @return The object associated with this {@link DebugEvent}. May be null.
	 */
	public Object getAssociatedObject() {
		return this.associatedObject;
	}

	@Override
	public java.util.logging.Formatter getFormatter() {
		return new Formatter();
	}

	private static class Formatter extends SingleLineFormatter {
		@Override
		public String format(LogRecord record) {
			if (record instanceof DebugEvent) {
				StringBuilder builder = new StringBuilder();
				final String source = ((DebugEvent) record).getSource();
				if (source != null && !source.isEmpty()) {
					builder.append("[" + source + "] ");
				}
				builder.append(super.format(record));
				return builder.toString();
			} else {
				return super.format(record);
			}
		}
	}
}