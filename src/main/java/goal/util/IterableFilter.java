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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Filters an iteration of {@code T}s, and converts them into {@code U}s if it
 * is decided that they should be in the iteration. The conversion is done by
 * the {@link #select} method.<br>
 * <br>
 * Using {@code yield return} as an indicator what the {@code next()} method of
 * the {@link #iterator()} should return, the following pseudo-code describes
 * the working of this class:<br>
 * <br>
 * <code>
 * &nbsp;&nbsp;for (T value : iterable) {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;U outValue = {@link select}(value);<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;if (outValue != null)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;yield return outValue;<br>
 * &nbsp;&nbsp;}
 *
 * @author N.Kraayenbrink
 *
 * @param <T>
 *            The input type.
 * @param <U>
 *            The output type.
 */
public abstract class IterableFilter<T, U> implements Iterable<U> {
	/**
	 * The iterable to get the input values for {@link #select} from.
	 */
	private final Iterable<T> baseIterable;

	/**
	 * Creates a new IterableFilter.
	 *
	 * @param baseIterable
	 *            The iterable to get the input values from.
	 */
	public IterableFilter(Iterable<T> baseIterable) {
		this.baseIterable = baseIterable;
	}

	/**
	 * Checks if the given value should be part of the iterated collection. If
	 * so, the value should be converted and returned. If not, {@code null} is
	 * returned.
	 *
	 * @param value
	 *            The value that may or may not have to be in the iterated
	 *            collection.
	 * @return {@code null} iff the given value should not be in the iterated
	 *         collection. The converted value otherwise.
	 */
	public abstract U select(T value);

	@Override
	public Iterator<U> iterator() {
		return new FilteredIterator(this.baseIterable);
	}

	/**
	 * The iterator for a filtered Iterable. Will find the next filtered value
	 * from the inner iterator after each next() call, as well as after
	 * construction, to prevent requiring a call to {@link #hasNext()} every
	 * time (although that is still recommended).<br>
	 * The values are selected using the {@link IterableFilter#select} method.
	 */
	private class FilteredIterator implements Iterator<U> {
		private final Iterator<T> innerIterator;
		private U next;

		public FilteredIterator(Iterable<T> iterable) {
			this.innerIterator = iterable.iterator();
			this.findNext();
		}

		/**
		 * Finds the next value in the iterator for which
		 * {@link IterableFilter#select} returns a non-{@code null} value.
		 */
		private void findNext() {
			this.next = null;
			while (this.innerIterator.hasNext() && this.next == null) {
				T in = this.innerIterator.next();
				this.next = select(in);
			}
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		@Override
		public U next() {
			U val = this.next;
			if (val == null) {
				throw new NoSuchElementException();
			}
			this.findNext();
			return val;
		}

		@Override
		public void remove() {
			// since the inner iterator is one step further, we cannot call
			// it's remove() method.
			throw new UnsupportedOperationException();
		}
	}
}
