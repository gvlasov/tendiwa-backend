package org.tendiwa.core.meta;

public class DoubleRange {
	private double start;
	private double end;

	public DoubleRange(double start, double end) {
		if (end <= start) {
			throw new IllegalArgumentException("End (" + end + ") must be greater than start (" + start + ")");
		}
		this.start = start;
		this.end = end;
	}

	public DoubleRange[] split(double start, double end) {
		if (end <= start) {
			throw new IllegalArgumentException("End (" + end + ") must be greater than start (" + start + ")");
		}
		if (this.end <= start) {
			return new DoubleRange[]{this};
		}
		if (this.start >= end) {
			return new DoubleRange[]{this};
		}
		if (start <= this.start && end >= this.end) {
			return new DoubleRange[0];
		}
		if (start <= this.start && end < this.end) {
			return new DoubleRange[]{new DoubleRange(end, this.end)};
		}
		if (start > this.start && end >= this.end) {
			return new DoubleRange[]{new DoubleRange(this.start, start)};
		}
		assert start > this.start && end < this.end : start + " " + end + " " + this.start + " " + this.end;
		return new DoubleRange[]{
			new DoubleRange(this.start, start),
			new DoubleRange(end, this.end)
		};
	}

	public double length() {
		return end - start;
	}

	@Override
	public String toString() {
		return "DR: " + length();
	}
}
