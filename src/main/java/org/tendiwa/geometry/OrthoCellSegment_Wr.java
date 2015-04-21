package org.tendiwa.geometry;

import org.tendiwa.core.Orientation;

public abstract class OrthoCellSegment_Wr implements OrthoCellSegment {
	private final OrthoCellSegment segment;

	OrthoCellSegment_Wr(OrthoCellSegment segment) {
		this.segment = segment;
	}
	@Override
	public int getX() {
		return segment.getX();
	}

	@Override
	public int getY() {
		return segment.getY();
	}

	@Override
	public int length() {
		return segment.length();
	}

	@Override
	public Orientation orientation() {
		return segment.orientation();
	}
}
