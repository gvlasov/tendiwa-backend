package org.tendiwa.core.meta;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class DoubleRangeCollection {
	private static final double minRange = 0.04;
	private Collection<DoubleRange> ranges;

	public DoubleRangeCollection(DoubleRange... ranges) {
		this.ranges = new LinkedList<>();
		Collections.addAll(this.ranges, ranges);
	}

	public void splitWith(double start, double end) {
		Collection<DoubleRange> newRanges = new LinkedList<>();
		for (DoubleRange range : ranges) {
			for (DoubleRange newRange : range.split(start, end)) {
				if (newRange.length() > minRange) {
					newRanges.add(newRange);
				}
			}
		}
		ranges = newRanges;
	}

	public int size() {
		return ranges.size();
	}

	public Collection<DoubleRange> collection() {
		return ranges;
	}
}
