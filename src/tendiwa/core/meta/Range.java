package tendiwa.core.meta;

/**
 * Represents a set of all integers between some minimum and maximum values
 * inclusive.
 * 
 * @author suseika
 * 
 */
public class Range {
	public final int min;
	public final int max;

	/**
	 * Creates a new range defined by minimum and maximum value
	 * 
	 * @param min
	 * @param max
	 * @throws IllegalArgumentException
	 *             if min > max
	 */
	public Range(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("min must be <= max");
		}
		this.min = min;
		this.max = max;
	}
	public String toString() {
		return "[" + min + "," + max + "]";
	}
	/**
	 * Returns a new Range that is an intersection of this Range and another
	 * one.
	 * 
	 * @param b
	 *            Range to be intersected with.
	 * @return null, if Ranges don't intersect.
	 * @see Range#lengthOfIntersection(Range, Range)
	 */
	public Range intersection(Range b) {
		if (b.max < min) {
			return null;
		}
		if (b.min > max) {
			return null;
		}
		if (min >= b.min && max <= b.max) {
			return new Range(min, max);
		}
		if (b.min >= min && b.max <= max) {
			return new Range(b.min, b.max);
		}
		if (min >= b.max) {
			return new Range(min, b.max);
		} else {
			return new Range(b.min, max);
		}
	}
	/**
	 * Returns how much integers occur in both Ranges. Unlike
	 * {@link Range#intersection(Range)} it returns an integer, so if ranges
	 * don't intersect it will return 0 instead of null.
	 * 
	 * @param a
	 * @param b
	 * @return 0, if ranges don't intersect.
	 */
	public static int lengthOfIntersection(Range a, Range b) {
		if (b.max < a.min) {
			return 0;
		}
		if (b.min > a.max) {
			return 0;
		}
		if (a.min >= b.min && a.max <= b.max) {
			return a.max - a.min + 1;
		}
		if (b.min >= a.min && b.max <= a.max) {
			return b.max - b.min + 1;
		}
		if (a.min >= b.max) {
			return b.max - a.min + 1;
		} else {
			return a.max - b.min + 1;
		}
	}
	/**
	 * Checks if two ranges overlap.
	 * 
	 * @param min1
	 *            Start of first range.
	 * @param max1
	 *            End of first range.
	 * @param min2
	 *            Start of another range.
	 * @param max2
	 *            End of another range.
	 * @return
	 */
	public static boolean overlap(int min1, int max1, int min2, int max2) {
		if (max2 < min1 || min2 > max1) {
			return false;
		}
		return true;
	}
	/**
	 * Checks if this range contains common numbers with anoter Range.
	 * 
	 * @param range another range.
	 * @return true if it does, false otherwise.
	 */
	public boolean overlaps(Range range) {
		if (range.max < min || range.min > max) {
			return false;
		}
		return true;
	}
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
		Range other = (Range) obj;
		if (max != other.max)
			return false;
		if (min != other.min)
			return false;
		return true;
	}
	/**
	 * Returns the amount of numbers in this Range. For example, for Range(7,9)
	 * it returns 3, since there are 3 numbers in it: 7, 8 and 9.
	 * 
	 * @return
	 */
	public int getLength() {
		return max - min + 1;

	}
	/**
	 * Checks if all values from {@code range} and inside this Range.
	 * 
	 * @param range
	 * @return
	 */
	public boolean contains(Range range) {
		return range.min >= min && range.max <= max;
	}

}