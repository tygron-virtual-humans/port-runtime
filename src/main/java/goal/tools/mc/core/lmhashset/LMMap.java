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

import java.util.Map;
import java.util.Set;

/**
 * A "less memory" version (hence "LM") of {@link java.util.Map}, whose each
 * entry is a key rather than a key-value pair.
 * 
 * @author sungshik
 * 
 */
public interface LMMap<K> {
	// Query Operations

	/**
	 * Returns the number of key-value mappings in this map. If the map contains
	 * more than <tt>Integer.MAX_VALUE</tt> elements, returns
	 * <tt>Integer.MAX_VALUE</tt>.
	 *
	 * @return the number of key-value mappings in this map
	 */
	int size();

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 *
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	boolean isEmpty();

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified
	 * key. More formally, returns <tt>true</tt> if and only if this map
	 * contains a mapping for a key <tt>k</tt> such that
	 * <tt>(key==null ? k==null : key.equals(k))</tt>. (There can be at most one
	 * such mapping.)
	 *
	 * @param key
	 *            key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 *         key
	 * @throws ClassCastException
	 *             if the key is of an inappropriate type for this map
	 *             (optional)
	 * @throws NullPointerException
	 *             if the specified key is null and this map does not permit
	 *             null keys (optional)
	 */
	boolean containsKey(Object key);

	/**
	 * Returns <tt>true</tt> if this map maps one or more keys to the specified
	 * value. More formally, returns <tt>true</tt> if and only if this map
	 * contains at least one mapping to a value <tt>v</tt> such that
	 * <tt>(value==null ? v==null : value.equals(v))</tt>. This operation will
	 * probably require time linear in the map size for most implementations of
	 * the <tt>Map</tt> interface.
	 *
	 * @param value
	 *            value whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map maps one or more keys to the specified
	 *         value
	 * @throws ClassCastException
	 *             if the value is of an inappropriate type for this map
	 *             (optional)
	 * @throws NullPointerException
	 *             if the specified value is null and this map does not permit
	 *             null values (optional)
	 */
	boolean containsValue(Object value);

	// Modification Operations

	/**
	 * Associates the specified value with the specified key in this map
	 * (optional operation). If the map previously contained a mapping for the
	 * key, the old value is replaced by the specified value. (A map <tt>m</tt>
	 * is said to contain a mapping for a key <tt>k</tt> if and only if
	 * {@link #containsKey(Object) m.containsKey(k)} would return <tt>true</tt>
	 * .)
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>, if the implementation supports
	 *         <tt>null</tt> values.)
	 * @throws UnsupportedOperationException
	 *             if the <tt>put</tt> operation is not supported by this map
	 * @throws ClassCastException
	 *             if the class of the specified key or value prevents it from
	 *             being stored in this map
	 * @throws NullPointerException
	 *             if the specified key or value is null and this map does not
	 *             permit null keys or values
	 * @throws IllegalArgumentException
	 *             if some property of the specified key or value prevents it
	 *             from being stored in this map
	 */
	void put(K key);

	/**
	 * Removes the mapping for a key from this map if it is present (optional
	 * operation). More formally, if this map contains a mapping from key
	 * <tt>k</tt> to value <tt>v</tt> such that
	 * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping is
	 * removed. (The map can contain at most one such mapping.)
	 *
	 * <p>
	 * Returns the value to which this map previously associated the key, or
	 * <tt>null</tt> if the map contained no mapping for the key.
	 *
	 * <p>
	 * If this map permits null values, then a return value of <tt>null</tt>
	 * does not <i>necessarily</i> indicate that the map contained no mapping
	 * for the key; it's also possible that the map explicitly mapped the key to
	 * <tt>null</tt>.
	 *
	 * <p>
	 * The map will not contain a mapping for the specified key once the call
	 * returns.
	 *
	 * @param key
	 *            key whose mapping is to be removed from the map
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>.
	 * @throws UnsupportedOperationException
	 *             if the <tt>remove</tt> operation is not supported by this map
	 * @throws ClassCastException
	 *             if the key is of an inappropriate type for this map
	 *             (optional)
	 * @throws NullPointerException
	 *             if the specified key is null and this map does not permit
	 *             null keys (optional)
	 */
	void remove(Object key);

	// Bulk Operations

	/**
	 * Copies all of the mappings from the specified map to this map (optional
	 * operation). The effect of this call is equivalent to that of calling
	 * {@link #put(Object,Object) put(k, v)} on this map once for each mapping
	 * from key <tt>k</tt> to value <tt>v</tt> in the specified map. The
	 * behavior of this operation is undefined if the specified map is modified
	 * while the operation is in progress.
	 *
	 * @param m
	 *            mappings to be stored in this map
	 * @throws UnsupportedOperationException
	 *             if the <tt>putAll</tt> operation is not supported by this map
	 * @throws ClassCastException
	 *             if the class of a key or value in the specified map prevents
	 *             it from being stored in this map
	 * @throws NullPointerException
	 *             if the specified map is null, or if this map does not permit
	 *             null keys or values, and the specified map contains null keys
	 *             or values
	 * @throws IllegalArgumentException
	 *             if some property of a key or value in the specified map
	 *             prevents it from being stored in this map
	 */
	void putAll(LMMap<? extends K> m);

