package org.tendiwa.geometry;

import java.util.stream.Stream;

public interface CutSegment2D extends Iterable<Segment2D> {
	Segment2D originalSegment();

	Segment2D getSplitPartWithPoint(Point2D startingPoint);

	Stream<Segment2D> segmentStream();

	Stream<Point2D> pointStream();

	boolean hasBeenCut();
}
