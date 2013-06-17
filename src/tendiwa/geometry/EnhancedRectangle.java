package tendiwa.geometry;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

import tendiwa.core.meta.Coordinate;
import tendiwa.core.meta.Direction;
import tendiwa.core.meta.Side;
import tests.SideTest;


/**
 * Adds more geometry methods to Rectangle. Unlike {@link Rectangle}, this class can't be of zero width or height.
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
		Direction dir;
		if (start.x == end.x) {
			dir = Direction.V;
		} else if (start.y == end.y) {
			dir = Direction.H;
		} else {
			throw new Error(start + " and " + end + " are not on the same line");
		}
		if (dir.isV() && start.x >= x && start.x <= x + width - 1) {
			throw new Error("Vertical line inside rectangle");
		} else if (dir.isH() && start.y >= y && start.y <= y + height - 1) {
			throw new Error("Horizontal line inside rectangle");
		}
		if (dir.isV()) {
			return start.x > x ? start.x - x - width + 1 : x - start.x;
		} else {
			return start.y > y ? start.y - y - height + 1 : y - start.y;
		}
	}

	public Coordinate getMiddleOfSide(Side side) {
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
				throw new Error("Incorrect side " + side.side2int());
		}
	}
	/**
	 * A more convenient method for creating rectangles. Takes a point, places
	 * another point from ordinal direction from the initial point.
	 * 
	 * @param x Initial point
	 * @param y Initial point
	 * @param side Location of the second point relatively from the initial point.
	 * @param width How far is the second point from the initial point on x-axis.
	 * @param height How far is the second point from the initial point on y-axis.
	 * @return
	 */
	public static EnhancedRectangle growFromPoint(int x, int y, Side side, int width, int height) {
		if (!side.isOrdinal()) {
			throw new IllegalArgumentException("SideTest must be ordinal (SideTest = "+side);
		}
		if (side == Side.SE) {
			return new EnhancedRectangle(x, y, width, height);
		}
		if (side == Side.NE) {
			return new EnhancedRectangle(x, y-height, width, height);
		}
		if (side == Side.NW) {
			return new EnhancedRectangle(x-width, y-height, width, height);
		}
		return new EnhancedRectangle(x-width, y, width, height);
	}
	/**
	 * Get cell on border.
	 * 
	 * @param side
	 *            Which border;
	 * 
	 * @param sideOf
	 *            SideTest Determines one of the ends of border;
	 * 
	 * @param depth
	 *            How far is the cell from the end of the border. 0 is the first
	 *            cell near end of border. Depth may be even more than width or
	 *            height, so the cell will be outside the rectangle.
	 */
	public Coordinate getCellFromSide(Side side, Side sideOfSide, int depth) {
		switch (side) {
			case N:
				switch (sideOfSide) {
					case E:
						return new Coordinate(x + width - 1 - depth, y);
					case W:
						return new Coordinate(x + depth, y);
					default:
						throw new Error("sideOfSide (" + sideOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			case E:
				switch (sideOfSide) {
					case N:
						return new Coordinate(x + width - 1, y + depth);
					case S:
						return new Coordinate(x + width - 1, y + height - 1 - depth);
					default:
						throw new Error("sideOfSide (" + sideOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			case S:
				switch (sideOfSide) {
					case E:
						return new Coordinate(x + width - 1 - depth, y + height - 1);
					case W:
						return new Coordinate(x + depth, y + height - 1);
					default:
						throw new Error("sideOfSide (" + sideOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			case W:
				switch (sideOfSide) {
					case N:
						return new Coordinate(x, y + depth);
					case S:
						return new Coordinate(x, y + height - 1 - depth);
					default:
						throw new Error("sideOfSide (" + sideOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
				}
			default:
				throw new Error("Incorrect side " + side.side2int());
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
	public EnhancedRectangle stretch(Side side, int amount) {
		switch (side) {
			case N:
				this.setBounds(this.x, this.y - amount, this.width, this.height + amount);
				break;
			case E:
				this.setSize(this.width + amount, this.height);
				break;
			case S:
				this.setSize(this.width, this.height + amount);
				break;
			case W:
				this.setBounds(this.x - amount, this.y, this.width + amount, this.height);
				break;
			default:
				throw new Error("Incorrect side " + side);
		}
		return this;
	}

	public int getDimensionBySide(Side side) {
		/**
		 * Returns this.height if side is N or S, returns this.width if side is
		 * W or E
		 */
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
	 *            {@link SideTest#NW}, {@link SideTest#NE}, {@link SideTest#SW} or
	 *            {@link SideTest#SE}.
	 */
	public Coordinate getCorner(Side side) {
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
				throw new IllegalArgumentException("Only cardinal sides are allowed");
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
}
