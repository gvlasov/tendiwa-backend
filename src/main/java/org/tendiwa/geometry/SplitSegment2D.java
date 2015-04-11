package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Represents a Segment2D split into two Segment2Ds.
 */
public final class SplitSegment2D implements CutSegment2D {
	private final Segment2D firstHalf;
	private final Segment2D secondHalf;
	private final Segment2D originalSegment;

	SplitSegment2D(Segment2D originalSegment, Point2D splitPoint) {
		this.originalSegment = originalSegment;
		this.firstHalf = new Segment2D(originalSegment.start, splitPoint);
		this.secondHalf = new Segment2D(splitPoint, originalSegment.end);
	}

	public Segment2D firstHalf() {
		return firstHalf;
	}

	public Segment2D secondHalf() {
		return secondHalf;
	}

	public Point2D middlePoint() {
		return firstHalf.end;
	}

	public Point2D originalStart() {
		return firstHalf.start;
	}

	public Point2D originalEnd() {
		return secondHalf.end;
	}

	/**
	 * Constructs a new Segment2D equal to the original segment this SplitSegment2D was constructed from.
	 *
	 * @return A new Segment2D equal to the original segment this SplitSegment2D was constructed from.
	 */
	public Segment2D recallOriginalSegment() {
		return new Segment2D(originalStart(), originalEnd());
	}

	@Override
	public Segment2D originalSegment() {
		return originalSegment;
	}

	@Override
	public Stream<Segment2D> segmentStream() {
		return Stream.of(firstHalf, secondHalf);
	}

	@Override
	public Stream<Point2D> pointStream() {
		return Stream.of(firstHalf.start, firstHalf.end, secondHalf.end);
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
