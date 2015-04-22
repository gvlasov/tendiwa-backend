package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.OrdinalDirection;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.core.meta.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public interface Rectangle extends RecTree, BoundedCellSet, Dimension {


	public int x();

	public int y();

	public int width();

	public int height();


	public default Collection<BasicCell> getCells() {
		ArrayList<BasicCell> answer = new ArrayList<>();
		for (int i = x(); i < x() + width(); i++) {
			for (int j = y(); j < y() + height(); j++) {
				answer.add(new BasicCell(i, j));
			}
		}
		return answer;
	}

	/**
	 * Stretch rectangle
	 *
	 * @param side
	 * 	SideTest where rectangle stretches
	 * @param amount
	 * 	Amount of cells to stretch. If depth > 0, then rectangle will grow, if depth < 0, then rectangle will
	 * 	shrink. Notice that if SideTest == N or W, rectangle.x and rectangle.y will move. If depth == 0 then
	 * 	rectangle stays the same.
	 */
	public default Rectangle stretch(CardinalDirection side, int amount) {
		switch (side) {
			case N:
				return new BasicRectangle(
					x(),
					y() - amount,
					width(),
					height() + amount
				);
			case E:
				return new BasicRectangle(
					x(),
					y(),
					width() + amount,
					height()
				);
			case S:
				return new BasicRectangle(
					x(),
					y(),
					width(),
					height() + amount
				);
			case W:
				return new BasicRectangle(
					x() - amount,
					y(),
					width() + amount,
					height()
				);
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Returns a {@link Point2D} that is right in the middle of this Rectangle.
	 *
	 * @return
	 */
	default Point2D getCenterPoint() {
		return new BasicPoint2D(
			x() + ((double) width() / 2),
			y() + ((double) height() / 2)
		);
	}

	@Override
	default ImmutableCollection<NamedRecTree> parts() {
		return ImmutableList.of(new NamedRecTree(this, Optional.empty()));
	}

	default int centerX() {
		return x() + width() / 2;
	}

	default int centerY() {
		return y() + height() / 2;
	}

	@Override
	default RecTree rotate(Rotation rotation) {
		int newWidth, newHeight;
		switch (rotation) {
			case CLOCKWISE:
			case COUNTER_CLOCKWISE:
				newWidth = height();
				newHeight = width();
				break;
			case HALF_CIRCLE:
				newWidth = width();
				newHeight = height();
				break;
			default:
				throw new UnsupportedOperationException("Operation for rotation " + rotation + " is not implemented yet");
		}
		return new BasicRectangle(x(), y(), newWidth, newHeight);
	}

	default Iterable<Rectangle> getRectangles() {
		return ImmutableList.of(this);
	}


	default StepPlaceNextAt repeat(int count) {
		return new StepPlaceNextAt(count, this);
	}

	default Rectangle bounds() {
		return this;
	}

	default int getMinStaticCoord(Orientation orientation) {
		if (orientation == Orientation.HORIZONTAL) {
			return x();
		} else {
			assert orientation == Orientation.VERTICAL;
			return y();
		}
	}

	default int getMaxStaticCoord(Orientation orientation) {
		if (orientation == Orientation.HORIZONTAL) {
			return x() + width() - 1;
		} else {
			assert orientation == Orientation.VERTICAL;
			return y() + height() - 1;
		}
	}

	default int getMinDynamicCoord(Orientation orientation) {
		if (orientation == Orientation.HORIZONTAL) {
			return y();
		} else {
			assert orientation == Orientation.VERTICAL;
			return x();
		}
	}

	default int getStaticLength(Orientation orientation) {
		if (orientation == Orientation.HORIZONTAL) {
			return width();
		} else {
			assert orientation == Orientation.VERTICAL;
			return height();
		}
	}

	default int getMaxDynamicCoord(Orientation orientation) {
		if (orientation == Orientation.HORIZONTAL) {
			return y() + height() - 1;
		} else {
			assert orientation == Orientation.VERTICAL;
			return x() + width() - 1;
		}
	}

	/**
	 * Creates a new {@link Rectangle} around the same central point but with dimensions reduced by {@code dSize*2} and
	 * shifted to {@link OrdinalDirection#SE} by {@code dSize}.
	 *
	 * @param dSize
	 * 	A radius to shrink by. This number will be cut from four sides. Note that this still can be negative
	 * 	(then at actually extend this rectangle instead of shrinking it).
	 * @return A new, srinked rectangle.
	 * @see #stretch(int)
	 */
	default Rectangle shrink(int dSize) {
		return new BasicRectangle(
			x() + dSize,
			y() + dSize,
			width() - dSize * 2,
			height() - dSize * 2
		);
	}

	/**
	 * Creates a new {@link Rectangle} around the same central point but with dimensions increased by {@code dSize*2}
	 * and shifted to {@link OrdinalDirection#NW} by {@code dSize}.
	 *
	 * @param dSize
	 * 	A radius to stretch by. This number will be cut from four sides. Note that this still can be negative
	 * 	(then at actually extend this rectangle instead of shrinking it).
	 * @return A new, stretched rectangle.
	 * @see #shrink(int) (int)
	 */
	default Rectangle stretch(int dSize) {
		return new BasicRectangle(
			x() - dSize,
			y() - dSize,
			width() + dSize * 2,
			height() + dSize * 2
		);
	}

	default int maxX() {
		return x() + width() - 1;
	}

	default int maxY() {
		return y() + height() - 1;
	}

	/**
	 * Checks if a rectangle contains a <i>cell</i> with given coordinates.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 * @return true if rectangle contains the cell, false otherwise.
	 */
	@Override
	default boolean contains(int x, int y) {
		return x >= this.x() && x < this.x() + this.width() && y >= this.y() && y < this.y() + this.height();
	}

	default boolean contains(Cell cell) {
		return contains(cell.x(), cell.y());
	}


	default int area() {
		return width() * height();
	}

	default Rectangle moveTo(int x, int y) {
		return new BasicRectangle(
			x,
			y,
			width(),
			height()
		);
	}

	default Rectangle dragSide(CardinalDirection dir, int amount) {
		Objects.requireNonNull(dir);
		switch (dir) {
			case N:
				return new BasicRectangle(x(), y() - amount, width(), height() + amount);
			case E:
				return new BasicRectangle(x(), y(), width() + amount, height());
			case S:
				return new BasicRectangle(x(), y(), width(), height() + amount);
			case W:
				return new BasicRectangle(x() - amount, y(), width() + amount, height());
			default:
				throw new UnsupportedOperationException();
		}
	}

	@Override
	default Rectangle translate(int x, int y) {
		return new BasicRectangle(
			x() + x,
			y() + y,
			width(),
			height()
		);
	}

	default public Side side(CardinalDirection direction) {
		return new BasicRectangleSide(this, direction);
	}

	default public boolean intersects(Rectangle b) {
		Rectangle a = this;
		int tw = a.width();
		int th = a.height();
		int rw = b.width();
		int rh = b.height();
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = a.x();
		int ty = a.y();
		int rx = b.x();
		int ry = b.y();
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

	@Override
	default Stream<Rectangle> rectangles() {
		return Stream.of(this);
	}

	default Optional<Rectangle> intersection(Rectangle rectangle) {
		int xMin = Math.max(this.x(), rectangle.x());
		int yMax = Math.min(
			this.y() + this.height(),
			rectangle.y() + rectangle.height()
		);
		int yMin = Math.max(this.y(), rectangle.y());
		int xMax = Math.min(
			this.x() + this.width(),
			rectangle.x() + rectangle.width()
		);
		if (xMax > xMin && yMax > yMin) {
			return Optional.of(
				new BasicRectangle(
					xMin,
					yMin,
					xMax - xMin,
					yMax - yMin
				)
			);
		} else {
			return Optional.empty();
		}
	}

	default Rectangle getBounds() {
		return this;
	}

	default RecTree part(String name) {
		throw new UnsupportedOperationException("Trying to get part of a single rectangle");
	}

	default RecTree nestedPart(String name) {
		throw new UnsupportedOperationException("Trying to get part of a single rectangle");
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
	default boolean intersects(Segment2D segment) {
		return new RectangleSegmentIntersection(this, segment).intersect();
	}

	default int perpendicularDistanceTo(Rectangle rectangle) {
		throw new UnsupportedOperationException();
	}

}
