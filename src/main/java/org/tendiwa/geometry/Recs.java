package org.tendiwa.geometry;

import org.tendiwa.core.OrdinalDirection;
import org.tendiwa.core.meta.Coordinate;

import java.util.Objects;
import java.util.Optional;

public final class Recs {
	private Recs() {
		throw new UnsupportedOperationException("This class should not be instantiated");
	}

	/**
	 * Takes a cell, and creates a Rectangle with one corner in this cell, and another corner in some other cell.
	 * <p>
	 * The advantage of this method over a plain {@link org.tendiwa.geometry.Rectangle} constructor is that this
	 * method allows more intuitive descriptions of a rectangle (the constructor creates rectangles by their NW
	 * corner, whereas with this method you can create a rectangle counting from any corner).
	 *
	 * @param x
	 * 	X-coordinate of the first corner.
	 * @param y
	 * 	Y-coordinate of the first corner.
	 * @param anotherCornerDirection
	 * 	Location of the second point relatively from the initial point.
	 * @param width
	 * 	How far is the second point from the initial point on the x-axis.
	 * @param height
	 * 	How far is the second point from the initial point on the y-axis.
	 * @return A new rectangle grown from the point in the specified direction.
	 */
	public static Rectangle growFromCell(
		int x,
		int y,
		OrdinalDirection anotherCornerDirection,
		int width,
		int height
	) {
		switch (anotherCornerDirection) {
			case SE:
				return new Rectangle(x, y, width, height);
			case NE:
				return new Rectangle(x, y - height + 1, width, height);
			case NW:
				return new Rectangle(
					x - width + 1,
					y - height + 1,
					width,
					height);
			case SW:
				return new Rectangle(x - width + 1, y, width, height);
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Returns rectangle defined by two corner points
	 */
	public static Rectangle getRectangleFromTwoCorners(Cell c1, Cell c2) {
		int startX = Math.min(c1.x, c2.x);
		int startY = Math.min(c1.y, c2.y);
		int recWidth = Math.max(c1.x, c2.x) - startX + 1;
		int recHeight = Math.max(c1.y, c2.y) - startY + 1;
		return new Rectangle(startX, startY, recWidth, recHeight);
	}

	/**
	 * Returns rectangle defined by two corner points
	 */
	public static Rectangle getRectangleFromTwoCorners(int x1, int y1, int x2, int y2) {
		int startX = Math.min(x1, x2);
		int startY = Math.min(y1, y2);
		int recWidth = Math.max(x1, x2) - startX + 1;
		int recHeight = Math.max(y1, y2) - startY + 1;
		return new Rectangle(startX, startY, recWidth, recHeight);
	}

	/**
	 * <p> Creates a new Rectangle defined by its minimum and maximum coordinates. </p>
	 * <p>
	 * //     * @param xMin
	 * Least coordinate by x-axis.
	 *
	 * @param yMin
	 * 	Least coordinate by y-axis.
	 * @param xMax
	 * 	Greatest coordinate by x-axis.
	 * @param yMax
	 * 	Greatest coordinate by y-axis.
	 * @return A new Rectangle bounded by these two points (containing both of them inside).
	 */
	public static Rectangle rectangleByMinAndMaxCoords(int xMin, int yMin, int xMax, int yMax) {
		if (xMin > xMax) {
			throw new IllegalArgumentException("xMin can't be > xMax");
		}
		if (yMin > yMax) {
			throw new IllegalArgumentException("yMin can't be > yMax");
		}
		return new Rectangle(
			xMin,
			yMin,
			xMax - xMin + 1,
			yMax - yMin + 1);
	}

	/**
	 * Returns the minimum rectangle containing all the given cells inside it.
	 *
	 * @param cells
	 * 	Source of cells.
	 * @return The smallest rectangle possible that contains all of cells within it.
	 */
	public static Rectangle boundsOfCells(Iterable<Cell> cells) {
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (Cell point : cells) {
			if (point.getX() < minX) {
				minX = point.getX();
			}
			if (point.getX() > maxX) {
				maxX = point.getX();
			}
			if (point.getY() < minY) {
				minY = point.getY();
			}
			if (point.getY() > maxY) {
				maxY = point.getY();
			}
		}
		return new Rectangle(
			minX,
			minY,
			maxX - minX + 1,
			maxY - minY + 1);
	}

	/**
	 * Creates a new rectangle whose center is the given cell, with given width and height. If the rectangle created
	 * has
	 * even width/height, the exact center coordinate will be randomized between two possible coordinates.
	 *
	 * @param cell
	 * @param width
	 * @param height
	 * @return
	 */
	public static Rectangle rectangleByCenterPoint(Cell cell, int width, int height) {
		return new Rectangle(
			cell.getX() - width / 2,
			cell.getY() - height / 2,
			width,
			height);
	}

	/**
	 * Grows a new Rectangle from a point where two {@link org.tendiwa.geometry.IntercellularLine}s intersect. An
	 * intersection of two such lines divides the plane in 4 quadrants, and the quadrant where the rectangle will be is
	 * defined by DirectionOldSide argument.
	 *
	 * @param line1
	 * 	Must be perpendicular to line2.
	 * @param line2
	 * 	Must be perpendicular to line1.
	 * @param side
	 * 	Must be ordinal.
	 * @param width
	 * 	Width of the resulting rectangle.
	 * @param height
	 * 	Height of the resulting rectangle.
	 * @return
	 * @see {@link #growFromCell(int, int, org.tendiwa.core.OrdinalDirection, int, int)}
	 */
	public static Rectangle growFromIntersection(IntercellularLine line1, IntercellularLine line2, OrdinalDirection side, int width, int height) {
		if (!line1.isPerpendicular(line2)) {
			throw new IllegalArgumentException(
				"Two lines must be perpendicular");
		}
		IntercellularLinesIntersection intersection = IntercellularLine
			.intersectionOf(line1, line2);
		return growFromIntersection(intersection, side, width, height);

	}

	/**
	 * Grows a new Rectangle from a point where two {@link org.tendiwa.geometry.IntercellularLine}s intersect. An
	 * intersection of two such lines divides the plane in 4 quadrants, and the quadrant where the rectangle will be is
	 * defined by DirectionOldSide argument.
	 *
	 * @param intersection
	 * @param side
	 * 	Must be ordinal.
	 * @param width
	 * 	Width of the resulting rectangle.
	 * @param height
	 * 	Height of the resulting rectangle.
	 * @return
	 */
	public static Rectangle growFromIntersection(IntercellularLinesIntersection intersection, OrdinalDirection side, int width, int height) {
		Cell point = intersection.getCornerPointOfQuarter(side);
		return growFromCell(point.getX(), point.getY(), side, width, height);
	}

	/**
	 * Creates a new {@link Rectangle} relative to an already existing {@link java.awt.Rectangle}.
	 *
	 * @param r
	 * 	an already existing rectangle.
	 * @param dx
	 * 	how far will the new rectangle be shifted by x-axis from the original one.
	 * @param dy
	 * 	how far will the new rectangle be shifted by x-axis from the original one.
	 * @return a new {@link Rectangle} with width and height equal to {@code r}'s.
	 */
	public static Rectangle rectangleMovedFromOriginal(Rectangle r, int dx, int dy) {
		if (r == null) {
			throw new NullPointerException();
		}
		return new Rectangle(
			r.getX() + dx,
			r.getY() + dy,
			r.getWidth(),
			r.getHeight()
		);

	}

	/**
	 * Checks if a rectangle intersects segment.
	 *
	 * @param r
	 * 	A rectangle.
	 * @param segment
	 * 	A segment.
	 * @return true if some part of {@code segment} lies inside {@code rectangle}, false otherwise.
	 * @see <a href="http://stackoverflow.com/a/293052/1542343">How to test if a line segment intersects an
	 * axis-aligned rectange in 2D</a>
	 */
	public static boolean rectangleIntersectsSegment(Rectangle r, Segment2D segment) {
		double pointPosition = pointRelativeToLine(r.x, r.y, segment);
		do {
			if (Math.abs(pointPosition) < Vectors2D.EPSILON) {
				break;
			}
			double newPointPosition = pointRelativeToLine(r.getMaxX(), r.y, segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(r.x, r.getMaxY(), segment);
			if (Math.abs(newPointPosition) < Vectors2D.EPSILON) {
				break;
			}
			if (Math.signum(newPointPosition) != Math.signum(pointPosition)) {
				break;
			}
			newPointPosition = pointRelativeToLine(r.getMaxX(), r.getMaxY(), segment);
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
		if (segment.start.x < segment.end.x) {
			segmentBoundsMin = segment.start.x;
			segmentBoundsMax = segment.end.x;
		} else {
			segmentBoundsMin = segment.end.x;
			segmentBoundsMax = segment.start.x;
		}
		if (segmentBoundsMax < r.x || segmentBoundsMin > r.getMaxX()) {
			return false;
		}
		if (segment.start.y < segment.end.y) {
			segmentBoundsMin = segment.start.y;
			segmentBoundsMax = segment.end.y;
		} else {
			segmentBoundsMin = segment.end.y;
			segmentBoundsMax = segment.start.y;
		}
		if (segmentBoundsMax < r.y || segmentBoundsMin > r.getMaxY()) {
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
	private static double pointRelativeToLine(int x, int y, Segment2D segment) {
		return (segment.end.y - segment.start.y) * x + (segment.start.x - segment.end.x) * y + (segment.end.x * segment.start
			.y - segment.start.x * segment.end.y);
	}

	/**
	 * Checks if all cells of a {@link Rectangle} are contained within a {@link Placeable}.
	 *
	 * @param placeable
	 * @param rectangle
	 * @return true if all cells of a {@code rectangle} are inside {@code placeable}, false otherwise.
	 */
	public static boolean placeableContainsRectangle(Placeable placeable, Rectangle rectangle) {
		Objects.requireNonNull(placeable);
		Objects.requireNonNull(rectangle);
		int recArea = rectangle.area();
		int intersectionArea = 0;

		for (Rectangle areaPiece : placeable.getRectangles()) {
			Optional<Rectangle> intersection = areaPiece.intersectionWith(rectangle);
			if (intersection.isPresent()) {
				intersectionArea += intersection.get().area();
			}
			if (intersectionArea == recArea) {
				return true;
			}
		}
		return false;
	}
}