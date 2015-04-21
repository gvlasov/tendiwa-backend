package org.tendiwa.core.meta;

import java.util.Optional;

public interface Range {

	int min();

	int max();

	default int length() {
		return max() - min() + 1;
	}

	default boolean contains(int value) {
		return value >= min() && value <= max();
	}

	/**
	 * Checks if this range contains common numbers with another Range.
	 *
	 * @param range
	 * 	another range.
	 * @return true if it does, false otherwise.
	 */
	default boolean overlaps(Range range) {
		if (range.max() < min() || range.min() > max()) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a new Range that is an intersection of this Range and another one.
	 *
	 * @param b
	 * 	Range to be intersected with.
	 * @return null, if Ranges don't intersect.
	 */
	default Optional<Range> intersection(Range b) {
		if (b.max() < min() || b.min() > max()) {
			return Optional.empty();
		}
		if (b.contains(this)) {
			return Optional.of(this);
		}
		if (this.contains(b)) {
			return Optional.of(b);
		}
		int greaterMin = Math.max(min(), b.min());
		int lesserMax = Math.min(max(), b.max());
		return Optional.of(new BasicRange(greaterMin, lesserMax));
	}

	/**
	 * Checks if all values from {@code range} and inside this Range.
	 *
	 * @param range
	 * @return
	 */
	default boolean contains(Range range) {
		return range.min() >= min() && range.max() <= max();
	}
}
