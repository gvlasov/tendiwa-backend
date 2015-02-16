package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

import java.util.Optional;

public class SnapEventNode implements SnapEvent {
	private final Point2D source;
	private final Point2D target;

	SnapEventNode(Point2D source, Point2D target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public SnapEvent integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter) {
		if (segmentInserter.chanceToConnect()) {
			assert fullNetwork.graph().containsVertex(target);
			segmentInserter.addSecondaryNetworkEdge(source, target);
			return this;
		} else {
			return SnapEvent.CHANCE_FAILED;
		}
	}

	@Override
	public Point2D target() {
		return target;
	}

	@Override
	public Optional<Point2D> nextNewNodePoint() {
		return Optional.of(target);
	}
}
