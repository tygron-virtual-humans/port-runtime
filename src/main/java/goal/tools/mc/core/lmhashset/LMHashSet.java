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

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A "less memory" version (hence "LM") of {@link java.util.HashSet}, whose
 * implementation is largely based on {@link java.util.HashMap}. In fact, this
 * is a {@link java.util.HashMap} whose each entry is a key rather than a key-
 * value pair (plus some additional methods).
 * 
 * @author sungshik
 * 
 */
@SuppressWarnings(value = { "unchecked", "serial" })
public class LMHashSet<K> extends LMAbstractMap<K> implements LMMap<K>,
		Cloneable, Serializable, Iterable<K> {

	//
	// Added code
	//

	/**
	 * Adds an object to the set.
	 * 
	 * @param key
	 *            - The object to be added.
	 */
	public void add(K key) {
		put(key);
	}

	/**
	 * Adds a set of objects to the set.
	 * 
	 * @param set
	 *            - The objects to be added.
	 */
	public void addAll(LMHashSet<K> set) {
		putAll(set);
	}

	/**
	 * Checks if the set contains an object.
	 * 
	 * @param key
	 *            - The object to check inclusion for.
	 * @return <code>true</code> if the object is contained; <code>false</code>
	 *         otherwise.
	 */
	public boolean contains(K key) {
		return containsKey(key);
	}

	/**
	 * Checks if the set contains all objects in another set.
	 * 
	 * @param set
	 *            - The set of other objects.
	 * @return <code>true</code> if all objects in <code>set</code> are
	 *         contained in this set; <code>false</code> otherwise.
	 */
	public boolean containsAll(LMHashSet<K> set) {
		Iterator<K> e = set.iterator();
		while (e.hasNext()) {
			if (!contains(e.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the set contains an object, and returns the existing object if
	 * so.
	 * 
	 * @param key
	 *            - The object to check inclusion for.
	 * @return The existing included object if one exists; <code>null</code>
	 *         otherwise.
	 */
	public K containsAndGet(K key) {
		Entry<K> e = getEntry(key);
		return (e == null ? null : e.getKey());
	}

	@Override
	public Iterator<K> iterator() {
		return keySet().iterator();
	}

	/**
	 * Removes all elements in another set from this set.
	 * 
	 * @param set
	 *            - The set whose elements should be removed from this set.
	 * @return <code>true</code> if this set is modified, i.e. if at least one
	 *         element is removed; <code>false</code> otherwise.
	 */
	public boolean removeAll(LMHashSet<K> set) {
		boolean modified = false;
		Iterator<K> e = iterator();
		while (e.hasNext()) {
			if (set.contains(e.next())) {
				e.remove();
				modified = true;
			}
		}
		return modified;
	}

	//
	// Existing code (yet stripped to remove values from entries)
	//

	/**
	 * The default initial capacity - MUST be a power of two.
	 */
	static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified by
	 * either of the constructors with arguments. MUST be a power of two <=
	 * 1<<30.
	 */
	static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The load factor used when none specified in constructor.
	 */
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * The table, resized as necessary. Length MUST Always be a power of two.
	 */
	transient Entry[] table;

	/**
	 * The number of key-value mappings contained in this map.
	 */
	transient int size;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 * 
	 * @serial
	 */
	int threshold;

	/**
	 * The load factor for the hash table.
	 * 
	 * @serial
	 */
	final float loadFactor;

	/**
	 * The number of times this HashMap has been structurally modified
	 * Structural modifications are those that change the number of mappings in
	 * the HashMap or otherwise modify its internal structure (e.g., rehash).
	 * This field is used to make iterators on Collection-views of the HashMap
	 * fail-fast. (See ConcurrentModificationException).
	 */
	transient volatile int modCount;

	/**
	 * Constructs an empty <tt>HashMap</tt> with the specified initial capacity
	 * and load factor.
	 * 
	 * @param initialCapacity
	 *            the initial capacity
	 * @param loadFactor
	 *            the load factor
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative or the load factor is
	 *             nonpositive
	 */
	public LMHashSet(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal initial capacity: "
					+ initialCapacity);
		}
		if (initialCapacity > MAXIMUM_CAPACITY) {
			initialCapacity = MAXIMUM_CAPACITY;
		}
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
			throw new IllegalArgumentException("Illegal load factor: "
					+ loadFactor);
		}

		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity) {
			capacity <<= 1;
		}

		this.loadFactor = loadFactor;
		threshold = (int) (capacity * loadFactor);
		table = new Entry[capacity];
		init();
	}

	/**
	 * Constructs an empty <tt>HashMap</tt> with the specified initial capacity
	 * and the default load factor (0.75).
	 * 
	 * @param initialCapacity
	 *            the initial capacity.
	 * @throws IllegalArgumentException
	 *             if the initial capacity is negative.
	 */
	public LMHashSet(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs an empty <tt>HashMap</tt> with the default initial capacity
	 * (16) and the default load factor (0.75).
	 */
	public LMHashSet() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
		table = new Entry[DEFAULT_INITIAL_CAPACITY];
		init();
	}

	/**
	 * Constructs a new <tt>HashMap</tt> with the same mappings as the specified
	 * <tt>Map</tt>. The <tt>HashMap</tt> is created with default load factor
	 * (0.75) and an initial capacity sufficient to hold the mappings in the
	 * specified <tt>Map</tt>.
	 * 
	 * @param m
	 *            the map whose mappings are to be placed in this map
	 * @throws NullPointerException
	 *             if the specified map is null
	 */
	public LMHashSet(LMMap<? extends K> m) {
		this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
		putAllForCreate(m);
	}

	// internal utilities

	/**
	 * Initialization hook for subclasses. This method is called in all
	 * constructors and pseudo-constructors (clone, readObject) after HashMap
	 * has been initialized but before any entries have been inserted. (In the
	 * absence of this method, readObject would require explicit knowledge of
	 * subclasses.)
	 */
	void init() {
	}

	/**
	 * Applies a supplemental hash function to a given hashCode, which defends
	 * against poor quality hash functions. This is critical because HashMap
	 * uses power-of-two length hash tables, that otherwise encounter collisions
	 * for hashCodes that do not differ in lower bits. Note: Null keys always
	 * map to hash 0, thus index 0.
	 */
	static int hash(int h) {
		// This function ensures that hashCodes that differ only by
		// constant multiples at each bit position have a bounded
		// number of collisions (approximately 8 at default load factor).
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	/**
	 * Returns index for hash code h.
	 */
	static int indexFor(int h, int length) {
		return h & (length - 1);
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * 
	 * @return the number of key-value mappings in this map
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Returns <tt>true</tt> if this map contains no key-value mappings.
	 * 
	 * @return <tt>true</tt> if this map contains no key-value mappings
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the value to which the specified key is mapped, or {@code null}
	 * if this map contains no mapping for the key.
	 * 
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a
	 * value {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise it returns
	 * {@code null}. (There can be at most one such mapping.)
	 * 
	 * <p>
	 * A return value of {@code null} does not <i>necessarily</i> indicate that
	 * the map contains no mapping for the key; it's also possible that the map
	 * explicitly maps the key to {@code null}. The {@link #containsKey
	 * containsKey} operation may be used to distinguish these two cases.
	 * 
	 * @see #put(Object, Object)
	 */
	@Override
	public K get(Object key) {
		if (key == null) {
			return null;
		}
		int hash = hash(key.hashCode());
		for (Entry<K> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
				return e.key;
			}
		}
		return null;
	}

	/**
	 * Returns <tt>true</tt> if this map contains a mapping for the specified
	 * key.
	 * 
	 * @param key
	 *            The key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 *         key.
	 */
	@Override
	public boolean containsKey(Object key) {
		return getEntry(key) != null;
	}

	/**
	 * Returns the entry associated with the specified key in the HashMap.
	 * Returns null if the HashMap contains no mapping for the key.
	 */
	final Entry<K> getEntry(Object key) {
		int hash = (key == null) ? 0 : hash(key.hashCode());
		for (Entry<K> e = table[indexFor(hash, table.length)]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash
					&& ((k = e.key) == key || (key != null && key.equals(k)))) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Associates the specified value with the specified key in this map. If the
	 * map previously contained a mapping for the key, the old value is
	 * replaced.
	 * 
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>.)
	 */
	@Override
	public void put(K key) {
		if (key == null) {
			return;
		}
		int hash = hash(key.hashCode());
		int i = indexFor(hash, table.length);
		for (Entry<K> e = table[i]; e != null; e = e.next) {
			Object k;
			if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
				e.recordAccess(this);
				return;
			}
		}
		modCount++;
		addEntry(hash, key, i);
	}

	/**
	 * This method is used instead of put by constructors and pseudoconstructors
	 * (clone, readObject). It does not resize the table, check for
	 * comodification, etc. It calls createEntry rather than addEntry.
	 */
	private void putForCreate(K key) {
		int hash = (key == null) ? 0 : hash(key.hashCode());
		int i = indexFor(hash, table.length);

		/**
		 * Look for preexisting entry for key. This will never happen for clone
		 * or deserialize. It will only happen for construction if the input Map
		 * is a sorted map whose ordering is inconsistent w/ equals.
		 */
		for (Entry<K> e = table[i]; e != null; e = e.next) {
			Object k;

			if (e.hash == hash
					&& ((k = e.key) == key || (key != null && key.equals(k)))) {
				return;
			}
		}

		createEntry(hash, key, i);
	}

	private void putAllForCreate(LMMap<? extends K> m) {
		for (goal.tools.mc.core.lmhashset.LMMap.Entry<? extends K> e : m
				.entrySet()) {
			putForCreate(e.getKey());
		}
	}

	/**
	 * Rehashes the contents of this map into a new array with a larger
	 * capacity. This method is called automatically when the number of keys in
	 * this map reaches its threshold.
	 * 
	 * If current capacity is MAXIMUM_CAPACITY, this method does not resize the
	 * map, but sets threshold to Integer.MAX_VALUE. This has the effect of
	 * preventing future calls.
	 * 
	 * @param newCapacity
	 *            the new capacity, MUST be a power of two; must be greater than
	 *            current capacity unless current capacity is MAXIMUM_CAPACITY
	 *            (in which case value is irrelevant).
	 */
	void resize(int newCapacity) {
		Entry[] oldTable = table;
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		Entry[] newTable = new Entry[newCapacity];
		transfer(newTable);
		table = newTable;
		threshold = (int) (newCapacity * loadFactor);
	}

	/**
	 * Transfers all entries from current table to newTable.
	 */
	void transfer(Entry[] newTable) {
		Entry[] src = table;
		int newCapacity = newTable.length;
		for (int j = 0; j < src.length; j++) {
			Entry<K> e = src[j];
			if (e != null) {
				src[j] = null;
				do {
					Entry<K> next = e.next;
					int i = indexFor(e.hash, newCapacity);
					e.next = newTable[i];
					newTable[i] = e;
					e = next;
				} while (e != null);
			}
		}
	}

	/**
	 * Copies all of the mappings from the specified map to this map. These
	 * mappings will replace any mappings that this map had for any of the keys
	 * currently in the specified map.
	 * 
	 * @param m
	 *            mappings to be stored in this map
	 * @throws NullPointerException
	 *             if the specified map is null
	 */
	@Override
	public void putAll(LMMap<? extends K> m) {
		int numKeysToBeAdded = m.size();
		if (numKeysToBeAdded == 0) {
			return;
		}

		/*
		 * Expand the map if the map if the number of mappings to be added is
		 * greater than or equal to threshold. This is conservative; the obvious
		 * condition is (m.size() + size) >= threshold, but this condition could
		 * result in a map with twice the appropriate capacity, if the keys to
		 * be added overlap with the keys already in this map. By using the
		 * conservative calculation, we subject ourself to at most one extra
		 * resize.
		 */
		if (numKeysToBeAdded > threshold) {
			int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
			if (targetCapacity > MAXIMUM_CAPACITY) {
				targetCapacity = MAXIMUM_CAPACITY;
			}
			int newCapacity = table.length;
			while (newCapacity < targetCapacity) {
				newCapacity <<= 1;
			}
			if (newCapacity > table.length) {
				resize(newCapacity);
			}
		}

		for (goal.tools.mc.core.lmhashset.LMMap.Entry<? extends K> e : m
				.entrySet()) {
			put(e.getKey());
		}
	}

	/**
	 * Removes the mapping for the specified key from this map if present.
	 * 
	 * @param key
	 *            key whose mapping is to be removed from the map
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>.)
	 */
	@Override
	public void remove(Object key) {
		removeEntryForKey(key);
	}

	/**
	 * Removes and returns the entry associated with the specified key in the
	 * HashMap. Returns null if the HashMap contains no mapping for this key.
	 */
	final Entry<K> removeEntryForKey(Object key) {
		int hash = (key == null) ? 0 : hash(key.hashCode());
		int i = indexFor(hash, table.length);
		Entry<K> prev = table[i];
		Entry<K> e = prev;

		while (e != null) {
			Entry<K> next = e.next;
			Object k;
			if (e.hash == hash
					&& ((k = e.key) == key || (key != null && key.equals(k)))) {
				modCount++;
				size--;
				if (prev == e) {
					table[i] = next;
				} else {
					prev.next = next;
				}
				e.recordRemoval(this);
				return e;
			}
			prev = e;
			e = next;
		}

		return e;
	}

	/**
	 * Special version of remove for EntrySet.
	 */
	final Entry<K> removeMapping(Object o) {
		if (!(o instanceof LMMap.Entry)) {
			return null;
		}
		LMMap.Entry<K> entry = (LMMap.Entry<K>) o;
		Object key = entry.getKey();
		int hash = (key == null) ? 0 : hash(key.hashCode());
		int i = indexFor(hash, table.length);
		Entry<K> prev = table[i];
		Entry<K> e = prev;
		while (e != null) {
			Entry<K> next = e.next;
			if (e.hash == hash && e.equals(entry)) {
				modCount++;
				size--;
				if (prev == e) {
					table[i] = next;
				} else {
					prev.next = next;
				}
				e.recordRemoval(this);
				return e;
			}
			prev = e;
			e = next;
		}
		return e;
	}

	/**
	 * Removes all of the mappings from this map. The map will be empty after
	 * this call returns.
	 */
	@Override
	public void clear() {
		modCount++;
		Entry[] tab = table;
		for (int i = 0; i < tab.length; i++) {
			tab[i] = null;
		}
		size = 0;
	}

	static class Entry<K> implements LMMap.Entry<K> {
		final K key;
		Entry<K> next;
		final int hash;

		/**
		 * Creates new entry.
		 */
		Entry(int h, K k, Entry<K> n) {
			next = n;
			key = k;
			hash = h;
		}

		@Override
		public final K getKey() {
			return key;
		}

		@Override
		public final boolean equals(Object o) {
			if (!(o instanceof Map.Entry)) {
				return false;
			}
			Map.Entry e = (Map.Entry) o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				return true;
			}
			return false;
		}

		@Override
		public final int hashCode() {
			return (key == null ? 0 : key.hashCode());
		}

		@Override
		public final String toString() {
			return getKey().toString();
		}

		/**
		 * This method is invoked whenever the value in an entry is overwritten
		 * by an invocation of put(k,v) for a key k that's already in the
		 * HashMap.
		 */
		void recordAccess(LMHashSet<K> m) {
		}

		/**
		 * This method is invoked whenever the entry is removed from the table.
		 */
		void recordRemoval(LMHashSet<K> m) {
		}
	}

	/**
	 * Adds a new entry with the specified key, value and hash code to the
	 * specified bucket. It is the responsibility of this method to resize the
	 * table if appropriate.
	 * 
	 * Subclass overrides this to alter the behavior of put method.
	 */
	void addEntry(int hash, K key, int bucketIndex) {
		Entry<K> e = table[bucketIndex];

		table[bucketIndex] = new Entry<K>(hash, key, e);

		if (size++ >= threshold) {
			resize(2 * table.length);
		}
	}

	/**
	 * Like addEntry except that this version is used when creating entries as
	 * part of Map construction or "pseudo-construction" (cloning,
	 * deserialization). This version needn't worry about resizing the table.
	 * 
	 * Subclass overrides this to alter the behavior of HashMap(Map), clone, and
	 * readObject.
	 */
	void createEntry(int hash, K key, int bucketIndex) {
		Entry<K> e = table[bucketIndex];
		table[bucketIndex] = new Entry<K>(hash, key, e);
		size++;
	}

	private abstract class HashIterator<E> implements Iterator<E> {
		Entry<K> next; // next entry to return
		int expectedModCount; // For fast-fail
		int index; // current slot
		Entry<K> current; // current entry

		HashIterator() {
			expectedModCount = modCount;
			if (size > 0) { // advance to first entry
				Entry[] t = table;
				while (index < t.length) {
					if ((next = t[index++]) != null) {
						break;
					}
				}
			}
		}

		@Override
		public final boolean hasNext() {
			return next != null;
		}

		final Entry<K> nextEntry() {
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			Entry<K> e = next;
			if (e == null) {
				throw new NoSuchElementException();
			}
			if ((next = e.next) == null) {
				Entry[] t = table;
				while (index < t.length) {
					if ((next = t[index++]) != null) {
						break;
					}
				}
			}
			current = e;
			return e;
		}

		@Override
		public void remove() {
			if (current == null) {
				throw new IllegalStateException();
			}
			if (modCount != expectedModCount) {
				throw new ConcurrentModificationException();
			}
			Object k = current.key;
			current = null;
			LMHashSet.this.removeEntryForKey(k);
			expectedModCount = modCount;
		}

	}

	private final class KeyIterator extends HashIterator<K> {
		@Override
		public K next() {
			return nextEntry().getKey();
		}
	}

	private final class EntryIterator extends HashIterator<LMMap.Entry<K>> {
		@Override
		public LMMap.Entry<K> next() {
			return nextEntry();
		}
	}

	// Subclass overrides these to alter behavior of views' iterator() method
	Iterator<K> newKeyIterator() {
		return new KeyIterator();
	}

	Iterator<LMMap.Entry<K>> newEntryIterator() {
		return new EntryIterator();
	}

	// Views

	private transient Set<LMMap.Entry<K>> entrySet = null;

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
	 */
	@Override
	public Set<K> keySet() {
		Set<K> ks = keySet;
		return (ks != null ? ks : (keySet = new KeySet()));
	}

	private final class KeySet extends AbstractSet<K> {
		@Override
		public Iterator<K> iterator() {
			return newKeyIterator();
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		public boolean remove(Object o) {
			return LMHashSet.this.removeEntryForKey(o) != null;
		}

		@Override
		public void clear() {
			LMHashSet.this.clear();
		}
	}

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
	@Override
	public Set<LMMap.Entry<K>> entrySet() {
		return entrySet0();
	}

	private Set<LMMap.Entry<K>> entrySet0() {
		Set<LMMap.Entry<K>> es = entrySet;
		return es != null ? es : (entrySet = new EntrySet());
	}

	private final class EntrySet extends AbstractSet<LMMap.Entry<K>> {
		@Override
		public Iterator<LMMap.Entry<K>> iterator() {
			return newEntryIterator();
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof LMMap.Entry)) {
				return false;
			}
			LMMap.Entry<K> e = (LMMap.Entry<K>) o;
			Entry<K> candidate = getEntry(e.getKey());
			return candidate != null && candidate.equals(e);
		}

		@Override
		public boolean remove(Object o) {
			return removeMapping(o) != null;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public void clear() {
			LMHashSet.this.clear();
		}
	}
}
