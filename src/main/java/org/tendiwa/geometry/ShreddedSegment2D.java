package org.tendiwa.geometry;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public final class ShreddedSegment2D implements CutSegment2D {
	private final MutableShreddedSegment2D mutableBackend;

	public ShreddedSegment2D(Segment2D originalSegment, List<Point2D> splitPoints) {
		this.mutableBackend = new MutableShreddedSegment2D(originalSegment, splitPoints);
	}

	@Override
	public Segment2D originalSegment() {
		return mutableBackend.originalSegment();
	}

	@Override
	public Segment2D getSplitPartWithPoint(Point2D point) {
		return mutableBackend.getSplitPartWithPoint(point);
	}

	@Override
	public Stream<Segment2D> segmentStream() {
		return mutableBackend.segmentStream();
	}

	@Override
	public Stream<Point2D> pointStream() {
		return mutableBackend.pointStream();
	}

	@Override
	public boolean hasBeenCut() {
		return mutableBackend.hasBeenCut();
	}

	@Override
	public Iterator<Segment2D> iterator() {
		return mutableBackend.iterator();
	}
}
