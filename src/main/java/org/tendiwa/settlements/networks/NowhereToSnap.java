package org.tendiwa.settlements.networks;

import org.tendiwa.geometry.Point2D;

public class NowhereToSnap implements SnapEvent {
	private final Point2D source;
	private final Point2D target;

	NowhereToSnap(Point2D source, Point2D target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public SnapEvent integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter) {
		fullNetwork.graph().addVertex(target);
		segmentInserter.addSecondaryNetworkEdge(source, target);
		return this;
	}

	@Override
	public Point2D target() {
		return target;
	}
}
