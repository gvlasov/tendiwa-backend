package org.tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import org.tendiwa.core.*;
import org.tendiwa.core.meta.Coordinate;
import org.tendiwa.core.meta.Range;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Adds more geometry methods to Rectangle. Unlike {@link java.awt.Rectangle}, this class can't be of zero width or
 * height.
 */
public class Rectangle implements Placeable {
private final int x;
private final int y;
private final int width;
private final int height;

public Rectangle(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	if (width == 0 || height == 0) {
		throw new IllegalArgumentException("Width or height can't be 0");
	}
}

public Rectangle(Rectangle r) {
	this.x = r.x;
	this.y = r.y;
	this.width = r.width;
	this.height = r.height;
}

public Rectangle(java.awt.Rectangle r) {
	this.x = r.x;
	this.y = r.y;
	this.width = r.width;
	this.height = r.height;
	if (r.width == 0 || r.height == 0) {
		throw new IllegalArgumentException("Width or height can't be 0");
	}
}

public boolean intersects(Rectangle r) {
	int tw = this.width;
	int th = this.height;
	int rw = r.width;
	int rh = r.height;
	if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
		return false;
	}
	int tx = this.x;
	int ty = this.y;
	int rx = r.x;
	int ry = r.y;
	rw += rx;
	rh += ry;
	tw += tx;
	th += ty;
	//      overflow || intersect
	return ((rw < rx || rw > tx) &&
		(rh < ry || rh > ty) &&
		(tw < tx || tw > rx) &&
		(th < ty || th > ry));
}

