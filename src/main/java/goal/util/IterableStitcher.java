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
 * An Iterable that will 'stitch' an iteration of iterations.<br>
 * <br>
 * Using {@code yield return} as an indicator what the {@code next()} method of
 * the {@link #iterator()} should return, the following pseudo-code describes
 * the working of this class:<br>
 * <br>
 * <code>
 * &nbsp;&nbsp;for (Iterable&lt;? extends T&gt; subIter : allIters)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;for (T value : subIter)<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;yield return value;
 * </code>
 *
 * @author N.Kraayenbrink
 *
 * @param <T>
 *            The value the iterable's iterator will iterate over.
 */
public class IterableStitcher<T> implements Iterable<T> {
	/**
	 * The iterable whose iterator iterates over the collections of values that
	 * this stitcher stitches.
	 */
	private final Iterable<? extends Iterable<? extends T>> baseIterable;

	/**
	 * Creates a new {@link IterableStitcher}, which will stitch the given
	 * iterable of iterables.
	 *
	 * @param baseIterable
	 *            The iteration of iterables to stitch together.
	 */
	public IterableStitcher(
			Iterable<? extends Iterable<? extends T>> baseIterable) {
		this.baseIterable = baseIterable;
	}

	@Override
	public Iterator<T> iterator() {
		return new StitchIterator(this.baseIterable);
	}

	/**
	 * Iterator for the {@link IterableStitcher}. Takes one Iterable and
	 * iterates over its values. Once the iterable's iterator is exhausted, the
	 * next iterable is taken. This repeats until all iterables have been
	 * iterated over.
	 */
	private class StitchIterator implements Iterator<T> {
		/** The iterator to stitch. */
		private final Iterator<? extends Iterable<? extends T>> innerIterator;
		/**
		 * The iterator to use for the next value in {@link #next()}, as well as
		 * for {@link #hasNext()}
		 */
		private Iterator<? extends T> currentIterator;
		/** The iterator last used to generate the value for next(). */
		private Iterator<? extends T> lastUsedIterator;

		/**
		 * Creates a new {@link StitchIterator}.
		 *
		 * @param iterable
		 *            the existing iterable.
		 */
		public StitchIterator(Iterable<? extends Iterable<? extends T>> iterable) {
			this.innerIterator = iterable.iterator();
			// initialize the current iterator
			this.nextIterator();
		}

		/**
		 * Finds the next iterator in {@link #innerIterator} that can provide a
		 * value for {@link #next()}. That iterator is stored in
		 * {@link #currentIterator}.
		 */
		private void nextIterator() {
			this.currentIterator = null;
			while (this.innerIterator.hasNext() && this.currentIterator == null) {
				this.currentIterator = this.innerIterator.next().iterator();
				// skip the iterator if it does not have any
				if (!this.currentIterator.hasNext()) {
					this.currentIterator = null;
				}
			}
		}

		@Override
		public boolean hasNext() {
			// the current iterator is removed when it does not have any
			// more items.
			return this.currentIterator != null;
		}

		@Override
		public T next() {
			if (this.currentIterator == null) {
				throw new NoSuchElementException();
			}

			T value = this.currentIterator.next();
			// save the current iterator, in order to support #remove()
			this.lastUsedIterator = this.currentIterator;
			// make sure to update the current iterator if it is empty,
			// so that #hasNext can return the proper value.
			if (!this.currentIterator.hasNext()) {
				this.nextIterator();
			}
			return value;
		}

		@Override
		public void remove() {
			if (this.lastUsedIterator == null) {
				throw new IllegalStateException();
			}
			this.lastUsedIterator.remove();
		}
	}
}
