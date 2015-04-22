package org.tendiwa.geometry;

public abstract class Segment2D_Wr implements Segment2D {

	private final Point2D end;
	private final Point2D start;

	protected Segment2D_Wr(Segment2D segment) {
		this.start = segment.start();
		this.end = segment.end();
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
