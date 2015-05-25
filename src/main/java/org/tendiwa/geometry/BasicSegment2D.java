package org.tendiwa.geometry;

public class BasicSegment2D implements Segment2D {
	public final Point2D start;
	public final Point2D end;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Segment2D that = (Segment2D) o;

		if (!start.equals(that.start())) return false;
		return end.equals(that.end());

	}

	@Override
	public final int hashCode() {
		int result = start.hashCode();
		result = 31 * result + end.hashCode();
		return result;
	}

	public BasicSegment2D(Point2D start, Point2D end) {
		if (start.x() == end.x() && start.y() == end.y()) {
			throw new IllegalArgumentException(
				"Start and end of a segment must be different points"
			);
		}
		this.start = start;
		this.end = end;
	}

	@Override
	public final String toString() {
		return "BasicSegment2D{" +
			"start=" + start +
			", end=" + end +
			'}';
	}

	@Override
	public final Point2D start() {
		return start;
	}

	@Override
	public final Point2D end() {
		return end;
	}
}
