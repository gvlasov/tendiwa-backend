package org.tendiwa.core.meta;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * Represents a set of all integers between some minimum and maximum values inclusive.
 *
 * @author suseika
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
	 * 	if min > max
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
	 * Returns a new Range that is an intersection of this Range and another one.
	 *
	 * @param b
	 * 	Range to be intersected with.
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
		int greaterMin = Math.max(min, b.min);
		int lesserMax = Math.min(max, b.max);
		return new Range(greaterMin, lesserMax);
	}

	/**
	 * Returns how much integers occur in both Ranges. Unlike {@link Range#intersection(Range)} it returns an integer,
	 * so if
	 * ranges don't intersect it will return 0 instead of null.
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
	 * Returns how much integers occur in both Ranges. Unlike {@link Range#intersection(Range)} it returns an integer,
	 * so if
	 * ranges don't intersect it will return 0 instead of null.
	 *
	 * @return 0, if ranges don't intersect.
	 */
	public static int lengthOfIntersection(int minA, int maxA, int minB, int maxB) {
		if (minA > maxA) {
			throw new IllegalArgumentException("minA must be less than maxA");
		}
		if (minB > maxB) {
			throw new IllegalArgumentException("minB must be less than maxB");
		}
		if (maxB < minA) {
			return 0;
		}
		if (minB > maxA) {
			return 0;
		}
		if (minA >= minB && maxA <= maxB) {
			return maxA - minA + 1;
		}
		if (minB >= minA && maxB <= maxA) {
			return maxB - minB + 1;
		}
		if (minA >= maxB) {
			return maxB - minA + 1;
		} else {
			return maxA - minB + 1;
		}
	}

	/**
	 * Checks if two ranges overlap.
	 *
	 * @param min1
	 * 	Start of first range.
	 * @param max1
	 * 	End of first range.
	 * @param min2
	 * 	Start of another range.
	 * @param max2
	 * 	End of another range.
	 * @return
	 */
	public static boolean overlap(int min1, int max1, int min2, int max2) {
		if (max2 < min1 || min2 > max1) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if this range contains common numbers with another Range.
	 *
	 * @param range
	 * 	another range.
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
	 * Returns the amount of numbers in this Range. For example, for Range(7,9) it returns 3, since there are 3 numbers
	 * in
	 * it: 7, 8 and 9.
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

	/**
	 * <p> Takes {@code ranges} out of this Range and returns ranges that form the remaining set of integer values.
	 * </p>
	 * <p>
	 * For example, if we split range [0,10] with ranges [2,5] and [7,8], we will get ranges [0,1], [6,6] and [9,10].
	 * </p>
	 *
	 * @param ranges
	 * 	Ranges that split this range, none of which may overlap.
	 * @return
	 * @throws IllegalArgumentException
	 * 	if some of ranges in {@code ranges} overlap.
	 */
	public Range[] splitWithRanges(Collection<Range> ranges) {
		for (Range range : ranges) {
			// Check that no argument ranges overlap
			for (Range anotherRange : ranges) {
				if (range == anotherRange) {
					continue;
				}
				if (range.overlaps(anotherRange)) {
					throw new IllegalArgumentException(
						"None of argument ranges can overlap");
				}
			}
		}
		Range[] rangesArray = ranges.toArray(new Range[ranges.size()]);
		Range[] finalRangesArray;
		Arrays.sort(rangesArray, new Comparator<Range>() {
			@Override
			public int compare(Range range1, Range range2) {
				if (range1.min > range2.min) {
					return 1;
				}
				if (range1.min < range2.min) {
					return -1;
				}
				assert false;
				return 0;
			}
		});
		// Algorithm takes max coordinate of one range and min coordinate of
		// next range, and those two form a range that will be returned.
		Range preRange = null, postRange = null;
		int additionalSize = 0;
		// If min point of ranges is greater that min point of this Range,
		// create an additional range.
		if (rangesArray[0].min > min) {
			additionalSize++;
			preRange = new Range(min - 2, min - 1);
		}
		// Same if max point is greater than this.max
		if (rangesArray[rangesArray.length - 1].max < max) {
			additionalSize++;
			postRange = new Range(max + 1, max + 2);
		}
		if (additionalSize != 0) {
			// If there are 1 or 2 additional ranges.
			int finalSize = rangesArray.length + additionalSize;
			finalRangesArray = new Range[finalSize];
			int startIndex;
			if (preRange != null) {
				// Add additional range before all ranges.
				finalRangesArray[0] = preRange;
				startIndex = 1;
			} else {
				startIndex = 0;
			}
			for (int i = 0, l = rangesArray.length; i < l; i++) {
				finalRangesArray[startIndex + i] = rangesArray[i];
			}
			if (postRange != null) {
				// Add additional range after all ranges.
				finalRangesArray[finalSize - 1] = postRange;
			}
		} else {
			finalRangesArray = rangesArray;
		}
		Range[] answer = new Range[finalRangesArray.length - 1];
		int indexInAnswer = 0;
		int itemsSkipped = 0;
		for (int i = 0, l = finalRangesArray.length - 1; i < l; i++) {
			int betweenMin = finalRangesArray[i].max + 1;
			int betweenMax = finalRangesArray[i + 1].min - 1;
			if (betweenMin > betweenMax) {
				continue;
			}
			Range rangeBetween = new Range(betweenMin, betweenMax);
			if (rangeBetween.overlaps(this)) {
				answer[indexInAnswer++] = rangeBetween;
			}
		}

		return Arrays.copyOf(answer, indexInAnswer);
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
	public static Range intersectionOf(int min1, int max1, int min2, int max2) {
		if (max2 < min1) {
			throw new IllegalArgumentException("Ranges don't intersect");
		}
		if (min2 > max1) {
			throw new IllegalArgumentException("Ranges don't intersect");
		}
		if (min1 >= min2 && max1 <= max2) {
			return new Range(min1, max1);
		}
		if (min2 >= min1 && max2 <= max1) {
			return new Range(min2, max2);
		}
		if (min1 >= max2) {
			return new Range(min1, max2);
		} else {
			return new Range(min2, max1);
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
			throw new IllegalArgumentException("rangeMin (" + rangeMin + ") can't be > rangeMax (" + rangeMax + ")");
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