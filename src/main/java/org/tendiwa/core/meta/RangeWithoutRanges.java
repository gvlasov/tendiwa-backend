package org.tendiwa.core.meta;

import java.util.Arrays;
import java.util.Collection;

final class SplitRange {
	private final Range baseRange;
	private final Collection<Range> excludedRanges;

	/**
	 * <p> Takes {@code ranges} out of this Range and returns ranges that form the remaining set of integer values.
	 * </p>
	 * <p>
	 * For example, if we split range [0,10] with ranges [2,5] and [7,8], we will get ranges [0,1], [6,6] and [9,10].
	 * </p>
	 *
	 * @param excludedRanges
	 * 	Ranges that split this range, none of which may overlap.
	 * @throws IllegalArgumentException
	 * 	if some of ranges in {@code ranges} overlap.
	 */
	public SplitRange(Range baseRange, Collection<Range> excludedRanges) {
		this.baseRange = baseRange;
		this.excludedRanges = excludedRanges;
	}

	public Range[] ranges() {
		for (Range range : excludedRanges) {
			// Check that no argument ranges overlap
			for (Range anotherRange : excludedRanges) {
				if (range == anotherRange) {
					continue;
				}
				if (range.overlaps(anotherRange)) {
					throw new IllegalArgumentException(
						"None of argument ranges can overlap");
				}
			}
		}
		Range[] rangesArray = excludedRanges.toArray(new Range[excludedRanges.size()]);
		Range[] finalRangesArray;
		Arrays.sort(rangesArray, (range1, range2) -> {
			if (range1.min() > range2.min()) {
				return 1;
			}
			if (range1.min() < range2.min()) {
				return -1;
			}
			assert false;
			return 0;
		});
		// Algorithm takes max coordinate of one range and min coordinate of
		// next range, and those two form a range that will be returned.
		BasicRange preRange = null, postRange = null;
		int additionalSize = 0;
		// If min point of ranges is greater that min point of this Range,
		// create an additional range.
		if (rangesArray[0].min() > baseRange.min()) {
			additionalSize++;
			preRange = new BasicRange(baseRange.min() - 2, baseRange.min() - 1);
		}
		// Same if max point is greater than this.max
		if (rangesArray[rangesArray.length - 1].max() < baseRange.max()) {
			additionalSize++;
			postRange = new BasicRange(baseRange.max() + 1, baseRange.max() + 2);
		}
		if (additionalSize != 0) {
			// If there are 1 or 2 additional ranges.
			int finalSize = rangesArray.length + additionalSize;
			finalRangesArray = new BasicRange[finalSize];
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
			int betweenMin = finalRangesArray[i].max() + 1;
			int betweenMax = finalRangesArray[i + 1].min() - 1;
			if (betweenMin > betweenMax) {
				continue;
			}
			Range rangeBetween = new BasicRange(betweenMin, betweenMax);
			if (rangeBetween.overlaps(baseRange)) {
				answer[indexInAnswer++] = rangeBetween;
			}
		}

		return Arrays.copyOf(answer, indexInAnswer);
	}

}
