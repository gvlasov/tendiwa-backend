package tendiwa.geometry;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Coordinate;
import tests.SideTest;

/**
 * Adds more geometry methods to Rectangle. Unlike {@link Rectangle}, this class
 * can't be of zero width or height.
 */
public class EnhancedRectangle extends Rectangle {
	private static final long serialVersionUID = -3818700857263511272L;

	public EnhancedRectangle(int x, int y, int width, int height) {
		super(x, y, width, height);
		if (width == 0 || height == 0) {
			throw new IllegalArgumentException("Width or height can't be 0");
		}
	}
	public EnhancedRectangle(Rectangle r) {
		super(r);
		if (r.width == 0 || r.height == 0) {
			throw new IllegalArgumentException("Width or height can't be 0");
		}
	}
	public Collection<Coordinate> getCells() {
		ArrayList<Coordinate> answer = new ArrayList<Coordinate>();
		for (int i = x; i < x + width; i++) {
			for (int j = y; j < y + height; j++) {
				answer.add(new Coordinate(i, j));
			}
		}
		return answer;
	}

	public boolean isCellOnRectangleBorder(int x, int y, Rectangle r) {
		return x == this.x || y == this.y || x == this.x + this.width - 1 || y == this.y + this.height - 1;
	}

	/**
	 * Finds distance from line to rectangle's nearest border parallel to that
	 * line
	 */
	public int distanceToLine(Coordinate start, Coordinate end) {
		Orientation dir;
		if (start.x == end.x) {
			dir = Orientation.VERTICAL;
		} else if (start.y == end.y) {
			dir = Orientation.HORIZONTAL;
		} else {
			throw new Error(start + " and " + end + " are not on the same line");
		}
		if (dir.isVertical() && start.x >= x && start.x <= x + width - 1) {
			throw new Error("Vertical line inside rectangle");
		} else if (dir.isHorizontal() && start.y >= y && start.y <= y + height - 1) {
			throw new Error("Horizontal line inside rectangle");
		}
		if (dir.isVertical()) {
			return start.x > x ? start.x - x - width + 1 : x - start.x;
		} else {
			return start.y > y ? start.y - y - height + 1 : y - start.y;
		}
	}

