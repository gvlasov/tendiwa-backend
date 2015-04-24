package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.BasicSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.Optional;

final class SnapToSegment implements PropagationEvent {
	private final Segment2D addedSegment;
	private final Segment2D splitSegment;

	public SnapToSegment(Point2D source, Point2D target, Segment2D splitSegment) {
		this.addedSegment = new BasicSegment2D(source, target);
		this.splitSegment = splitSegment;
		assert target.distanceToLine(splitSegment) < Vectors2D.EPSILON;
	}

	@Override
	public boolean createsNewSegment() {
		return true;
	}

	@Override
	public Point2D target() {
		return addedSegment.end();
	}

	@Override
	public Point2D source() {
		return addedSegment.start();
	}

	@Override
	public boolean isTerminal() {
		return true;
	}

	@Override
	public Segment2D addedSegment() {
		return addedSegment;
	}

	@Override
	public Optional<Segment2D> splitSegmentMaybe() {
		return Optional.of(splitSegment);
	}
}
