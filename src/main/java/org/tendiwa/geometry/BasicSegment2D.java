package org.tendiwa.geometry;

public final class BasicSegment2D implements Segment2D {
	public final Point2D start;
	public final Point2D end;

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
	public String toString() {
		return "BasicSegment2D{" +
			"start=" + start +
			", end=" + end +
			'}';
	}

	@Override
	public Point2D start() {
		return start;
	}

	@Override
	public Point2D end() {
		return end;
	}
}
