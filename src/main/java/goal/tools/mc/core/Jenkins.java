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

package goal.tools.mc.core;

/**
 * Represents Jenkins' hash function <tt>lookup3</tt>. The function was
 * originally written in C by Jenkins. This Java implementation is based on the
 * C code, which can be found at http://www.burtleburtle.net/bob/c/lookup3.c.
 * 
 * @author sungshik
 */
public class Jenkins {

	/**
	 * DEADBEEF variable.
	 */
	public static final int DEADBEEF = 1588444911;

	/**
	 * Rotates the integer <code>x</code> <code>k</code> bits to the left. The
	 * implementation is based on Jenkins' C code.
	 * 
	 * @param x
	 *            - The integer to be rotated.
	 * @param k
	 *            - The number of bits to rotate.
	 * @return The rotated integer.
	 */
	private static int rot(int x, int k) {
		return (x << k) | (x >>> (32 - k));
	}

	/**
	 * Mixes <code>a</code>, <code>b</code>, and <code>c</code> by performing
	 * subtractions, multiplications, rotations and additions. The
	 * implementation is based on Jenkins' C code (see class header for
	 * reference).
	 * 
	 * @param a
	 *            - First integer seed.
	 * @param b
	 *            - Second integer seed.
	 * @param c
	 *            - Third integer seed.
	 * @return Three new values based on <code>a</code>, <code>b</code>, and
	 *         <code>c</code>.
	 */
	private static int[] mix(int a, int b, int c) {
		a -= c;
		a ^= rot(c, 4);
		c += b;
		b -= a;
		b ^= rot(a, 6);
		a += c;
		c -= b;
		c ^= rot(b, 8);
		b += a;
		a -= c;
		a ^= rot(c, 16);
		c += b;
		b -= a;
		b ^= rot(a, 19);
		a += c;
		c -= b;
		c ^= rot(b, 4);
		b += a;
		int[] array = { a, b, c };
		return array;
	}

	/**
	 * Finalizes <code>a</code>, <code>b</code>, and <code>c</code> by
	 * performing multiplications and rotations. The implementation is based on
	 * Jenkins' C code (see class header for reference).
	 * 
	 * @param a
	 *            - First integer seed.
	 * @param b
	 *            - Second integer seed.
	 * @param c
	 *            - Third integer seed.
	 * @return Three new values based on <code>a</code>, <code>b</code>, and
	 *         <code>c</code>.
	 */
	private static int[] f1nal(int a, int b, int c) {
		c ^= b;
		c -= rot(b, 14);
		a ^= c;
		a -= rot(c, 11);
		b ^= a;
		b -= rot(a, 25);
		c ^= b;
		c -= rot(b, 16);
		a ^= c;
		a -= rot(c, 4);
		b ^= a;
		b -= rot(a, 14);
		c ^= b;
		c -= rot(b, 24);
		int[] array = { a, b, c };
		return array;
	}

	/**
	 * 
	 * Apply Jenkins' hash function. It assumes a big endian representation,
	 * which is justified since the JVM uses big endian. The implementation is
	 * based on Jenkins' C code.
	 * 
	 * @param key
	 *            - The key to compute the hash code(s) for.
	 * @param length
	 *            - The length of the key.
	 * @param pc
	 *            - The seed for the first hash code.
	 * @param pb
	 *            - The seed for the second hash code.
	 * @return Two hash codes for <tt>key</tt>
	 */
	public static int[] apply(byte[] key, int pc, int pb) {

		/* Set up internal state */
		int length = key.length;
		int a, b, c;
		a = DEADBEEF + length + pc;
		b = DEADBEEF + length + pc;
		c = DEADBEEF + length + pc + pb;

		/* Read the key one byte at a time */
		int k = 0;
		while (length > 12) {
			a += key[k];
			a += (key[k + 1]) << 24;
			a += (key[k + 2]) << 16;
			a += (key[k + 3]) << 8;
			b += key[k + 4];
			b += (key[k + 5]) << 24;
			b += (key[k + 6]) << 16;
			b += (key[k + 7]) << 8;
			c += key[k + 8];
			c += (key[k + 9]) << 24;
			c += (key[k + 10]) << 16;
			c += (key[k + 11]) << 8;
			int[] abc = mix(a, b, c);
			a = abc[0];
			b = abc[1];
			c = abc[2];
			length -= 12;
			k += 12;
		}

		/* Note: all the case statements fall through */
		switch (length) {
		case 12:
			c += (key[k + 11]) << 8;
		case 11:
			c += (key[k + 10]) << 16;
		case 10:
			c += (key[k + 9]) << 24;
		case 9:
			c += key[k + 8];
		case 8:
			b += (key[k + 7]) << 8;
		case 7:
			b += (key[k + 6]) << 16;
		case 6:
			b += (key[k + 5]) << 24;
		case 5:
			b += key[k + 4];
		case 4:
			a += (key[k + 3]) << 8;
		case 3:
			a += (key[k + 2]) << 16;
		case 2:
			a += (key[k + 1]) << 24;
		case 1:
			a += key[k];
			break;

		/* Zero length strings require no mixing */
		case 0:
			int[] array = { c + (c < 0 ? Integer.MAX_VALUE : 0),
					b + (b < 0 ? Integer.MAX_VALUE : 0) };
			return array;
		}

		int[] abc = f1nal(a, b, c);
		if (abc[1] < 0) {
			abc[1] = abc[1] + Integer.MAX_VALUE;
		}
		if (abc[2] < 0) {
			abc[2] = abc[2] + Integer.MAX_VALUE;
		}
		int[] array = { abc[2], abc[1] };
		return array;
	}
}