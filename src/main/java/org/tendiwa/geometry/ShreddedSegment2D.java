package org.tendiwa.geometry;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a Segment2D split into multiple consecutive pieces. Where one segment ends, another starts. The first
 * segment doesn't share its start with any segment, and the last segment doesn't share its end with any segment.
 */
public final class ShreddedSegment2D implements CutSegment2D {
	private final Collection<Segment2D> segments;
	private final Segment2D originalSegment;

	public ShreddedSegment2D(Segment2D originalSegment, int expectedNumberOfShreds) {
		this.originalSegment = originalSegment;
		segments = new HashSet<>(expectedNumberOfShreds);
		segments.add(originalSegment);
	}

	public ShreddedSegment2D(Segment2D originalSegment, List<Point2D> splitPoints) {
		this(originalSegment, splitPoints.size() - 1);
		splitPoints.forEach(this::splitAt);
	}

	private void splitAt(Point2D point) {
		Segment2D segmentToSplit = getSplitPartWithPoint(point);
		Segment2D oneParh = new Segment2D(segmentToSplit.start, point);
		Segment2D anotherPart = new Segment2D(point, segmentToSplit.end);
		split(segmentToSplit, oneParh, anotherPart);
	}


	public void split(Segment2D segment, Segment2D onePart, Segment2D anotherPart) {
		assert segments.contains(segment);
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

	@Override
	public Iterator<Segment2D> iterator() {
		Iterator<Segment2D> segmentsIterator = segments.iterator();
		return new Iterator<Segment2D>() {
			@Override
			public boolean hasNext() {
				return segmentsIterator.hasNext();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Segment2D next() {
				return segmentsIterator.next();
			}

		};
	}

	@Override
	public Stream<Segment2D> stream() {
		return segments.stream();
	}

	@Override
	public void forEach(Consumer<? super Segment2D> action) {
		segments.forEach(action);
	}

	@Override
	public Segment2D originalSegment() {
		return originalSegment;
	}
}
