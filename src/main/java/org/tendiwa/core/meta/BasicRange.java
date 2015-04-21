package org.tendiwa.core.meta;

/**
 * Represents a set of all integers between some minimum and maximum values inclusive.
 *
 * @author suseika
 */
public class BasicRange implements Range {
	public final int min;
	public final int max;

	/**
	 * Creates a new range defined by minimum and maximum value
	 *
	 * @param min
	 * @param max
	 * @throws IllegalArgumentException
	 * 	if min > max
	 */
	public BasicRange(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("min must be <= max");
		}
		this.min = min;
		this.max = max;
	}

	public String toString() {
		return "[" + min + "," + max + "]";
	}

	@Override
	public boolean contains(int value) {
		return value >= min && value <= max;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + max;
		result = prime * result + min;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicRange other = (BasicRange) obj;
		if (max != other.max)
			return false;
		if (min != other.min)
			return false;
		return true;
	}

	@Override
	public int min() {
		return min;
	}

	@Override
	public int max() {
		return max;
	}

	/**
	 * Returns the amount of numbers in this Range. For example, for Range(7,9) it returns 3, since there are 3 numbers
	 * in
	 * it: 7, 8 and 9.
	 *
	 * @return
	 */
	@Override
	public int length() {
		return max - min + 1;

	}


	/**
	 * Returns a new range that is an intersection of two ranges.
	 *
	 * @param min1
	 * @param max1
	 * @param min2
	 * @param max2
	 * @return
	 */
	public static BasicRange intersectionOf(int min1, int max1, int min2, int max2) {
		if (max2 < min1) {
			throw new IllegalArgumentException("Ranges don't intersect");
		}
		if (min2 > max1) {
			throw new IllegalArgumentException("Ranges don't intersect");
		}
		if (min1 >= min2 && max1 <= max2) {
			return new BasicRange(min1, max1);
		}
		if (min2 >= min1 && max2 <= max1) {
			return new BasicRange(min2, max2);
		}
		if (min1 >= max2) {
			return new BasicRange(min1, max2);
		} else {
			return new BasicRange(min2, max1);
		}
	}

	/**
	 * Checks if rangeMin <= value <= rangeMax.
	 *
	 * @param rangeMin
	 * @param rangeMax
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 * 	if rangeMin > rangeMax
	 */
	public static boolean contains(int rangeMin, int rangeMax, int value) {
		if (rangeMin > rangeMax) {
			throw new IllegalArgumentException(
				"rangeMin (" + rangeMin + ") can't be > rangeMax (" + rangeMax + ")"
			);
		}
		return value >= rangeMin && value <= rangeMax;
	}

	/**
	 * Checks if rangeMin <= value <= rangeMax.
	 *
	 * @param rangeMin
	 * @param rangeMax
	 * @param value
	 * @return
	 * @throws IllegalArgumentException
	 * 	if rangeMin > rangeMax
	 */
	public static boolean contains(double rangeMin, double rangeMax, double value) {
		if (rangeMin > rangeMax) {
			throw new IllegalArgumentException("rangeMin (" + rangeMin + ") can't be > rangeMax (" + rangeMax + ")");
		}
		return value >= rangeMin && value <= rangeMax;
	}

}