	public Coordinate getMiddleOfSide(CardinalDirection side) {
		switch (side) {
			case N:
				return new Coordinate(x + width / 2, y);
			case E:
				return new Coordinate(x + width - 1, y + height / 2);
			case S:
				return new Coordinate(x + width / 2, y + height - 1);
			case W:
				return new Coordinate(x, y + height / 2);
			default:
				throw new IllegalArgumentException();
		}
	}
	/**
	 * A more convenient method for creating rectangles. Takes a point, places
	 * another point from ordinal direction from the initial point.
	 * 
	 * @param x
	 *            Initial point
	 * @param y
	 *            Initial point
	 * @param side
	 *            Location of the second point relatively from the initial
	 *            point.
	 * @param width
	 *            How far is the second point from the initial point on x-axis.
	 * @param height
	 *            How far is the second point from the initial point on y-axis.
	 * @return
	 */
	public static EnhancedRectangle growFromPoint(int x, int y, OrdinalDirection side, int width, int height) {
		switch (side) {
			case SE:
				return new EnhancedRectangle(x, y, width, height);
			case NE:
				return new EnhancedRectangle(x, y - height + 1, width, height);
			case NW:
				return new EnhancedRectangle(
					x - width + 1,
					y - height + 1,
					width,
					height);
			case SW:
				return new EnhancedRectangle(x - width + 1, y, width, height);
			default:
				throw new IllegalArgumentException();
		}
	}
	/**
	 * Get cell on border.
	 * 
	 * @param side
	 *            Which border;
	 * 
	 * @param endOfSide
	 *            SideTest Determines one of the ends of border;
	 * 
	 * @param depth
	 *            How far is the cell from the end of the border. 0 is the first
	 *            cell near end of border. Depth may be even more than width or
	 *            height, so the cell will be outside the rectangle.
	 */
	public Coordinate getCellFromSide(CardinalDirection side, CardinalDirection endOfSide, int depth) {
		switch (side) {
			case N:
				switch (endOfSide) {
					case E:
						return new Coordinate(x + width - 1 - depth, y);
					case W:
						return new Coordinate(x + depth, y);
					default:
						throw new Error(
							"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			case E:
				switch (endOfSide) {
					case N:
						return new Coordinate(x + width - 1, y + depth);
					case S:
						return new Coordinate(
							x + width - 1,
							y + height - 1 - depth);
					default:
						throw new Error(
							"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			case S:
				switch (endOfSide) {
					case E:
						return new Coordinate(
							x + width - 1 - depth,
							y + height - 1);
					case W:
						return new Coordinate(x + depth, y + height - 1);
					default:
						throw new Error(
							"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			case W:
				switch (endOfSide) {
					case N:
						return new Coordinate(x, y + depth);
					case S:
						return new Coordinate(x, y + height - 1 - depth);
					default:
						throw new Error(
							"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			default:
				throw new Error("Incorrect side " + side.toInt());
		}
	}
	/**
	 * Stretch rectangle
	 * 
	 * @param side
	 *            SideTest where rectangle stretches
	 * 
	 * @param amount
	 *            Amount of cells to stretch. If depth > 0, then rectangle will
	 *            grow, if depth < 0, then rectangle will shrink. Notice that if
	 *            SideTest == N or W, rectangle.x and rectangle.y will move. If
	 *            depth == 0 then rectangle stays the same.
	 */
	public EnhancedRectangle stretch(CardinalDirection side, int amount) {
		switch (side) {
			case N:
				this.setBounds(
					this.x,
					this.y - amount,
					this.width,
					this.height + amount);
				break;
			case E:
				this.setSize(this.width + amount, this.height);
				break;
			case S:
				this.setSize(this.width, this.height + amount);
				break;
			case W:
				this.setBounds(
					this.x - amount,
					this.y,
					this.width + amount,
					this.height);
				break;
			default:
				throw new IllegalArgumentException();
		}
		return this;
	}

	/**
	 * Returns this.height if side is N or S, returns this.width if side is W or
	 * E
	 */
	public int getDimensionBySide(CardinalDirection side) {
		switch (side) {
			case N:
			case S:
				return this.height;
			case E:
			case W:
				return this.width;
			default:
				throw new Error("SideTest " + side + " is incorrect");
		}
	}
	/**
	 * Returns Coordinate of particular rectangle's corner.
	 * 
	 * @param side
	 *            {@link SideTest#NW}, {@link SideTest#NE}, {@link SideTest#SW}
	 *            or {@link SideTest#SE}.
	 */
	public Coordinate getCorner(OrdinalDirection side) {
		switch (side) {
			case NE:
				return new Coordinate(x + width - 1, y);
			case SE:
				return new Coordinate(x + width - 1, y + height - 1);
			case SW:
				return new Coordinate(x, y + height - 1);
			case NW:
				return new Coordinate(x, y);
			default:
				throw new IllegalArgumentException();
		}
	}
	/**
	 * Returns rectangle defined by two corner points
	 */
	public static EnhancedRectangle getRectangleFromTwoCorners(Coordinate c1, Coordinate c2) {
		int startX = Math.min(c1.x, c2.x);
		int startY = Math.min(c1.y, c2.y);
		int recWidth = Math.max(c1.x, c2.x) - startX + 1;
		int recHeight = Math.max(c1.y, c2.y) - startY + 1;
		return new EnhancedRectangle(startX, startY, recWidth, recHeight);
	}

	/**
	 * <p>
	 * Creates a new EnhancedRectangle defined by its minimum and maximum
	 * coordinates.
	 * </p>
	 * 
	 * @param xMin
	 *            Least coordinate by x-axis.
	 * @param yMin
	 *            Least coordinate by y-axis.
	 * @param xMax
	 *            Greatest coordinate by x-axis.
	 * @param yMax
	 *            Greatest coordinate by y-axis.
	 * @return A new EnhancedRectangle bounded by these two points (containing
	 *         both of them inside).
	 */
	public static EnhancedRectangle rectangleByMinAndMaxCoords(int xMin, int yMin, int xMax, int yMax) {
		if (xMin > xMax) {
			throw new IllegalArgumentException("xMin can't be > xMax");
		}
		if (yMin > yMax) {
			throw new IllegalArgumentException("yMin can't be > yMax");
		}
		return new EnhancedRectangle(
			xMin,
			yMin,
			xMax - xMin + 1,
			yMax - yMin + 1);
	}
	/**
	 * Checks if all cells of this rectangle are inside a particular circle.
	 * 
	 * @param cx
	 *            X-coordinate of the center of a circle.
	 * @param cy
	 *            Y-coordinate of the center of a circle.
	 * @param radius
	 *            Radius of a circle.
	 * @return
	 */
	public boolean isInCircle(int cx, int cy, int radius) {
		Coordinate c = new Coordinate(cx, cy);
		if (c.distance(x, y) > radius) {
			return false;
		}
		if (c.distance(x + width - 1, y) > radius) {
			return false;
		}
		if (c.distance(x, y + height - 1) > radius) {
			return false;
		}
		if (c.distance(x + width - 1, y + height - 1) > radius) {
			return false;
		}
		return true;
	}
	/**
	 * Returns a Coordinate of Rectangle's middle point. If
	 * {@link EnhancedRectangle} has odd width or height, Coordinate will be
	 * rounded up.
	 * 
	 * @return
	 */
	public Coordinate getCenterPoint() {
		return new Coordinate(x + width / 2, y + height / 2);
	}
	/**
	 * Returns the minimum rectangle containing all the given points inside it.
	 * 
	 * @param points
	 * @return
	 */
	public static EnhancedRectangle rectangleContainingAllPonts(Collection<Point> points) {
		int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (Point point : points) {
			if (point.x < minX) {
				minX = point.x;
			}
			if (point.x > maxX) {
				maxX = point.x;
			}
			if (point.y < minY) {
				minY = point.y;
			}
			if (point.y > maxY) {
				maxY = point.y;
			}
		}
		return new EnhancedRectangle(
			minX,
			minY,
			maxX - minX + 1,
			maxY - minY + 1);
	}
	/**
	 * Creates a new rectangle whose center is the given point, with given width
	 * and height. If the rectangle created has even width/height, the exact
	 * center coordinate will be randomized between two possible coordinates.
	 * 
	 * @param point
	 * @param width
	 * @param height
	 * @return
	 */
	public static EnhancedRectangle rectangleByCenterPoint(Point point, int width, int height) {
		return new EnhancedRectangle(
			point.x - width / 2 + (Chance.roll(50) ? -1 : 0),
			point.y - height / 2 + (Chance.roll(50) ? -1 : 0),
			width,
			height);
	}
	/**
	 * Grows a new EnhancedRectangle from a point where two
	 * {@link IntercellularLine}s intersect. An intersection of two such lines
	 * divides the plane in 4 quadrants, and the quadrant where the rectangle
	 * will be is defined by DirectionOldSide argument.
	 * 
	 * @param line1
	 *            Must be perpendicular to line2.
	 * @param line2
	 *            Must be perpendicular to line1.
	 * @param side
	 *            Must be ordinal.
	 * @param width
	 *            Width of the resulting rectangle.
	 * @param height
	 *            Height of the resulting rectangle.
	 * @return
	 * @see {@link EnhancedRectangle#growFromPoint(int, int, DirectionOldSide, int, int)}
	 */
	public static EnhancedRectangle growFromIntersection(IntercellularLine line1, IntercellularLine line2, OrdinalDirection side, int width, int height) {
		if (!line1.isPerpendicular(line2)) {
			throw new IllegalArgumentException(
				"Two lines must be perpendicular");
		}
		IntercellularLinesIntersection intersection = IntercellularLine
			.intersectionOf(line1, line2);
		return growFromIntersection(intersection, side, width, height);

	}
	/**
	 * Grows a new EnhancedRectangle from a point where two
	 * {@link IntercellularLine}s intersect. An intersection of two such lines
	 * divides the plane in 4 quadrants, and the quadrant where the rectangle
	 * will be is defined by DirectionOldSide argument.
	 * 
	 * @param intersection
	 * @param side
	 *            Must be ordinal.
	 * @param width
	 *            Width of the resulting rectangle.
	 * @param height
	 *            Height of the resulting rectangle.
	 * @return
	 */
	public static EnhancedRectangle growFromIntersection(IntercellularLinesIntersection intersection, OrdinalDirection side, int width, int height) {
		Point point = intersection.getCornerPointOfQuarter(side);
		return growFromPoint(point.x, point.y, side, width, height);
	}
	/**
	 * Checks if this rectangle touches an {@link RectangleSidePiece} with one
	 * of its sides.
	 * 
	 * @param segment
	 * @return
	 */
	public boolean touches(RectangleSidePiece piece) {
		if (piece.isVertical()) {
			if (getSideAsSidePiece(CardinalDirection.W).touches(piece) || getSideAsSidePiece(
				CardinalDirection.E).touches(piece)) {
				return true;
			}
			return false;
		} else {
			if (getSideAsSidePiece(CardinalDirection.N).touches(piece) || getSideAsSidePiece(
				CardinalDirection.S).touches(piece)) {
				return true;
			}
			return false;
		}
	}
	/**
	 * Returns an RectangleSidePiece representing one of 4 sides of this
	 * rectangle.
	 * 
	 * @param side
	 * @return
	 */
	@Deprecated
	public RectangleSidePiece getSegmentFromSide(CardinalDirection side) {
		// TODO: Duplicate?
		switch (side) {
			case N:
				return new RectangleSidePiece(CardinalDirection.N, x, y, width);
			case E:
				return new RectangleSidePiece(
					CardinalDirection.E,
					x + width,
					y,
					height);
			case S:
				return new RectangleSidePiece(
					CardinalDirection.S,
					x,
					y + height,
					width);
			case W:
				return new RectangleSidePiece(CardinalDirection.W, x, y, height);
			default:
				throw new IllegalArgumentException();
		}
	}
	/**
	 * Returns a {@link RectangleSidePiece} representing the whole side of a
	 * rectangle.
	 * 
	 * @param side
	 * @return
	 */
	public RectangleSidePiece getSideAsSidePiece(CardinalDirection side) {
		if (side == null) {
			throw new NullPointerException();
		}
		int x, y, length;
		switch (side) {
			case N:
				x = this.x;
				y = this.y;
				length = this.width;
				break;
			case E:
				x = this.x + this.width - 1;
				y = this.y;
				length = this.height;
				break;
			case S:
				x = this.x;
				y = this.y + this.height - 1;
				length = this.width;
				break;
			case W:
			default:
				x = this.x;
				y = this.y;
				length = this.height;
		}
		return new RectangleSidePiece(side, x, y, length);
	}
	/**
	 * Creates a new {@link EnhancedRectangle} relative to an already existing
	 * {@link Rectangle}.
	 * 
	 * @param r
	 *            an already existing rectangle.
	 * @param dx
	 *            how far will the new rectangle be shifted by x-axis from the
	 *            original one.
	 * @param dy
	 *            how far will the new rectangle be shifted by x-axis from the
	 *            original one.
	 * 
	 * @return a new {@link EnhancedRectangle} with width and height equal to
	 *         {@code r}'s.
	 */
	public static EnhancedRectangle rectangleMovedFromOriginal(Rectangle r, int dx, int dy) {
		if (r == null) {
			throw new NullPointerException();
		}
		return new EnhancedRectangle(r.x + dx, r.y + dy, r.width, r.height);

	}
	@Override
	public String toString() {
		return "{" + x + "," + y + "," + width + "," + height + "}";
	}
}
