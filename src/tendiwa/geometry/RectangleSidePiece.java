package tendiwa.geometry;

import java.awt.Rectangle;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import tendiwa.core.meta.Range;

/**
 * Represents a line on rectangle's direction that divides cells inside the
 * rectangle from cells outside the rectangle. Unlike {@link Segment} it doesn't
 * describe specific cells, but rather defines a line between cells.
 * 
 * @param r
 * @param direction
 * @param startCoord
 * @param width
 */
public class RectangleSidePiece {
	final Rectangle r;
	final CardinalDirection direction;
	final Segment segment;
	final IntercellularLine line;

	public RectangleSidePiece(Rectangle r, CardinalDirection side, int x, int y, int length) {
		Orientation orientation = side.getOrientation().reverted();
		line = new IntercellularLine(
			orientation,
			orientation.isVertical() ? x : y);
		segment = new Segment(x, y, length, orientation);
		this.r = r;
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
			new Range(segment.getStartCoord(), segment.getEndCoord()),
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
		Builder<RectangleSidePiece> builder = ImmutableSet
			.<RectangleSidePiece> builder();
		int cutteeStart = segment.getStartCoord();
		int cutteeEnd = segment.getEndCoord();
		if (cutteeStart < cutterRange.min) {
			builder.add(new RectangleSidePiece(
				r,
				direction,
				line.orientation.isHorizontal() ? cutteeStart : segment.x,
				line.orientation.isVertical() ? cutteeStart : segment.y,
				cutterRange.min - cutteeStart));
		}
		if (cutteeEnd > cutterRange.max) {
			builder
				.add(new RectangleSidePiece(
					r,
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
		return direction + "-" + segment.length;
	}
	RectangleSidePiece[] splitWithPiece(RectangleSidePiece splitter) {
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
				r,
				direction,
				segment.x,
				segment.y,
				segment.length);
		}
		return answer;
	}

}
