package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.stream.Stream;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

/**
 * Represents a Segment2D split into two Segment2Ds.
 */
public final class SplitSegment2D implements CutSegment2D {
	private final Segment2D firstHalf;
	private final Segment2D secondHalf;
	private final Segment2D originalSegment;

	public SplitSegment2D(Segment2D originalSegment, Point2D splitPoint) {
		this.originalSegment = originalSegment;
		this.firstHalf = segment2D(originalSegment.start(), splitPoint);
		this.secondHalf = segment2D(splitPoint, originalSegment.end());
	}
	public final Ray leftNormal() {
		Point2D cwPoint = originalEnd();
		Point2D ccwPoint = originalStart();
		Point2D rayStart = middlePoint();
		Point2D pointOnRay = rayStart.add(
			new BasicBisector(
				cwPoint.subtract(rayStart),
				ccwPoint.subtract(rayStart)
			).asInbetweenVector()
		);
		return new Ray(
			rayStart,
			rayStart.angleTo(pointOnRay)
		);
	}

	public Segment2D firstHalf() {
		return firstHalf;
	}

	public Segment2D secondHalf() {
		return secondHalf;
	}

	public Point2D middlePoint() {
		return firstHalf.end();
	}

	public Point2D originalStart() {
		return firstHalf.start();
	}

	public Point2D originalEnd() {
		return secondHalf.end();
	}

	@Override
	public Segment2D originalSegment() {
		return originalSegment;
	}

	@Override
	public Segment2D getSplitPartWithPoint(Point2D startingPoint) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<Segment2D> segmentStream() {
		return Stream.of(firstHalf, secondHalf);
	}

	@Override
	public Stream<Point2D> pointStream() {
		return Stream.of(firstHalf.end());
	}

	@Override
	public boolean hasBeenCut() {
		return true;
	}

	@Override
	public Iterator<Segment2D> iterator() {
		return ImmutableList.of(firstHalf, secondHalf).iterator();
	}
}