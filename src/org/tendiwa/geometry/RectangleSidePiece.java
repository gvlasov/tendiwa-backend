package org.tendiwa.geometry;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import org.tendiwa.core.*;
import org.tendiwa.core.meta.CellPosition;
import org.tendiwa.core.meta.Range;

import java.util.ArrayList;
import java.util.Collection;

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

/**
 * Returns distance in cells between lines on which this and another piece lie.
 *
 * @param piece
 * @return
 */
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
		.<RectangleSidePiece>builder();
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
			return new RectangleSidePiece[]{
				this
			};
		}
		answer[index++] = new RectangleSidePiece(
			direction,
			segment.x,
			segment.y,
			segment.length);
		assert contains(answer[index - 1]);
	}
	return answer;
}

/**
 * Creates a rectangle out of this RectangleSidePiece, such that this piece forms one side of a rectangle, and another
 * dimension of the Rectangle is defined by an argument.
 *
 * @return A new rectangle one of whose sies is {@code splitter}.
 * @throws IllegalArgumentException
 * 	if {@code anotherDimensionLength} is less than or equal to zero.
 */
public EnhancedRectangle createRectangle(int anotherDimensionLength) {
	if (anotherDimensionLength <= 0) {
		throw new IllegalArgumentException(
			"anotherDimensionLength must be greater than 0, but it is " + anotherDimensionLength);
	}
	// Get point on inner side of a RectangleSidePiece to grow a rectangle from it.
	Cell startPoint = Cell.fromStaticAndDynamic(
		line.getStaticCoordFromSide(direction.opposite()),
		segment.getStartCoord(),
		segment.orientation);
	OrdinalDirection growDirection;
	switch (direction) {
		case N:
			growDirection = Directions.SE;
			break;
		case E:
			growDirection = Directions.SW;
			break;
		case S:
			growDirection = Directions.NE;
			break;
		case W:
		default:
			growDirection = Directions.SE;
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
		startPoint.getX(),
		startPoint.getY(),
		growDirection,
		width,
		height);
}

public boolean contains(RectangleSidePiece piece) {
	if (!piece.line.equals(line)) {
		return false;
	}
	assert piece.direction == direction;
	int thisStartCoord = segment.getStartCoord();
	int pieceStartCoord = piece.segment.getStartCoord();
	return new Range(thisStartCoord, thisStartCoord + segment.length)
		.contains(new Range(
			pieceStartCoord,
			pieceStartCoord + piece.segment.length));
}

/**
 * Distance by static coordinate of this piece to same coordinate of a point.
 *
 * @param point
 * @return
 */
int perpendicularDistanceTo(CellPosition point) {
	assert line.hasPointFromSide(point, direction);
	if (isVertical()) {
		return Math.abs(line.getStaticCoordFromSide(direction) - point.getX());
	} else {
		return Math.abs(line.getStaticCoordFromSide(direction) - point.getY());
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

public RectangleSidePiece[] splitWithPieces(Collection<RectangleSidePiece> touchingPieces) {
	assert touchingPieces != null;
	assert !touchingPieces.isEmpty();
	Collection<Range> touchingRanges = new ArrayList<Range>(
		touchingPieces.size());
	for (RectangleSidePiece piece : touchingPieces) {
		touchingRanges.add(piece.segment.asRange());
	}
	Range[] segmentsRanges = segment.asRange().splitWithRanges(
		touchingRanges);
	RectangleSidePiece[] answer = new RectangleSidePiece[segmentsRanges.length];
	int i = 0;
	for (Range range : segmentsRanges) {
		Cell point = Cell.fromStaticAndDynamic(
			segment.getStaticCoord(),
			range.min,
			segment.orientation);
		answer[i++] = new RectangleSidePiece(
			direction,
			point.getX(),
			point.getY(),
			range.getLength());
		assert contains(answer[i - 1]);
	}
	return answer;
}

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((direction == null) ? 0 : direction
		.hashCode());
	result = prime * result + ((line == null) ? 0 : line.hashCode());
	result = prime * result + ((segment == null) ? 0 : segment.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	RectangleSidePiece other = (RectangleSidePiece) obj;
	if (direction != other.direction)
		return false;
	if (line == null) {
		if (other.line != null)
			return false;
	} else if (!line.equals(other.line))
		return false;
	if (segment == null) {
		if (other.segment != null)
			return false;
	} else if (!segment.equals(other.segment))
		return false;
	return true;
}

/**
 * Returns static coordinate in front of piece.
 *
 * @return
 * @see Segment#getStaticCoord() for getting static coord behind piece.
 */
int getStaticCoordInFront() {
	return line.getStaticCoordFromSide(direction);
}
}
