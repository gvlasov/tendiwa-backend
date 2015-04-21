package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Optional;

public class SnapToNode implements PropagationEvent {
	private final Segment2D segment;

	SnapToNode(Point2D source, Point2D target) {
		this.segment = new Segment2D(source, target);
	}


	@Override
	public boolean createsNewSegment() {
		return true;
	}

	@Override
	public Point2D target() {
		return segment.end;
	}

	@Override
	public Point2D source() {
		return segment.start;
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public Segment2D addedSegment() {
		return segment;
	}

	@Override
	public Optional<Segment2D> splitSegmentMaybe() {
		return Optional.empty();
	}
}
