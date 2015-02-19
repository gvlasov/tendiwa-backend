package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

final class SnapToNeighbor implements SnapEvent {
	SnapToNeighbor() {

	}

	@Override
	public Point2D target() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public void integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean createsNewSegment() {
		return false;
	}
}
