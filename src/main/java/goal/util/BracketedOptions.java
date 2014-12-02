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

package goal.util;

import goal.core.program.rules.RuleSet;
import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.agentfile.GOALError;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.errorhandling.exceptions.InvalidSemanticsException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.antlr.runtime.NoViableAltException;

/**
 * Container for options (or settings). Although the name implies a raw format
 * of the form [key=value(,key=value)*], any set of key-value pairs should fit
 * in this class. The string representation does use brackets, however.<br>
 * When requesting values for various 'options', ANTLR exceptions are thrown
 * whenever the request could not be handled. Includes request support for
 * integer and double values; appropriate exceptions are thrown when the
 * requested option is not what it is expected to be (it is always a string).<br>
 * After requesting all necessary options, call
 * {@link #generateUnusedWarnings()} to generate warnings for any unused
 * options.<br>
 * For a usage example, see {@link RuleSet#setOptions(BracketedOptions)}.
 *
 * @author N.Kraayenbrink
 */
public class BracketedOptions {
	private final Map<KEYS, OptionValue> options;

	public BracketedOptions() {
		this.options = new HashMap<>();
	}

	/**
	 * Checks if a certain option is present.
	 *
	 * @param key
	 *            The name of the option. Is case-insensitive.
	 * @return <code>true</code> iff an option with the given name is present.
	 */
	public boolean hasOption(String key) {
		return this.options.containsKey(key.toLowerCase());
	}

	/**
	 * Checks if any option is present at all.
	 *
	 * @return True if an option is present
	 */
	public boolean hasOption() {
		return !this.options.isEmpty();
	}

	/**
	 * Adds an option to this set of options
	 *
	 * @param key
	 *            The name of the option
	 * @param value
	 *            The value of the option
	 * @param source
	 *            The source of the option in the code
	 * @return ValidatorError on error; null otherwise
	 */
	public ValidatorError addOption(String key, String value,
			InputStreamPosition source) {
		KEYS enumKey;
		try {
			enumKey = KEYS.valueOf(key.toUpperCase());
		} catch (Exception e) {
			return new ValidatorError(GOALError.OPTION_UNKNOWN, source, key
					+ "=" + value);
		}
		if (!options.containsKey(enumKey)) {
			this.options.put(enumKey, new OptionValue(enumKey, value, source));
		} else {
			return new ValidatorError(GOALError.OPTION_DUPLICATE, source, key);
		}
		return null;
	}

	/**
	 * Returns the keys stored in this {@link BracketedOptions} map.
	 *
	 * @return The keys stored in this options map.
	 */
	public Set<KEYS> getKeys() {
		return this.options.keySet();
	}

	/**
	 * Returns a string value associated with a key. Assumes that key exists.
	 *
	 * @param key
	 *            The key of the option.
	 * @return The value of the option as a String.
	 */
	public String getStringValue(KEYS key) {
		return this.options.get(key).getStringValue();
	}

	/**
	 * Tries to obtain an integer value of a certain option
	 *
	 * @param key
	 *            The name of the option
	 * @return The value of the option as an integer
	 * @throws NoViableAltException
	 *             If the option value is not an integer
	 * @throws InvalidSemanticsException
	 *             If there is no option with the given name
	 */
	public int getIntegerValue(String key) throws NoViableAltException,
	InvalidSemanticsException {
		key = key.toLowerCase();
		if (!options.containsKey(key)) {
			throw new InvalidSemanticsException("Option " + key
					+ " is required", "");
		} else {
			return this.options.get(key).getIntegerValue();
		}
	}

	/**
	 * Tries to obtain a double value of a certain option
	 *
	 * @param key
	 *            The name of the option
	 * @return The value of the option as a double
	 * @throws NoViableAltException
	 *             If the option value is not a double
	 * @throws InvalidSemanticsException
	 *             If there is no option with the given name
	 */
	public double getDoubleValue(String key) throws NoViableAltException,
	InvalidSemanticsException {
		key = key.toLowerCase();
		if (!options.containsKey(key)) {
			throw new InvalidSemanticsException("Option " + key
					+ " is required", "");
		} else {
			return this.options.get(key).getDoubleValue();
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		if (this.options.size() > 0) {
			boolean first = true;
			for (OptionValue option : this.options.values()) {
				if (!first) {
					builder.append(",");
				} else {
					first = false;
				}
				builder.append(option.toString());
			}
		}
		builder.append("]");
		return builder.toString();
	}

	@SuppressWarnings("serial")
	private class OptionValue extends ParsedObject {
		/**
		 * The key of the option
		 */
		KEYS key;
		/**
		 * The actual value of the option
		 */
		String value;

		OptionValue(KEYS key, String value, InputStreamPosition source) {
			super(source);
			this.key = key;
			this.value = value;
		}

		/**
		 * @return The value of this option as a String
		 */
		String getStringValue() {
			return this.value;
		}

		/**
		 * Try to extract an integer value out of this option value
		 *
		 * @return A parsed integer from the option value
		 * @throws NoViableAltException
		 *             If the value is not actually an integer value
		 */
		int getIntegerValue() throws NoViableAltException {
			try {
				return Integer.parseInt(this.value);
			} catch (NumberFormatException nfe) {
				throw new NoViableAltException(
						"Integer expected for option value " + "for '"
								+ this.key + "', but got: '" + this.value + "'",
								0, 0, null);
			}
		}

		/**
		 * Try to extract a double value out of this option value
		 *
		 * @return A parsed double from the option value
		 * @throws NoViableAltException
		 *             If the value is not actually a double value
		 */
		double getDoubleValue() throws NoViableAltException {
			try {
				return Double.parseDouble(this.value);
			} catch (NumberFormatException nfe) {
				throw new NoViableAltException(
						"Double expected for option value " + "for '"
								+ this.key + "', but got: '" + this.value + "'",
								0, 0, null);
			}
		}

		@Override
		public String toString() {
			return this.key + "," + this.value;
		}
	}

	/**
	 * All keys recognized by GOAL. See also grammar GOAL.g.
	 *
	 * @author K.Hindriks
	 */
	public enum KEYS {
		// Module options FOCUS and EXIT
		FOCUS("focus"),
		EXIT("exit"),
		// Program section option ORDER
		ORDER("order");

		private String displayName;

		private KEYS(String displayName) {
			this.displayName = displayName;
		}

		@Override
		public String toString() {
			return this.displayName;
		}
	}
}