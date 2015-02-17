package org.tendiwa.geometry;

import java.util.stream.Stream;

public interface CutSegment2D extends Iterable<Segment2D> {
	Segment2D originalSegment();

	Stream<Segment2D> stream();

	boolean hasBeenCut();
}
