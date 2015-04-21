package org.tendiwa.geometry;

import com.google.common.collect.Lists;
import org.tendiwa.core.meta.Cell;

import java.util.List;
import java.util.function.Function;

/**
 * An immutable line segment.
 */
public interface Segment2D extends RectangularHull {


	Point2D start();

	Point2D end();

	default Segment2D reverse() {
		return new BasicSegment2D(end(), start());
	}

	/**
	 * Distance by x-axis from {@link #start} to {@link #end}. May be negative.
	 *
	 * @return {@code end.x-start.x}.
	 */
	public default double dx() {
		return end().x() - start().x();
	}

	/**
	 * Distance by y-axis from {@link #start} to {@link #end}. May be negative.
	 *
	 * @return {@code end.y-start.y}.
	 */
	public default double dy() {
		return end().y() - start().y();
	}

	/**
	 * Length of this line segment.
	 *
	 * @return Distance from {@link #start} to {@link #end}.
	 */
	public default double length() {
		return start().distanceTo(end());
	}

	/**
	 * Effectively computes {@code this.length()*this.length()}.
	 * <p>
	 * Squared length is quicker to compute than {@link #length()} (no expensive square root operation). Instead of
	 * comparing lengths of two segments, one might compare their squared lengths.
	 *
	 * @return {@code this.length()*this.length()}
	 */
	public default double squaredLength() {
		return (end().x() - start().x()) * (end().x() - start().x())
			+ (end().y() - start().y()) * (end().y() - start().y());
	}


	public static Function<Segment2D, List<Cell>> toCellList() {
		return e -> Lists.newArrayList(
			CellSegment.vector(e.start().toCell(), e.end().toCell())
		);
	}

	public default boolean isParallel(Segment2D segment) {
		return dx() * segment.dy() - dy() * segment.dx() == 0;
	}

	default Vector2D asVector() {
		return new BasicPoint2D(
			end().x() - start().x(),
			end().y() - start().y()
		);
	}

	default Line2D toLine() {
		return new Line2D(
			start().x(),
			start().y(),
			end().x(),
			end().y()
		);
	}


	/**
	 * Checks if a point is in the left half-plane defined by this segment, or in right half-plane/on the line.
	 * <p>
	 * Left and right are defined in terms of y-down coordinate system.
	 *
	 * @param point
	 * 	A point to find relative position of.
	 * @return true if the point is in the left half-plane, false otherwise (if it is in the right half-plane or right
	 * on the line).
	 */
	// TODO: Extract this method
	public default boolean isLeftOfRay(Point2D point) {
		return ((end().x() - start().x()) * (point.y() - start().y()) - (end().y() - start().y()) * (point.x() - start().x()))
			< 0;
	}

	/**
	 * @param oneEnd
	 * 	A point that {@link Object#equals(Object)} to {@code this.start} or {@link this.end}.
	 * @return {@link #start} if {@code point == this.end}, or {@link #end} if {@code point = this.start}.
	 * @throws java.lang.IllegalArgumentException
	 * 	if {@code point} is neither {@code this.start} nor {@code this.end}.
	 */
	public default Point2D anotherEnd(Point2D oneEnd) {
		if (oneEnd.equals(start())) {
			return end();
		}
		if (oneEnd.equals(end())) {
			return start();
		}
		throw new IllegalArgumentException("Argument must be either start or end point");
	}

	default boolean oneOfEndsIs(Point2D point) {
		return start().equals(point) || end().equals(point);
	}

	public default boolean contains(Point2D point) {
		double minX = Math.min(start().x(), end().x());
		double maxX = Math.max(start().x(), end().x());
		double minY = Math.min(start().y(), end().y());
		double maxY = Math.max(start().y(), end().y());
		return point.x() >= minX && point.x() <= maxX
			&& point.y() >= minY && point.y() <= maxY
			&& point.distanceToLine(this) < Vectors2D.EPSILON;
	}

	default boolean hasEndsNear(Point2D oneEnd, Point2D anotherEnd, double snapChebyshovRadius) {
		return start().chebyshovDistanceTo(oneEnd) < snapChebyshovRadius
			&& end().chebyshovDistanceTo(anotherEnd) < snapChebyshovRadius
			|| end().chebyshovDistanceTo(oneEnd) < snapChebyshovRadius
			&& start().chebyshovDistanceTo(anotherEnd) < snapChebyshovRadius;
	}

	default Point2D middle() {
		return new BasicPoint2D(
			start().x() / 2 + end().x() / 2,
			start().y() / 2 + end().y() / 2
		);
	}

	default StraightLineIntersection intersectionWith(Segment2D anotherSegment) {
		return new BasicSegment2DIntersection(this, anotherSegment);
	}

	public default double minX() {
		return Math.min(start().x(), end().x());
	}

	default double maxX() {
		return Math.max(start().x(), end().x());
	}

	default double minY() {
		return Math.min(start().y(), end().y());
	}

	default double maxY() {
		return Math.max(start().y(), end().y());
	}

	default boolean intersects(Segment2D segment) {
		return new BasicSegment2DIntersection(this, segment).intersect();
	}

	default boolean intersects(Rectangle rectangle) {
		return new RectangleSegmentIntersection(rectangle, this).intersect();
	}
}
