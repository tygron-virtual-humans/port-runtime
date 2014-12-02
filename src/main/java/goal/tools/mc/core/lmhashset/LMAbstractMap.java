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

package goal.tools.mc.core.lmhashset;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A "less memory" version (hence "LM") of {@link java.util.AbstractMap}, whose
 * each entry is a key rather than a key-value pair.
 * 
 * @author sungshik
 * 
 */
public abstract class LMAbstractMap<K> implements LMMap<K> {
	/**
	 * Sole constructor. (For invocation by subclass constructors, typically
	 * implicit.)
	 */
	protected LMAbstractMap() {
	}

	// Query Operations

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation returns <tt>entrySet().size()</tt>.
	 */
	@Override
	public int size() {
		return entrySet().size();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation returns <tt>size() == 0</tt>.
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation iterates over <tt>entrySet()</tt> searching for an
	 * entry with the specified value. If such an entry is found, <tt>true</tt>
	 * is returned. If the iteration terminates without finding such an entry,
	 * <tt>false</tt> is returned. Note that this implementation requires linear
	 * time in the size of the map.
	 * 
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 */
	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation iterates over <tt>entrySet()</tt> searching for an
	 * entry with the specified key. If such an entry is found, <tt>true</tt> is
	 * returned. If the iteration terminates without finding such an entry,
	 * <tt>false</tt> is returned. Note that this implementation requires linear
	 * time in the size of the map; many implementations will override this
	 * method.
	 * 
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 */
	@Override
	public boolean containsKey(Object key) {
		Iterator<LMMap.Entry<K>> i = entrySet().iterator();
		if (key == null) {
			while (i.hasNext()) {
				Entry<K> e = i.next();
				if (e.getKey() == null) {
					return true;
				}
			}
		} else {
			while (i.hasNext()) {
				Entry<K> e = i.next();
				if (key.equals(e.getKey())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation iterates over <tt>entrySet()</tt> searching for an
	 * entry with the specified key. If such an entry is found, the entry's
	 * value is returned. If the iteration terminates without finding such an
	 * entry, <tt>null</tt> is returned. Note that this implementation requires
	 * linear time in the size of the map; many implementations will override
	 * this method.
	 * 
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 */
	public Object get(Object key) {
		return null;
	}

	// Modification Operations

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation always throws an
	 * <tt>UnsupportedOperationException</tt>.
	 * 
	 * @throws UnsupportedOperationException
	 *             {@inheritDoc}
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	@Override
	public void put(K key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation iterates over <tt>entrySet()</tt> searching for an
	 * entry with the specified key. If such an entry is found, its value is
	 * obtained with its <tt>getValue</tt> operation, the entry is removed from
	 * the collection (and the backing map) with the iterator's <tt>remove</tt>
	 * operation, and the saved value is returned. If the iteration terminates
	 * without finding such an entry, <tt>null</tt> is returned. Note that this
	 * implementation requires linear time in the size of the map; many
	 * implementations will override this method.
	 * 
	 * <p>
	 * Note that this implementation throws an
	 * <tt>UnsupportedOperationException</tt> if the <tt>entrySet</tt> iterator
	 * does not support the <tt>remove</tt> method and this map contains a
	 * mapping for the specified key.
	 * 
	 * @throws UnsupportedOperationException
	 *             {@inheritDoc}
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 */
	@Override
	public void remove(Object key) {
		Iterator<Entry<K>> i = entrySet().iterator();
		Entry<K> correctEntry = null;
		if (key == null) {
			while (correctEntry == null && i.hasNext()) {
				Entry<K> e = i.next();
				if (e.getKey() == null) {
					correctEntry = e;
				}
			}
		} else {
			while (correctEntry == null && i.hasNext()) {
				Entry<K> e = i.next();
				if (key.equals(e.getKey())) {
					correctEntry = e;
				}
			}
		}
		if (correctEntry != null) {
			i.remove();
		}
	}

	// Bulk Operations

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation iterates over the specified map's <tt>entrySet()</tt>
	 * collection, and calls this map's <tt>put</tt> operation once for each
	 * entry returned by the iteration.
	 * 
	 * <p>
	 * Note that this implementation throws an
	 * <tt>UnsupportedOperationException</tt> if this map does not support the
	 * <tt>put</tt> operation and the specified map is nonempty.
	 * 
	 * @throws UnsupportedOperationException
	 *             {@inheritDoc}
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	@Override
	public void putAll(LMMap<? extends K> m) {
		for (LMMap.Entry<? extends K> e : m.entrySet()) {
			put(e.getKey());
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation calls <tt>entrySet().clear()</tt>.
	 * 
	 * <p>
	 * Note that this implementation throws an
	 * <tt>UnsupportedOperationException</tt> if the <tt>entrySet</tt> does not
	 * support the <tt>clear</tt> operation.
	 * 
	 * @throws UnsupportedOperationException
	 *             {@inheritDoc}
	 */
	@Override
	public void clear() {
		entrySet().clear();
	}

	// Views

	/**
	 * Each of these fields are initialized to contain an instance of the
	 * appropriate view the first time this view is requested. The views are
	 * stateless, so there's no reason to create more than one of each.
	 */
	transient volatile Set<K> keySet = null;

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation returns a set that subclasses {@link AbstractSet}.
	 * The subclass's iterator method returns a "wrapper object" over this map's
	 * <tt>entrySet()</tt> iterator. The <tt>size</tt> method delegates to this
	 * map's <tt>size</tt> method and the <tt>contains</tt> method delegates to
	 * this map's <tt>containsKey</tt> method.
	 * 
	 * <p>
	 * The set is created the first time this method is called, and returned in
	 * response to all subsequent calls. No synchronization is performed, so
	 * there is a slight chance that multiple calls to this method will not all
	 * return the same set.
	 */
	@Override
	public Set<K> keySet() {
		if (keySet == null) {
			keySet = new AbstractSet<K>() {
				@Override
				public Iterator<K> iterator() {
					return new Iterator<K>() {
						private final Iterator<Entry<K>> i = entrySet()
								.iterator();

						@Override
						public boolean hasNext() {
							return i.hasNext();
						}

						@Override
						public K next() {
							return i.next().getKey();
						}

						@Override
						public void remove() {
							i.remove();
						}
					};
				}

				@Override
				public int size() {
					return LMAbstractMap.this.size();
				}

				@Override
				public boolean contains(Object k) {
					return LMAbstractMap.this.containsKey(k);
				}
			};
		}
		return keySet;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation returns a collection that subclasses
	 * {@link AbstractCollection}. The subclass's iterator method returns a
	 * "wrapper object" over this map's <tt>entrySet()</tt> iterator. The
	 * <tt>size</tt> method delegates to this map's <tt>size</tt> method and the
	 * <tt>contains</tt> method delegates to this map's <tt>containsValue</tt>
	 * method.
	 * 
	 * <p>
	 * The collection is created the first time this method is called, and
	 * returned in response to all subsequent calls. No synchronization is
	 * performed, so there is a slight chance that multiple calls to this method
	 * will not all return the same collection.
	 */
	public void values() {
	}

	@Override
	public abstract Set<Entry<K>> entrySet();

	// Comparison and hashing

	/**
	 * Compares the specified object with this map for equality. Returns
	 * <tt>true</tt> if the given object is also a map and the two maps
	 * represent the same mappings. More formally, two maps <tt>m1</tt> and
	 * <tt>m2</tt> represent the same mappings if
	 * <tt>m1.entrySet().equals(m2.entrySet())</tt>. This ensures that the
	 * <tt>equals</tt> method works properly across different implementations of
	 * the <tt>Map</tt> interface.
	 * 
	 * <p>
	 * This implementation first checks if the specified object is this map; if
	 * so it returns <tt>true</tt>. Then, it checks if the specified object is a
	 * map whose size is identical to the size of this map; if not, it returns
	 * <tt>false</tt>. If so, it iterates over this map's <tt>entrySet</tt>
	 * collection, and checks that the specified map contains each mapping that
	 * this map contains. If the specified map fails to contain such a mapping,
	 * <tt>false</tt> is returned. If the iteration completes, <tt>true</tt> is
	 * returned.
	 * 
	 * @param o
	 *            object to be compared for equality with this map
	 * @return <tt>true</tt> if the specified object is equal to this map
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof LMMap)) {
			return false;
		}
		LMMap<K> m = (LMMap<K>) o;
		if (m.size() != size()) {
			return false;
		}

		try {
			Iterator<Entry<K>> i = entrySet().iterator();
			while (i.hasNext()) {
				Entry<K> e = i.next();
				K key = e.getKey();
				if (!m.containsKey(key)) {
					return false;
				}
			}
		} catch (ClassCastException unused) {
			return false;
		} catch (NullPointerException unused) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the hash code value for this map. The hash code of a map is
	 * defined to be the sum of the hash codes of each entry in the map's
	 * <tt>entrySet()</tt> view. This ensures that <tt>m1.equals(m2)</tt>
	 * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
	 * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
	 * {@link Object#hashCode}.
	 * 
	 * <p>
	 * This implementation iterates over <tt>entrySet()</tt>, calling
	 * {@link Map.Entry#hashCode hashCode()} on each element (entry) in the set,
	 * and adding up the results.
	 * 
	 * @return the hash code value for this map
	 * @see Map.Entry#hashCode()
	 * @see Object#equals(Object)
	 * @see Set#equals(Object)
	 */
	@Override
	public int hashCode() {
		int h = 0;
		Iterator<Entry<K>> i = entrySet().iterator();
		while (i.hasNext()) {
			h += i.next().hashCode();
		}
		return h;
	}

	/**
	 * Returns a string representation of this map. The string representation
	 * consists of a list of key-value mappings in the order returned by the
	 * map's <tt>entrySet</tt> view's iterator, enclosed in braces (
	 * <tt>"{}"</tt>). Adjacent mappings are separated by the characters
	 * <tt>", "</tt> (comma and space). Each key-value mapping is rendered as
	 * the key followed by an equals sign (<tt>"="</tt>) followed by the
	 * associated value. Keys and values are converted to strings as by
	 * {@link String#valueOf(Object)}.
	 * 
	 * @return a string representation of this map
	 */
	@Override
	public String toString() {
		Iterator<Entry<K>> i = entrySet().iterator();
		if (!i.hasNext()) {
			return "{}";
		}

		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			Entry<K> e = i.next();
			K key = e.getKey();
			sb.append(key == this ? "(this Map)" : key);
			if (!i.hasNext()) {
				return sb.append('}').toString();
			}
			sb.append(", ");
		}
	}

	/**
	 * Returns a shallow copy of this <tt>AbstractMap</tt> instance: the keys
	 * and values themselves are not cloned.
	 * 
	 * @return a shallow copy of this map
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected Object clone() throws CloneNotSupportedException {
		LMAbstractMap<K> result = (LMAbstractMap<K>) super.clone();
		result.keySet = null;
		return result;
	}

	/**
	 * Utility method for SimpleEntry and SimpleImmutableEntry. Test for
	 * equality, checking for nulls.
	 */
	private static boolean eq(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	// Implementation Note: SimpleEntry and SimpleImmutableEntry
	// are distinct unrelated classes, even though they share
	// some code. Since you can't add or subtract final-ness
	// of a field in a subclass, they can't share representations,
	// and the amount of duplicated code is too small to warrant
	// exposing a common abstract class.

	/**
	 * An Entry maintaining a key and a value. The value may be changed using
	 * the <tt>setValue</tt> method. This class facilitates the process of
	 * building custom map implementations. For example, it may be convenient to
	 * return arrays of <tt>SimpleEntry</tt> instances in method
	 * <tt>Map.entrySet().toArray</tt>.
	 * 
	 * @since 1.6
	 */
	public static class SimpleEntry<K> implements Entry<K>,
			java.io.Serializable {
		private static final long serialVersionUID = -8499721149061103585L;

		private final K key;

		/**
		 * Creates an entry representing a mapping from the specified key to the
		 * specified value.
		 * 
		 * @param key
		 *            the key represented by this entry
		 * @param value
		 *            the value represented by this entry
		 */
		public SimpleEntry(K key) {
			this.key = key;
		}

		/**
		 * Creates an entry representing the same mapping as the specified
		 * entry.
		 * 
		 * @param entry
		 *            the entry to copy
		 */
		public SimpleEntry(Entry<? extends K> entry) {
			this.key = entry.getKey();
		}

		/**
		 * Returns the key corresponding to this entry.
		 * 
		 * @return the key corresponding to this entry
		 */
		@Override
		public K getKey() {
			return key;
		}

		/**
		 * Compares the specified object with this entry for equality. Returns
		 * {@code true} if the given object is also a map entry and the two
		 * entries represent the same mapping. More formally, two entries
		 * {@code e1} and {@code e2} represent the same mapping if
		 * 
		 * <pre>
		 * (e1.getKey() == null ? e2.getKey() == null : e1.getKey().equals(e2.getKey()))
		 * 		&amp;&amp; (e1.getValue() == null ? e2.getValue() == null : e1.getValue()
		 * 				.equals(e2.getValue()))
		 * </pre>
		 * 
		 * This ensures that the {@code equals} method works properly across
		 * different implementations of the {@code Map.Entry} interface.
		 * 
		 * @param o
		 *            object to be compared for equality with this map entry
		 * @return {@code true} if the specified object is equal to this map
		 *         entry
		 * @see #hashCode
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof LMMap.Entry)) {
				return false;
			}
			LMMap.Entry e = (LMMap.Entry) o;
			return eq(key, e.getKey());
		}

		/**
		 * Returns the hash code value for this map entry. The hash code of a
		 * map entry {@code e} is defined to be:
		 * 
		 * <pre>
		 * (e.getKey() == null ? 0 : e.getKey().hashCode())
		 * 		&circ; (e.getValue() == null ? 0 : e.getValue().hashCode())
		 * </pre>
		 * 
		 * This ensures that {@code e1.equals(e2)} implies that
		 * {@code e1.hashCode()==e2.hashCode()} for any two Entries {@code e1}
		 * and {@code e2}, as required by the general contract of
		 * {@link Object#hashCode}.
		 * 
		 * @return the hash code value for this map entry
		 * @see #equals
		 */
		@Override
		public int hashCode() {
			return (key == null ? 0 : key.hashCode());
		}

		/**
		 * Returns a String representation of this map entry. This
		 * implementation returns the string representation of this entry's key
		 * followed by the equals character ("<tt>=</tt>") followed by the
		 * string representation of this entry's value.
		 * 
		 * @return a String representation of this map entry
		 */
		@Override
		public String toString() {
			return key.toString();
		}

	}

	/**
	 * An Entry maintaining an immutable key and value. This class does not
	 * support method <tt>setValue</tt>. This class may be convenient in methods
	 * that return thread-safe snapshots of key-value mappings.
	 * 
	 * @since 1.6
	 */
	public static class SimpleImmutableEntry<K> implements Entry<K>,
			java.io.Serializable {
		private static final long serialVersionUID = 7138329143949025153L;

		private final K key;

		/**
		 * Creates an entry representing a mapping from the specified key to the
		 * specified value.
		 * 
		 * @param key
		 *            the key represented by this entry
		 * @param value
		 *            the value represented by this entry
		 */
		public SimpleImmutableEntry(K key) {
			this.key = key;
		}

		/**
		 * Creates an entry representing the same mapping as the specified
		 * entry.
		 * 
		 * @param entry
		 *            the entry to copy
		 */
		public SimpleImmutableEntry(Entry<? extends K> entry) {
			this.key = entry.getKey();
		}

		/**
		 * Returns the key corresponding to this entry.
		 * 
		 * @return the key corresponding to this entry
		 */
		@Override
		public K getKey() {
			return key;
		}

		/**
		 * Compares the specified object with this entry for equality. Returns
		 * {@code true} if the given object is also a map entry and the two
		 * entries represent the same mapping. More formally, two entries
		 * {@code e1} and {@code e2} represent the same mapping if
		 * 
		 * <pre>
		 * (e1.getKey() == null ? e2.getKey() == null : e1.getKey().equals(e2.getKey()))
		 * 		&amp;&amp; (e1.getValue() == null ? e2.getValue() == null : e1.getValue()
		 * 				.equals(e2.getValue()))
		 * </pre>
		 * 
		 * This ensures that the {@code equals} method works properly across
		 * different implementations of the {@code Map.Entry} interface.
		 * 
		 * @param o
		 *            object to be compared for equality with this map entry
		 * @return {@code true} if the specified object is equal to this map
		 *         entry
		 * @see #hashCode
		 */
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof LMMap.Entry)) {
				return false;
			}
			LMMap.Entry e = (LMMap.Entry) o;
			return eq(key, e.getKey());
		}

		/**
		 * Returns the hash code value for this map entry. The hash code of a
		 * map entry {@code e} is defined to be:
		 * 
		 * <pre>
		 * (e.getKey() == null ? 0 : e.getKey().hashCode())
		 * 		&circ; (e.getValue() == null ? 0 : e.getValue().hashCode())
		 * </pre>
		 * 
		 * This ensures that {@code e1.equals(e2)} implies that
		 * {@code e1.hashCode()==e2.hashCode()} for any two Entries {@code e1}
		 * and {@code e2}, as required by the general contract of
		 * {@link Object#hashCode}.
		 * 
		 * @return the hash code value for this map entry
		 * @see #equals
		 */
		@Override
		public int hashCode() {
			return (key == null ? 0 : key.hashCode());
		}

		/**
		 * Returns a String representation of this map entry. This
		 * implementation returns the string representation of this entry's key
		 * followed by the equals character ("<tt>=</tt>") followed by the
		 * string representation of this entry's value.
		 * 
		 * @return a String representation of this map entry
		 */
		@Override
		public String toString() {
			return key.toString();
		}

	}

}
