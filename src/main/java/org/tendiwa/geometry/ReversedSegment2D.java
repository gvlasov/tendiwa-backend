package org.tendiwa.geometry;

/**
 * A segment starting at {@code #end} and ending at {@code #start} of another segment.
 */
public final class ReversedSegment2D implements Segment2D {

	private final Point2D start;
	private final Point2D end;

	public ReversedSegment2D(Segment2D segment) {
		this.start = segment.end();
		this.end = segment.start();
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
