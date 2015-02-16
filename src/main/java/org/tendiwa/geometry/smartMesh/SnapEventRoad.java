package org.tendiwa.geometry.smartMesh;

import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.awt.Color;
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
	public SnapEvent integrateInto(FullNetwork fullNetwork, SegmentInserter segmentInserter) {
		if (segmentInserter.chanceToConnect()) {
			assert fullNetwork.graph().containsVertex(road.start);
			assert fullNetwork.graph().containsVertex(road.end);
			segmentInserter.splitEdge(road, target);
			segmentInserter.addSecondaryNetworkEdge(source, target);
			return this;
		} else {
			TestCanvas.canvas.draw(new Segment2D(source, target), DrawingSegment2D
				.withColorThin(Color.green));
			return SnapEvent.CHANCE_FAILED;
		}
	}

	@Override
	public Point2D target() {
		return target;
	}

	@Override
	public Optional<Point2D> nextNewNodePoint() {
		return Optional.empty();
	}
}
