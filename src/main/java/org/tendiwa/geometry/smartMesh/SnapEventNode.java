package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

public class SnapEventNode implements SnapEvent {
	private final Point2D source;
	private final Point2D target;

	SnapEventNode(Point2D source, Point2D target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public void integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter) {
		assert fullNetwork.graph().containsVertex(target);
		segmentInserter.addSecondaryNetworkEdge(source, target);
	}

	@Override
	public Point2D target() {
		return target;
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

}
