package goal.util;

import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Simple util for some map support functions. Maybe we can use a better library
 * for this.
 *
 * @author W.Pasman
 */
public class MapUtils {
	/**
	 * Find the first key in the map that maps to the given value. This is a
	 * simple implementation using a for loop. Should be used only where
	 * performance is not critical, or where the map is small.
	 *
	 * @param <K>
	 *            the type of the keys in the map.
	 * @param <V>
	 *            the type of values in the map
	 * @param map
	 *            the map to search
	 * @param value
	 *            the value that is looked for.
	 * @return the first key in the map that maps to given value.
	 * @throws NoSuchElementException
	 *             if no such value in the map.
	 */
	public static <K, V> K keyFromValue(Map<K, V> map, V value) {
		for (K key : map.keySet()) {
			if (map.get(key) == value) {
				return key;
			}
		}
		throw new NoSuchElementException("Map " + map
				+ " does not contain value " + value);
	}
}
