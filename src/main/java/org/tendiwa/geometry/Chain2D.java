package org.tendiwa.geometry;

import java.util.stream.Stream;

public interface Chain2D {
	Stream<Segment2D> asSegmentStream();

	Stream<Point2D> asPointStream();
}
