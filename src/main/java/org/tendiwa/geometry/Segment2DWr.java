package org.tendiwa.geometry;

public abstract class Segment2DWr implements Segment2D {
	private final Segment2D segment;

	Segment2DWr(Segment2D segment) {
		this.segment = segment;
	}
}