public Segment getSideAsSegment(CardinalDirection side) {
	if (side == CardinalDirection.N || side == CardinalDirection.S) {
		return getSegmentInsideFromSide(side, 0, width);
	}
	return getSegmentInsideFromSide(side, 0, height);
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

public boolean isCellOnRectangleBorder(int x, int y, java.awt.Rectangle r) {
	return x == this.x || y == this.y || x == this.x + this.width - 1 || y == this.y + this.height - 1;
}

/**
 * Finds distance from line to rectangle's nearest border parallel to that line
 */
public int distanceToLine(Cell start, Cell end) {
	Orientation dir;
	if (start.getX() == end.getX()) {
		dir = Orientation.VERTICAL;
	} else if (start.getY() == end.getY()) {
		dir = Orientation.HORIZONTAL;
	} else {
		throw new Error(start + " and " + end + " are not on the same line");
	}
	if (dir.isVertical() && start.getX() >= x && start.getX() <= x + width - 1) {
		throw new Error("Vertical line inside rectangle");
	} else if (dir.isHorizontal() && start.getY() >= y && start.getY() <= y + height - 1) {
		throw new Error("Horizontal line inside rectangle");
	}
	if (dir.isVertical()) {
		return start.getX() > x ? start.getX() - x - width + 1 : x - start.getX();
	} else {
		return start.getY() > y ? start.getY() - y - height + 1 : y - start.getY();
	}
}

public Cell getMiddleOfSide(CardinalDirection side) {
	switch (side) {
		case N:
			return new Cell(x + width / 2, y);
		case E:
			return new Cell(x + width - 1, y + height / 2);
		case S:
			return new Cell(x + width / 2, y + height - 1);
		case W:
			return new Cell(x, y + height / 2);
		default:
			throw new IllegalArgumentException();
	}
}

/**
 * Get cell on border.
 *
 * @param side
 * 	Which border;
 * @param endOfSide
 * 	SideTest Determines one of the ends of border;
 * @param depth
 * 	How far is the cell from the end of the border. 0 is the first cell near end of border. Depth may be even more than
 * 	width or height, so the cell will be outside the rectangle.
 */
public Cell getCellFromSide(CardinalDirection side, CardinalDirection endOfSide, int depth) {
	switch (side) {
		case N:
			switch (endOfSide) {
				case E:
					return new Cell(x + width - 1 - depth, y);
				case W:
					return new Cell(x + depth, y);
				default:
					throw new Error(
						"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
			}
		case E:
			switch (endOfSide) {
				case N:
					return new Cell(x + width - 1, y + depth);
				case S:
					return new Cell(
						x + width - 1,
						y + height - 1 - depth);
				default:
					throw new Error(
						"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
			}
		case S:
			switch (endOfSide) {
				case E:
					return new Cell(
						x + width - 1 - depth,
						y + height - 1);
				case W:
					return new Cell(x + depth, y + height - 1);
				default:
					throw new Error(
						"sideOfSide (" + endOfSide + ") must be clockwise or counter-clockwise from side (" + side + ")");
			}
		case W:
			switch (endOfSide) {
				case N:
					return new Cell(x, y + depth);
				case S:
					return new Cell(x, y + height - 1 - depth);
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
 * 	SideTest where rectangle stretches
 * @param amount
 * 	Amount of cells to stretch. If depth > 0, then rectangle will grow, if depth < 0, then rectangle will shrink. Notice
 * 	that if SideTest == N or W, rectangle.x and rectangle.y will move. If depth == 0 then rectangle stays the same.
 */
public Rectangle stretch(CardinalDirection side, int amount) {
	switch (side) {
		case N:
			return new Rectangle(
				this.x,
				this.y - amount,
				this.width,
				this.height + amount);
		case E:
			return new Rectangle(this.x, this.y, this.width + amount, this.height);
		case S:
			return new Rectangle(this.x, this.y, this.width, this.height + amount);
		case W:
			return new Rectangle(
				this.x - amount,
				this.y,
				this.width + amount,
				this.height);
		default:
			throw new IllegalArgumentException();
	}
}

/**
 * Returns this.height if side is N or S, returns this.width if side is W or E
 */
public int getDimensionBySide(CardinalDirection side) {
	switch (side) {
		case N:
		case S:
			return this.width;
		case E:
		case W:
			return this.height;
		default:
			throw new Error("SideTest " + side + " is incorrect");
	}
}

/**
 * Returns Coordinate of particular rectangle's corner.
 *
 * @param corner
 * 	Defines corner by ordinal side.
 */
public Cell getCorner(OrdinalDirection corner) {
	if (corner == Directions.NW) {
		return new Cell(x, y);
	} else if (corner == Directions.NE) {
		return new Cell(x + width - 1, y);
	} else if (corner == Directions.SE) {
		return new Cell(x + width - 1, y + height - 1);
	} else {
		assert corner == Directions.SW;
		return new Cell(x, y + height - 1);
	}
}

/**
 * Checks if all cells of this rectangle are inside a particular circle.
 *
 * @param cx
 * 	X-coordinate of the center of a circle.
 * @param cy
 * 	Y-coordinate of the center of a circle.
 * @param radius
 * 	Radius of a circle.
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
 * Returns a Coordinate of Rectangle's middle point. If {@link Rectangle} has odd width or height, Coordinate will be
 * rounded up.
 *
 * @return
 */
public Cell getCenterPoint() {
	return new Cell(x + width / 2, y + height / 2);
}

public int getCenterX() {
	return x + width / 2;
}

public int getCenterY() {
	return y + height / 2;
}

/**
 * Checks if this rectangle touches an {@link RectangleSidePiece} with one of its sides.
 *
 * @param piece
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
 * Returns a {@link RectangleSidePiece} representing the whole side of a rectangle.
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

@Override
public String toString() {
	return "{" + x + "," + y + "," + width + "," + height + "}";
}

@Override
public Rectangle place(RectangleSystemBuilder builder, int x, int y) {
	return builder.placeRectangle(x, y, this.width, this.height);
}

@Override
public void prebuild(RectangleSystemBuilder builder) {
}

@Override
public Placeable rotate(Rotation rotation) {
	int newWidth, newHeight;
	switch (rotation) {
		case CLOCKWISE:
		case COUNTER_CLOCKWISE:
			newWidth = height;
			newHeight = width;
			break;
		case HALF_CIRCLE:
			newWidth = width;
			newHeight = height;
		default:
			throw new UnsupportedOperationException("Operation for rotation " + rotation + " is not implemented yet");
	}
	return new Rectangle(x, y, newWidth, newHeight);
}

@Override
public Iterable<Rectangle> getRectangles() {
	return getArrayOfItself();
}

private Iterable<Rectangle> getArrayOfItself() {
	return ImmutableList.of(this);
}

@Override
public StepPlaceNextAt repeat(int count) {
	return new StepPlaceNextAt(count, this);
}

@Override
public Rectangle getBounds() {
	return this;
}

/**
 * Returns coordinate of the farthest point inside a rectangle from given side;
 *
 * @param side
 * 	What side take coordinate from.
 * @return X-coordinate in {@code side} is {@link Directions#W} or {@link Directions#E}, Y-coordinate if {@code side} is
 *         {@link Directions#N} or {@link Directions#S};
 */
public int getStaticCoordOfSide(CardinalDirection side) {
	switch (side) {
		case E:
			return x + width - 1;
		case S:
			return y + height - 1;
		case W:
			return x;
		case N:
			return y;
		default:
			throw new Error();

	}
}

int getMinStaticCoord(Orientation orientation) {
	if (orientation == Orientation.HORIZONTAL) {
		return x;
	} else {
		assert orientation == Orientation.VERTICAL;
		return y;
	}
}

int getMaxStaticCoord(Orientation orientation) {
	if (orientation == Orientation.HORIZONTAL) {
		return x + width - 1;
	} else {
		assert orientation == Orientation.VERTICAL;
		return y + height - 1;
	}
}

public int getMinDynamicCoord(Orientation orientation) {
	if (orientation == Orientation.HORIZONTAL) {
		return y;
	} else {
		assert orientation == Orientation.VERTICAL;
		return x;
	}
}

public int getStaticLength(Orientation orientation) {
	if (orientation == Orientation.HORIZONTAL) {
		return width;
	} else {
		assert orientation == Orientation.VERTICAL;
		return height;
	}
}

public int getMaxDynamicCoord(Orientation orientation) {
	if (orientation == Orientation.HORIZONTAL) {
		return y + height - 1;
	} else {
		assert orientation == Orientation.VERTICAL;
		return x + width - 1;
	}
}

int amountOfCellsBetween(Rectangle r, Orientation orientation) {
	int staticCoord1 = r.getMinStaticCoord(orientation);
	int staticCoord2 = this.getMinStaticCoord(orientation);
	int staticLength1 = r.getStaticLength(orientation);
	int staticLength2 = this.getStaticLength(orientation);
	assert staticCoord1 != staticCoord2 : "Rectangles can't have same static coord";
	assert !Range.overlap(staticCoord1, staticCoord1 + staticLength1 - 1, staticCoord2, staticCoord2 + staticLength2 - 1)
		: "Rectangles can't overlap";
	if (staticCoord1 > staticCoord2) {
		assert staticCoord1 - staticCoord2 - staticLength2 >= 0;
		return staticCoord1 - staticCoord2 - staticLength2;
	} else {
		assert staticCoord2 - staticCoord1 - staticLength1 >= 0;
		return staticCoord2 - staticCoord1 - staticLength1;
	}
}

boolean overlapsByDynamicRange(Rectangle r, Orientation orientation) {
	return Range.overlap(
		this.getMinDynamicCoord(orientation),
		this.getMaxDynamicCoord(orientation),
		r.getMinDynamicCoord(orientation),
		r.getMaxDynamicCoord(orientation)
	);
}

boolean overlapsByStaticRange(Rectangle r, Orientation orientation) {
	return Range.overlap(
		this.getMinStaticCoord(orientation),
		this.getMaxStaticCoord(orientation),
		r.getMinStaticCoord(orientation),
		r.getMaxStaticCoord(orientation)
	);
}

public Segment getIntersectionSegment(Rectangle r) {
	if (this == r) {
		throw new IllegalArgumentException("You can't get intersection segment of a rectanlge with itself");
	}
	assert !this.intersects(r);
	if (overlapsByStaticRange(r, Orientation.HORIZONTAL)) {
		int y = this.y < r.y ? this.y : this.y + this.height - 1;
		Range range = Range.intersectionOf(
			x, x + width - 1,
			r.x, r.x + r.width - 1
		);
		return new Segment(range.min, y, range.getLength(), Orientation.HORIZONTAL);
	}
	if (overlapsByStaticRange(r, Orientation.VERTICAL)) {
		int x = this.x < r.x ? this.x : this.x + this.width - 1;
		Range range = Range.intersectionOf(
			y, y + height - 1,
			r.y, r.y + r.height - 1
		);
		return new Segment(x, range.min, range.getLength(), Orientation.VERTICAL);
	}
	throw new IllegalArgumentException("Rectangles " + this + " and " + r + " don't have adjacency segment");
}

/**
 * Creates a new Segment that lies along a side inside this rectangle.
 *
 * @param side
 * 	Side of rectangle.
 * @param shift
 * 	If side is N or S, it is shift by x-axis. If E or W, then by y-axis.
 * @param length
 * 	Length of the segment.
 * @return A new Segment that lies along a side inside this rectangle.
 */
public Segment getSegmentInsideFromSide(CardinalDirection side, int shift, int length) {
	int startX;
	int startY;
	if (side == Directions.N) {
		startX = x + shift;
		startY = y;
	} else if (side == Directions.E) {
		startX = x + width - 1;
		startY = y + shift;
	} else if (side == Directions.S) {
		startX = x + shift;
		startY = y + height - 1;
	} else {
		assert side == Directions.W;
		startX = x;
		startY = y + shift;
	}
	return new Segment(startX, startY, length, side.getOrientation().reverted());
}

/**
 * <p>Returns true if this rectangle touches another rectangle's side from inside that rectangle, that is:</p><ol>
 * <li>their sides lie on the same line and</li> <li>this rectangle is at least partially inside another rectangle</li>
 * </ol>
 *
 * @param anotherRectangle
 * 	True if this rectangle touches another rectangle from inside, false otherwise.
 */
public boolean touchesFromInside(Rectangle anotherRectangle) {
	if (intersects(anotherRectangle)) {
		if (anotherRectangle.x == x) {
			return true;
		}
		if (anotherRectangle.y == y) {
			return true;
		}
		if (anotherRectangle.x + anotherRectangle.width == x + width) {
			// Unlike in similar methods, we don't need to subtract 1 here
			// because it won't make any difference:
			// 1 would be subtracted from both sides of == operator.
			return true;
		}
		if (anotherRectangle.y + anotherRectangle.height == y + height) {
			return true;
		}
	}
	return false;
}

/**
 * Returns a {@link RectangleSidePiece} of this rectangle that touches {@code rectangle} (with 0 cells between them).
 *
 * @param neighbor
 * 	Neighbor rectangle.
 */
public RectangleSidePiece getCommonSidePiece(Rectangle neighbor) {
	if (!RectangleSystem.areRectanglesInXCells(this, neighbor, 0)) {
		throw new IllegalArgumentException("Only neighbor rectangles (with 0 cells between them) can have a common side piece");
	}
	RectangleSidePiece commonSidePiece = null;
	RectangleSidePiece neighborPiece = null;
	boolean found = false;
	for (CardinalDirection thisSide : CardinalDirection.values()) {
		commonSidePiece = getSideAsSidePiece(thisSide);
		CardinalDirection neighborSide = thisSide.opposite();
		neighborPiece = neighbor.getSideAsSidePiece(neighborSide);
		if (commonSidePiece.distanceTo(neighborPiece) == 0) {
			found = true;
			break;
		}
	}
	if (!found) {
		throw new RuntimeException("An impossible situation ocurred");
	}
	Range commonPieceRange = commonSidePiece.segment.asRange().intersection(neighborPiece.segment.asRange());
	int x, y;
	if (commonSidePiece.isVertical()) {
		x = commonSidePiece.getLine().getStaticCoordFromSide(neighborPiece.direction);
		y = commonPieceRange.min;
	} else {
		x = commonPieceRange.min;
		y = commonSidePiece.getLine().getStaticCoordFromSide(neighborPiece.direction);
	}
	return new RectangleSidePiece(commonSidePiece.direction, x, y, commonPieceRange.getLength());

}

/**
 * Returns a point that resides on a side of this rectangle.
 *
 * @return
 */
public Cell getPointOnSide(CardinalDirection side, int shift) {
	if (side == null) {
		throw new NullPointerException("Argument `side` can't be null");
	}
	// Be default, shift shifts from left to right or from top to bottom.
	if (shift < 0) {
		// If shift is negative, then it will denote cell position from right to left in case side is N or S...
		if (side.isVertical()) {
			shift = width + shift - 1;
		} else {
			// Of from bottom to top if side is W or E.
			shift = height + shift - 1;
		}
	}
	switch (side) {
		case N:
			return new Cell(x + shift, y);
		case E:
			return new Cell(x + width - 1, y + shift);
		case S:
			return new Cell(x + shift, y + height - 1);
		case W:
			return new Cell(x, y + shift - 1);
		default:
			throw new UnsupportedOperationException();
	}
}

/**
 * Creates a new {@link Rectangle} around the same central point but with dimensions reduced by {@code dSize*2} and
 * shifted to {@link CardinalDirection#SE} by {@code dSize}.
 *
 * @param dSize
 * @return
 */
public Rectangle shrink(int dSize) {
	return new Rectangle(x + dSize, y + dSize, width - dSize * 2, height - dSize * 2);
}

public int getMaxX() {
	return x + width - 1;
}

public int getMaxY() {
	return y + width - 1;
}

public int getX() {
	return x;
}

public int getY() {
	return y;
}

public boolean contains(int x, int y) {
	return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
}

public int getWidth() {
	return width;
}

public int getHeight() {
	return height;
}

public java.awt.Rectangle toAwtRectangle() {
	return new java.awt.Rectangle(x, y, width, height);
}

@Override
public boolean equals(Object o) {
	if (this == o) return true;
	if (o == null || getClass() != o.getClass()) return false;

	Rectangle that = (Rectangle) o;

	if (height != that.height) return false;
	if (width != that.width) return false;
	if (x != that.x) return false;
	if (y != that.y) return false;

	return true;
}

@Override
public int hashCode() {
	int result = x;
	result = 31 * result + y;
	result = 31 * result + width;
	result = 31 * result + height;
	return result;
}

/**
 * Finds out which side of this rectangle intersects by its dynamic coord an opposite side of another rectangle.
 *
 * @param r
 * 	Another rectangle
 * @return A side of this rectangle that intersects by dynamic coordinate with a side of another rectangle.
 * @see Cell#getDynamicCoord(Orientation) For what a dynamic coord is
 */
}
