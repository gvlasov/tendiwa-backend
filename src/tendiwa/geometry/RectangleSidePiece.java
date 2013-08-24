package tendiwa.geometry;

import static tendiwa.geometry.Directions.N;
import static tendiwa.geometry.Directions.NE;
import static tendiwa.geometry.Directions.SE;
import static tendiwa.geometry.Directions.SW;
import static tendiwa.geometry.Directions.W;

import java.awt.Point;
import java.util.Arrays;

import tendiwa.core.meta.Range;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

/**
 * Represents a line on rectangle's direction that divides cells inside the
 * rectangle from cells outside the rectangle. Unlike {@link Segment} it doesn't
 * describe specific cells, but rather defines a line between cells.
 * 
 * @param direction
 * @param startCoord
 * @param width
 */
public class RectangleSidePiece {
	final CardinalDirection direction;
	final Segment segment;
	final IntercellularLine line;

	public RectangleSidePiece(CardinalDirection side, int x, int y, int length) {
		Orientation orientation = side.getOrientation().reverted();
		int constantCoord = orientation.isVertical() ? x : y;
		if (side == Directions.S || side == Directions.E) {
			constantCoord++;
		}
		line = new IntercellularLine(orientation, constantCoord);
		segment = new Segment(x, y, length, orientation);
		this.direction = side;
	}
	/**
	 * Returns an IntercellularLine on which this RectangleSidePiece lies.
	 * 
	 * @return
	 */
	public IntercellularLine getLine() {
		return line;
	}
	public CardinalDirection getDirection() {
		return direction;
	}
	public Segment getSegment() {
		return segment;
	}
	public boolean isVertical() {
		return line.orientation.isVertical();
	}
	/**
	 * 
	 * @param piece
	 * @return
	 */
	public boolean touches(RectangleSidePiece piece) {
		if (piece.direction != direction.opposite()) {
			return false;
		}
		return piece.line.equals(line) && Range.overlap(
			segment.getStartCoord(),
			segment.getEndCoord(),
			piece.segment.getStartCoord(),
			piece.segment.getEndCoord());
	}
	public int intersectionByDynamicCoord(RectangleSidePiece piece2) {
		return Range.lengthOfIntersection(
			new Range(piece2.segment.getStartCoord(), piece2.segment
				.getEndCoord()),
			new Range(segment.getStartCoord(), segment.getEndCoord()));
	}
	int distanceTo(RectangleSidePiece piece) {
		return piece.line.distanceTo(line);
	}
	/**
	 * Creates [0..2] new ranges, changes coord and length of this one.
	 * 
	 * @param cutterRange
	 * @return
	 */
	ImmutableCollection<RectangleSidePiece> cutWithRange(Range cutterRange) {
		assert cutterRange != null;
		Builder<RectangleSidePiece> builder = ImmutableSet
			.<RectangleSidePiece> builder();
		int cutteeStart = segment.getStartCoord();
		int cutteeEnd = segment.getEndCoord();
		if (cutteeStart < cutterRange.min) {
			builder.add(new RectangleSidePiece(
				direction,
				line.orientation.isHorizontal() ? cutteeStart : segment.x,
				line.orientation.isVertical() ? cutteeStart : segment.y,
				cutterRange.min - cutteeStart));
		}
		if (cutteeEnd > cutterRange.max) {
			builder
				.add(new RectangleSidePiece(
					direction,
					line.orientation.isHorizontal() ? cutterRange.max + 1 : segment.x,
					line.orientation.isVertical() ? cutterRange.max + 1 : segment.y,
					cutteeEnd - cutterRange.max));
		}
		Range cutteeRange = new Range(cutteeStart, cutteeEnd);

		segment.x = line.orientation.isHorizontal() ? cutteeRange
			.intersection(cutterRange).min : segment.x;
		segment.y = line.orientation.isVertical() ? cutteeRange
			.intersection(cutterRange).min : segment.y;
		segment.length = Range.lengthOfIntersection(cutteeRange, cutterRange);
		return builder.build();
	}
	@Override
	public String toString() {
		return direction + "-" + segment.length + "@" + hashCode();
	}
	/**
	 * 
	 * @param splitter
	 * @return An array of 0-2 elements (no null values);
	 */
	RectangleSidePiece[] splitWithPiece(RectangleSidePiece splitter) {
		assert splitter != null;
		Segment[] newSegments = segment.splitWithSegment(
			splitter.segment.getStartCoord(),
			splitter.segment.length);
		int arraySize = 0;
		for (Segment segment : newSegments) {
			if (segment != null) {
				arraySize++;
			}
		}
		RectangleSidePiece[] answer = new RectangleSidePiece[arraySize];
		int index = 0;
		for (Segment segment : newSegments) {
			if (segment == null) {
				continue;
			}
			if (segment == this.segment) {
				return new RectangleSidePiece[] {
					this
				};
			}
			answer[index++] = new RectangleSidePiece(
				direction,
				segment.x,
				segment.y,
				segment.length);
		}
		return answer;
	}
	/**
	 * Creates a rectangle out of this RectangleSidePiece, such that this piece
	 * forms one side of a Rectangle, and another dimension of the Rectangle is
	 * defined by an argument.
	 * 
	 * @return a new rectangle.
	 * @throws IllegalArgumentException
	 *             if {@code anotherDimensionLength} is less than or equal to
	 *             zero.
	 */
	public EnhancedRectangle createRectangle(int anotherDimensionLength) {
		if (anotherDimensionLength <= 0) {
			throw new IllegalArgumentException(
				"anotherDimensionLength must be greater than 0, but it is " + anotherDimensionLength);
		}
		Point startPoint = segment.getEndPoint(isVertical() ? N : W);
		OrdinalDirection growDirection;
		switch (direction) {
			case N:
				growDirection = SE;
			case E:
				growDirection = SW;
			case S:
				growDirection = NE;
			case W:
			default:
				growDirection = SE;
		}
		int width, height;
		if (isVertical()) {
			width = anotherDimensionLength;
			height = segment.length;
		} else {
			width = segment.length;
			height = anotherDimensionLength;
		}
		return EnhancedRectangle.growFromPoint(
			startPoint.x,
			startPoint.y,
			growDirection,
			width,
			height);
	}
	boolean contains(RectangleSidePiece piece) {
		assert piece.line.equals(line);
		assert piece.direction == direction;
		int thisStartCoord = segment.getStartCoord();
		int pieceStartCoord = piece.segment.getStartCoord();
		return new Range(thisStartCoord, thisStartCoord + segment.length)
			.contains(new Range(
				pieceStartCoord,
				pieceStartCoord + piece.segment.length));
	}
	int perpendicularDistanceTo(Point point) {
		assert line.hasPointFromSide(point, direction);
		if (isVertical()) {
			return Math.abs(line.getStaticCoordFromSide(direction) - point.x);
		} else {
			return Math.abs(line.getStaticCoordFromSide(direction) - point.y);
		}
	}
	public boolean overlaps(RectangleSidePiece piece) {
		if (line.equals(piece.line) && Range.overlap(
			segment.getStartCoord(),
			segment.getEndCoord(),
			piece.segment.getStartCoord(),
			piece.segment.getEndCoord())) {
			return true;
		}
		return false;
	}
}
