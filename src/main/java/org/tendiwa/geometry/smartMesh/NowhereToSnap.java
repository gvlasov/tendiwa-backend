package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

import java.util.Optional;

public class NowhereToSnap implements SnapEvent {
	private final Point2D source;
	private final Point2D target;

	NowhereToSnap(Point2D source, Point2D target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public void integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter) {
		fullNetwork.graph().addVertex(target);
		segmentInserter.addSecondaryNetworkEdge(source, target);
	}

	@Override
	public Point2D target() {
		return target;
	}

	@Override
	public boolean isTerminal() {
		return false;
	}

}
