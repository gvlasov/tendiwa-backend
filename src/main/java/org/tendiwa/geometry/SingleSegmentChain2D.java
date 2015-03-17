package org.tendiwa.geometry;

import java.util.stream.Stream;

public final class SingleSegmentChain2D implements Chain2D {

	private final Segment2D segment;

	public SingleSegmentChain2D(Segment2D segment) {
		this.segment = segment;
	}

	@Override
	public Stream<Segment2D> asSegmentStream() {
		return Stream.of(segment);
	}

	@Override
	public Stream<Point2D> asPointStream() {
		return Stream.of(segment.start, segment.end);
	}
}
