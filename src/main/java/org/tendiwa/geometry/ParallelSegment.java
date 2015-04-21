package org.tendiwa.geometry;

public final class ParallelSegment implements Segment2D {
	private final Point2D start;
	private final Point2D end;

	/**
	 * Creates a new segment that is parallel to this one.
	 *
	 * @param perpendicularDistance
	 * 	Perpendicular distance from this segment to the new one.
	 * @param fromLeft
	 * 	Whether the new segment should lay in the left half-plane from this segment or the right one
	 * 	(if we look from {@link #start} to {@link #end}).
	 * @return A new line parallel to this segment.
	 */
	public ParallelSegment(Segment2D segment, double perpendicularDistance, boolean fromLeft) {
		double magnitude = Math.sqrt(
			(segment.end().x() - segment.start().x()) * (segment.end().x() - segment.start().x())
				+ (segment.end().y() - segment.start().y()) * (segment.end().y() - segment.start().y())
		);
		double transitionX = -(segment.end().y() - segment.start().y()) / magnitude * (fromLeft ? -perpendicularDistance :
			perpendicularDistance);
		double transitionY = (segment.end().x() - segment.start().x()) / magnitude * (fromLeft ? -perpendicularDistance :
			perpendicularDistance);
		start = new BasicPoint2D(segment.start().x() + transitionX, segment.start().y() + transitionY);
		end = new BasicPoint2D(segment.end().x() + transitionX, segment.end().y() + transitionY);
	}

	@Override
	public Point2D end() {
		return end;
	}

	@Override
	public Point2D start() {
		return start;
	}
}
