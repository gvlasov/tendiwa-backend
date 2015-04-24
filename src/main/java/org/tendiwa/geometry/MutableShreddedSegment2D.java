package org.tendiwa.geometry;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a Segment2D split into multiple consecutive pieces. Where one segment ends, another starts. The first
 * segment doesn't share its start with any segment, and the last segment doesn't share its end with any segment.
 */
public final class MutableShreddedSegment2D implements CutSegment2D {
	private final Collection<Segment2D> segments;
	private final Segment2D originalSegment;

	public MutableShreddedSegment2D(Segment2D originalSegment, int expectedNumberOfShreds) {
		this.originalSegment = originalSegment;
		segments = new ArrayList<>(expectedNumberOfShreds);
		segments.add(originalSegment);
	}

	public MutableShreddedSegment2D(Segment2D originalSegment, List<Point2D> splitPoints) {
		this(originalSegment, splitPoints.size() + 1);
		splitPoints.stream()
			.filter(point -> !originalSegment.start().equals(point) && !originalSegment.end().equals(point))
			.forEach(this::splitAt);
	}

	public void splitAt(Point2D point) {
		Segment2D segmentToSplit = getSplitPartWithPoint(point);
		Segment2D onePart = new BasicSegment2D(segmentToSplit.start(), point);
		Segment2D anotherPart = new BasicSegment2D(point, segmentToSplit.end());
		split(segmentToSplit, onePart, anotherPart);
	}


	public void split(Segment2D segment, Segment2D onePart, Segment2D anotherPart) {
		assert segments.contains(segment);
		segments.remove(segment);
		segments.add(onePart);
		segments.add(anotherPart);
	}

	private static boolean isPointInBoundingRectangle(Point2D point, Segment2D segment) {
		if (segment.start().x() != segment.end().x()) {
			double minX = Math.min(segment.start().x(), segment.end().x());
			double maxX = Math.max(segment.start().x(), segment.end().x());
			return point.x() > minX && point.x() < maxX;
		} else {
			double minY = Math.min(segment.start().y(), segment.end().y());
			double maxY = Math.max(segment.start().y(), segment.end().y());
			return point.y() > minY && point.y() < maxY;
		}
	}

	@Override
	public Segment2D getSplitPartWithPoint(Point2D startingPoint) {
		Segment2D answer = segments.stream()
			.filter(s -> isPointInBoundingRectangle(startingPoint, s))
			.findAny()
			.orElseThrow(() -> new GeometryException("Can't find split part"));
		assert !answer.start().equals(startingPoint)
			&& !answer.end().equals(startingPoint);
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
	public Stream<Segment2D> segmentStream() {
		return segments.stream();
	}

	@Override
	public Stream<Point2D> pointStream() {
		return segments.stream()
			.skip(1)
			.map(Segment2D::start);
	}

	@Override
	public boolean hasBeenCut() {
		return segments.size() > 1;
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
