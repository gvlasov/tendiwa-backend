package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.Optional;

final class SnapEventRoad implements SnapEvent {
	private final Point2D source;
	private final Point2D target;
	private final Segment2D road;

	public SnapEventRoad(Point2D source, Point2D target, Segment2D road) {
		this.source = source;
		this.target = target;
		this.road = road;
		assert target.distanceToLine(road) < Vectors2D.EPSILON;
	}

	@Override
	public void integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter) {
		assert fullNetwork.graph().containsVertex(road.start);
		assert fullNetwork.graph().containsVertex(road.end);
		segmentInserter.splitEdge(road, target);
		segmentInserter.addSecondaryNetworkEdge(source, target);
	}

	@Override
	public boolean createsNewSegment() {
		return true;
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
