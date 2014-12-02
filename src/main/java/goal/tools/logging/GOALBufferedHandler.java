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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.MemoryHandler;

/**
 * Small wrapper around {@link MemoryHandler}, so that the buffered data is
 * published when flush() is called.
 *
 * @author N.Kraayenbrink
 */
public class GOALBufferedHandler extends Handler {
	private final MemoryHandler innerHandler;

	/**
	 * Creates a new {@link GOALBufferedHandler}, with a buffer size of 1000
	 * records. Logged records are not instantly published, but stored until
	 * {@link #flush()} is called.
	 *
	 * @param target
	 *            Where the logged records should be sent to, once the buffer is
	 *            flushed.
	 */
	public GOALBufferedHandler(Handler target) {
		this(target, 1000);
	}

	/**
	 * Creates a new {@link GOALBufferedHandler}, with some buffer size. Logged
	 * records are not instantly published, but stored until {@link #flush()} is
	 * called.
	 *
	 * @param target
	 *            Where the logged records should be sent to, once the buffer is
	 *            flushed.
	 * @param bufferSize
	 *            How many log records can be stored at once. If more than this
	 *            number of records are stored, the oldest ones are removed.
	 */
	public GOALBufferedHandler(Handler target, int bufferSize) {
		this(target, bufferSize, Level.ALL);
	}

	/**
	 * Creates a new {@link GOALBufferedHandler}, with some buffer size. Logged
	 * records are not instantly published, but stored until {@link #flush()} is
	 * called.
	 *
	 * @param target
	 *            Where the logged records should be sent to, once the buffer is
	 *            flushed.
	 * @param bufferSize
	 *            How many log records can be stored at once. If more than this
	 *            number of records are stored, the oldest ones are removed.
	 * @param level
	 *            Only log records of this level or higher are stored in the
	 *            buffer. Any record with a lower level is completely ignored.
	 *            (Level.ALL by default, ignoring none of the records)
	 */
	public GOALBufferedHandler(Handler target, int bufferSize, Level level) {
		// create an inner handler, which never pushes anything automatically
		// TODO: change Level.ALL to Level.OFF. #flush() should then be called
		// to published the logged warnings
		this.innerHandler = new MemoryHandler(target, bufferSize, Level.OFF);
		this.setLevel(level);
	}

	@Override
	public void close() throws SecurityException {
		this.innerHandler.close();
	}

	@Override
	public void flush() {
		// MemoryHandler.flush() does not clear the buffer
		this.innerHandler.push();
		this.innerHandler.flush();
	}

	@Override
	public void publish(LogRecord record) {
		if (record.getLevel().intValue() >= this.getLevel().intValue()) {
			this.innerHandler.publish(record);
		}
	}

	public void setPushLevel(Level newLevel) {
		this.innerHandler.setPushLevel(newLevel);
	}
}
