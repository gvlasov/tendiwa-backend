package org.tendiwa.geometry;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Function;

/**
 * An immutable line
 */
public class Segment2D {
	public final Point2D start;
	public final Point2D end;


	public Segment2D(Point2D start, Point2D end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Creates a reverse segment.
	 *
	 * @return A new segment starting at {@code #end} and ending at {@code #start}.
	 */
	public Segment2D reverse() {
		return new Segment2D(end, start);
	}

	/**
	 * A convenience factory method to create Segment2D from 4 numbers.
	 *
	 * @param x1
	 * 	X coordinate of start point.
	 * @param y1
	 * 	Y coordinate of start point.
	 * @param x2
	 * 	X coordinate of end point.
	 * @param y2
	 * 	Y coordinate of end point.
	 * @return A new Segment2D
	 */
	public static Segment2D create(double x1, double y1, double x2, double y2) {
		return new Segment2D(new Point2D(x1, y1), new Point2D(x2, y2));
	}

	/**
	 * Distance by x-axis from {@link #start} to {@link #end}. May be negative.
	 *
	 * @return {@code end.x-start.x}.
	 */
	public double dx() {
		return end.x - start.x;
	}

	/**
	 * Distance by y-axis from {@link #start} to {@link #end}. May be negative.
	 *
	 * @return {@code end.y-start.y}.
	 */
	public double dy() {
		return end.y - start.y;
	}

	@Override
	public String toString() {
		return
			start +
				"," + end;
	}

	/**
	 * Length of this line segment.
	 *
	 * @return Distance from {@link #start} to {@link #end}.
	 */
	public double length() {
		return start.distanceTo(end);
	}

	/**
	 * Effectively computes {@code this.length()*this.length()}.
	 * <p>
	 * Squared length is quicker to compute than {@link #length()} (no expensive square root operation). Instead of
	 * comparing lengths of two segments, one might compare their squared lengths.
	 *
	 * @return {@code this.length()*this.length()}
	 */
	public double squaredLength() {
		return (end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Segment2D segment2D = (Segment2D) o;

		if (end != null ? !end.equals(segment2D.end) : segment2D.end != null) return false;
		if (start != null ? !start.equals(segment2D.start) : segment2D.start != null) return false;

		return true;
	}

	/**
	 * Finds a point of intersection between this line and another line.
	 * <p>
	 * An intersection at ends of lines doesn't count for an intersection.
	 *
	 * @param line
	 * 	Another line.
	 * @return A Point2D where these two lines intersect, or null if lines don't intersect.
	 * @see #intersects(Segment2D)
	 */
	public Point2D intersection(Segment2D line) {
		RayIntersection lineIntersection = new RayIntersection(start, end, line);
		if (!lineIntersection.segmentsIntersect()) {
			return null;
		}
		return lineIntersection.getLinesIntersectionPoint();
	}

	/**
	 * Checks if this segment intersects another segment. This is less expensive than finding the intersection point
	 * with
	 * {@link #intersection(Segment2D)}.
	 * <p>
	 * An intersection at ends of lines doesn't count for an intersection.
	 *
	 * @param segment
	 * 	Another segment.
	 * @return true if lines intersect, false otherwise.
	 * @see #intersection(Segment2D)
	 */
	public boolean intersects(Segment2D segment) {
		RayIntersection intersection = new RayIntersection(start, end, segment);
		return intersection.segmentsIntersect();
	}

	@Override
	public int hashCode() {
		int result = start != null ? start.hashCode() : 0;
		result = 31 * result + (end != null ? end.hashCode() : 0);
		return result;
	}

	public static Function<Segment2D, List<Cell>> toCellList() {
		return e -> Lists.newArrayList(
			CellSegment.vector(e.start.toCell(), e.end.toCell())
		);
	}

	public boolean isParallel(Segment2D segment) {
		return Math.abs((segment.end.y - segment.start.y) / (segment.end.x - segment.start.x))
			== Math.abs((end.y - start.y) / (end.x - start.x));
	}

	public Vector2D asVector() {
		return new Point2D(end.x - start.x, end.y - start.y);
	}

	public Line2D toLine() {
		return new Line2D(start.x, start.y, end.x, end.y);
	}

	/**
	 * Creates a new segment that is parallel to this one.
	 *
	 * @param perpendicularDistance
	 * 	Perpendicular distance from this segment to the new one.
	 * @param fromLeft
	 * 	Whether the new segment should lay in the left half-plane from this segment or the right one
	 * 	(if we look from {@link #start} to {@link #end}).
	 * @return A new line parallel to this segment.
	 */
	public Segment2D createParallelSegment(double perpendicularDistance, boolean fromLeft) {
		double magnitude = Math.sqrt((end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y));
		double transitionX = -(end.y - start.y) / magnitude * (fromLeft ? -perpendicularDistance : perpendicularDistance);
		double transitionY = (end.x - start.x) / magnitude * (fromLeft ? -perpendicularDistance : perpendicularDistance);
		return new Segment2D(
			new Point2D(start.x + transitionX, start.y + transitionY),
			new Point2D(end.x + transitionX, end.y + transitionY)
		);
	}

	/**
	 * @param point
	 * 	A point that {@link Object#equals(Object)} to {@code this.start} or {@link this.end}.
	 * @return {@link #start} if {@code point == this.end}, or {@link #end} if {@code point = this.start}.
	 * @throws java.lang.IllegalArgumentException
	 * 	if {@code point} is neither {@code this.start} nor {@code this.end}.
	 */
	public Point2D anotherPoint(Point2D point) {
		if (point.equals(start)) {
			return end;
		}
		if (point.equals(end)) {
			return start;
		}
		throw new IllegalArgumentException("Argument must be either start or end point");
	}
}
