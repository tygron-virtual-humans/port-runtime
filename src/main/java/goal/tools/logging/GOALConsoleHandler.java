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

package goal.tools.logging;

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * A <code>ConsoleHandler</code> publishes log records to
 * <code>System.err</code> if the record level >= Level.WARNING, or else to
 * <code>System.out</code>. If the log record is a GOALLogRecord, the formatter
 * inside the record is used for formatting; else the simpleFormatter is used.
 *
 * </ul>
 * 
 * @author W.Pasman 17mar15
 *
 */
public class GOALConsoleHandler extends Handler {

	private StreamHandler outstream, errstream;
	private Formatter simpleFormatter = new SimpleFormatter();

	public GOALConsoleHandler() {
		outstream = new StreamHandler(System.out, simpleFormatter);
		errstream = new StreamHandler(System.err, simpleFormatter);
		setLevel(Level.ALL);
	}

	@Override
	public void close() {
		flush();
	}

	@Override
	public void publish(LogRecord record) {
		StreamHandler handler;
		if (record.getLevel().intValue() >= Level.WARNING.intValue()) {
			handler = errstream;
		} else {
			handler = outstream;
		}

		Formatter formatter = null;
		if (record instanceof GOALLogRecord ) {
			formatter = ((GOALLogRecord)record).getFormatter();
		} 
		
		if (formatter!=null) {
			handler.setFormatter(formatter);
		} else {
			handler.setFormatter(simpleFormatter);
		}
		
		handler.publish(record);
		handler.flush();
	}

	@Override
	public void flush() {
		outstream.flush();
		errstream.flush();
	}
	

}



