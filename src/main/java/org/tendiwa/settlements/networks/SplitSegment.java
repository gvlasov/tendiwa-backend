package org.tendiwa.settlements.networks;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Collection;
import java.util.HashSet;

public class SplitSegment {
	private final Collection<Segment2D> segments;

	public SplitSegment(int expectedSize) {
		segments = new HashSet<>(expectedSize);
	}

	public void split(Segment2D segment, Segment2D onePart, Segment2D anotherPart) {
		segments.remove(segment);
		segments.add(onePart);
		segments.add(anotherPart);
	}

	private static boolean isPointInBoundingRectangle(Point2D point, Segment2D segment) {
		if (segment.start.x != segment.end.x) {
			double minX = Math.min(segment.start.x, segment.end.x);
			double maxX = Math.max(segment.start.x, segment.end.x);
			return point.x > minX && point.x < maxX;
		} else {
			double minY = Math.min(segment.start.y, segment.end.y);
			double maxY = Math.max(segment.start.y, segment.end.y);
			return point.y > minY && point.y < maxY;
		}
	}

	public Segment2D getSplitPartWithPoint(Point2D startingPoint) {
		Segment2D answer = segments.stream()
			.filter(s -> isPointInBoundingRectangle(startingPoint, s))
			.findAny()
			.get();
		assert !answer.start.equals(startingPoint)
			&& !answer.end.equals(startingPoint);
		return answer;
	}
}
