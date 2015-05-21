package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.tendiwa.core.OrdinalDirection;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;
import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public interface Rectangle2D extends RectangularHull, Polygon {
	double x();

	double y();

	double width();

	double height();

	@Override
	default boolean isClockwise(Segment2D segment) {
		return true;
	}

	@Override
	default Point2D get(int index) {
		if (index == 0) {
			return nwCorner();
		} else if (index == 1) {
			return neCorner();
		} else if (index == 2) {
			return seCorner();
		} else if (index == 3) {
			return swCorner();
		} else {
			throw new IndexOutOfBoundsException(
				"Rectangle has only 4 points with indices 0 to 3, you attempted to access index " + index
			);
		}
	}

	@Override
	default Iterator<Point2D> iterator() {
		return Iterators.forArray(
			nwCorner(),
			neCorner(),
			seCorner(),
			swCorner()
		);
	}

	@Override
	default int indexOf(Object o) {
		if (nwCorner().equals(o)) {
			return 0;
		} else if (neCorner().equals(o)) {
			return 1;
		} else if (seCorner().equals(o)) {
			return 2;
		} else if (swCorner().equals(o)) {
			return 3;
		} else {
			return -1;
		}
	}

	@Override
	default int lastIndexOf(Object o) {
		return indexOf(o);
	}

	default double getMaxX() {
		return x() + width();
	}

	default double getMaxY() {
		return y() + height();
	}

	/**
	 * Checks if a rectangle intersects segment.
	 *
	 * @param segment
	 * 	A segment.
	 * @return true if some part of {@code segment} lies inside {@code rectangle}, false otherwise.
	 * @see <a href="http://stackoverflow.com/a/293052/1542343">How to test if a line segment intersects an
	 * axis-aligned rectange in 2D</a>
	 */
	default boolean intersectsSegment(Segment2D segment) {
		double pointPosition = pointRelativeToLine(x(), y(), segment);
		do {
			if (Math.abs(pointPosition) < Vectors2D.EPSILON) {
				break;
			}
			double newPointPosition = pointRelativeToLine(getMaxX(), y(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(x(), getMaxY(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(getMaxX(), getMaxY(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			return false;
		} while (false);
		double segmentBoundsMin;
		double segmentBoundsMax;
		if (segment.start().x() < segment.end().x()) {
			segmentBoundsMin = segment.start().x();
			segmentBoundsMax = segment.end().x();
		} else {
			segmentBoundsMin = segment.end().x();
			segmentBoundsMax = segment.start().x();
		}
		if (segmentBoundsMax < x() || segmentBoundsMin > getMaxX()) {
			return false;
		}
		if (segment.start().y() < segment.end().y()) {
			segmentBoundsMin = segment.start().y();
			segmentBoundsMax = segment.end().y();
		} else {
			segmentBoundsMin = segment.end().y();
			segmentBoundsMax = segment.start().y();
		}
		if (segmentBoundsMax < y() || segmentBoundsMin > getMaxY()) {
			return false;
		}
		return true;
	}

	/**
	 * @param x
	 * 	X-coordinate of a point.
	 * @param y
	 * 	X-coordinat of a point.
	 * @param segment
	 * 	A segment.
	 * @return > 0 if point is below line, < 0 if point is above line, 0 if point is on line.
	 */
	default double pointRelativeToLine(double x, double y, Segment2D segment) {
		return (segment.end().y() - segment.start().y()) * x
			+ (segment.start().x() - segment.end().x()) * y
			+ (segment.end().x() * segment.start().y() - segment.start().x() * segment.end().y());
	}

	@Override
	default boolean containsPoint(Point2D point) {
		return point.x() >= x() && point.x() <= getMaxX()
			&& point.y() >= y() && point.y() <= getMaxY();
	}

	default boolean strictlyContainsPoint(Point2D point) {
		return point.x() > x() && point.x() < getMaxX()
			&& point.y() > y() && point.y() < getMaxY();
	}

	@Override
	default double minX() {
		return x();
	}

	@Override
	default double maxX() {
		return x() + width();
	}

	@Override
	default double minY() {
		return y();
	}

	@Override
	default double maxY() {
		return y() + height();
	}

	default Rectangle2D stretch(double amount) {
		return new BasicRectangle2D(
			x() - amount,
			y() - amount,
			width() + amount * 2,
			height() + amount * 2
		);
	}

	default Point2D corner(OrdinalDirection direction) {
		Objects.requireNonNull(direction);
		switch (direction) {
			case NW:
				return new BasicPoint2D(x(), y());
			case NE:
				return new BasicPoint2D(x() + width(), y());
			case SE:
				return new BasicPoint2D(x() + width(), y() + height());
			case SW:
			default:
				return new BasicPoint2D(x(), y() + height());
		}
	}

	@Override
	default boolean isClockwise() {
		return true;
	}

	@Override
	default List<Segment2D> toSegments() {
		return ImmutableList.of(
			segment2D(
				corner(OrdinalDirection.NW),
				corner(OrdinalDirection.NE)
			),
			segment2D(
				corner(OrdinalDirection.NE),
				corner(OrdinalDirection.SE)
			),
			segment2D(
				corner(OrdinalDirection.SE),
				corner(OrdinalDirection.SW)
			),
			segment2D(
				corner(OrdinalDirection.SW),
				corner(OrdinalDirection.NW)
			)
		);
	}

	default Point2D nwCorner() {
		return point2D(x(), y());
	}

	default Point2D neCorner() {
		return point2D(maxX(), y());
	}

	default Point2D seCorner() {
		return point2D(maxX(), maxY());
	}

	default Point2D swCorner() {
		return point2D(x(), maxY());
	}

	@Override
	default int size() {
		return 4;
	}

	@Override
	default boolean contains(Object o) {
		if (!(o instanceof Point2D)) {
			return false;
		}
		Point2D point = (Point2D) o;
		return isNWCorner(point)
			|| isNECorner(point)
			|| isSECorner(point)
			|| isSWCorner(point);
	}

	default boolean isNWCorner(Point2D point) {
		return point.x() == x() && point.y() == y();
	}

	default boolean isNECorner(Point2D point) {
		return point.x() == maxX() && point.y() == y();
	}

	default boolean isSECorner(Point2D point) {
		return point.x() == maxX() && point.y() == maxY();
	}

	default boolean isSWCorner(Point2D point) {
		return point.x() == x() && point.y() == maxY();
	}

	@Override
	default Rectangle2D translate(Vector2D vector) {
		return new BasicRectangle2D(
			x() + vector.x(),
			y() + vector.y(),
			width(),
			height()
		);
	}
}