	/**
	 * Removes all of the mappings from this map (optional operation). The map
	 * will be empty after this call returns.
	 *
	 * @throws UnsupportedOperationException
	 *             if the <tt>clear</tt> operation is not supported by this map
	 */
	void clear();

	// Views

	/**
	 * Returns a {@link Set} view of the keys contained in this map. The set is
	 * backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own <tt>remove</tt> operation),
	 * the results of the iteration are undefined. The set supports element
	 * removal, which removes the corresponding mapping from the map, via the
	 * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>,
	 * <tt>retainAll</tt>, and <tt>clear</tt> operations. It does not support
	 * the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the keys contained in this map
	 */
	Set<K> keySet();

	/**
	 * Returns a {@link Set} view of the mappings contained in this map. The set
	 * is backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own <tt>remove</tt> operation, or
	 * through the <tt>setValue</tt> operation on a map entry returned by the
	 * iterator) the results of the iteration are undefined. The set supports
	 * element removal, which removes the corresponding mapping from the map,
	 * via the <tt>Iterator.remove</tt>, <tt>Set.remove</tt>, <tt>removeAll</tt>
	 * , <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support
	 * the <tt>add</tt> or <tt>addAll</tt> operations.
	 *
	 * @return a set view of the mappings contained in this map
	 */
	Set<LMMap.Entry<K>> entrySet();

	/**
	 * A map entry (key-value pair). The <tt>Map.entrySet</tt> method returns a
	 * collection-view of the map, whose elements are of this class. The
	 * <i>only</i> way to obtain a reference to a map entry is from the iterator
	 * of this collection-view. These <tt>Map.Entry</tt> objects are valid
	 * <i>only</i> for the duration of the iteration; more formally, the
	 * behavior of a map entry is undefined if the backing map has been modified
	 * after the entry was returned by the iterator, except through the
	 * <tt>setValue</tt> operation on the map entry.
	 *
	 * @see Map#entrySet()
	 * @since 1.2
	 */
	interface Entry<K> {
		/**
		 * Returns the key corresponding to this entry.
		 *
		 * @return the key corresponding to this entry
		 * @throws IllegalStateException
		 *             implementations may, but are not required to, throw this
		 *             exception if the entry has been removed from the backing
		 *             map.
		 */
		K getKey();

		/**
		 * Compares the specified object with this entry for equality. Returns
		 * <tt>true</tt> if the given object is also a map entry and the two
		 * entries represent the same mapping. More formally, two entries
		 * <tt>e1</tt> and <tt>e2</tt> represent the same mapping if
		 * 
		 * <pre>
		 * (e1.getKey() == null ? e2.getKey() == null : e1.getKey().equals(e2.getKey()))
		 * 		&amp;&amp; (e1.getValue() == null ? e2.getValue() == null : e1.getValue()
		 * 				.equals(e2.getValue()))
		 * </pre>
		 * 
		 * This ensures that the <tt>equals</tt> method works properly across
		 * different implementations of the <tt>Map.Entry</tt> interface.
		 *
		 * @param o
		 *            object to be compared for equality with this map entry
		 * @return <tt>true</tt> if the specified object is equal to this map
		 *         entry
		 */
		@Override
		boolean equals(Object o);

		/**
		 * Returns the hash code value for this map entry. The hash code of a
		 * map entry <tt>e</tt> is defined to be:
		 * 
		 * <pre>
		 * (e.getKey() == null ? 0 : e.getKey().hashCode())
		 * 		&circ; (e.getValue() == null ? 0 : e.getValue().hashCode())
		 * </pre>
		 * 
		 * This ensures that <tt>e1.equals(e2)</tt> implies that
		 * <tt>e1.hashCode()==e2.hashCode()</tt> for any two Entries <tt>e1</tt>
		 * and <tt>e2</tt>, as required by the general contract of
		 * <tt>Object.hashCode</tt>.
		 *
		 * @return the hash code value for this map entry
		 * @see Object#hashCode()
		 * @see Object#equals(Object)
		 * @see #equals(Object)
		 */
		@Override
		int hashCode();
	}

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
	 * @param o
	 *            object to be compared for equality with this map
	 * @return <tt>true</tt> if the specified object is equal to this map
	 */
	@Override
	boolean equals(Object o);

	/**
	 * Returns the hash code value for this map. The hash code of a map is
	 * defined to be the sum of the hash codes of each entry in the map's
	 * <tt>entrySet()</tt> view. This ensures that <tt>m1.equals(m2)</tt>
	 * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
	 * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
	 * {@link Object#hashCode}.
	 *
	 * @return the hash code value for this map
	 * @see Map.Entry#hashCode()
	 * @see Object#equals(Object)
	 * @see #equals(Object)
	 */
	@Override
	int hashCode();
}
