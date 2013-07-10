package tendiwa.geometry;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.commons.lang3.tuple.Pair;

import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Range;
import tendiwa.core.meta.Side;
import tendiwa.core.meta.Utils;

public class RandomGrowingRectangleSystem extends GrowingRectangleSystem {
	private int amountOfRectangles;
	private int rectangleWidth;
	/**
	 * A front #x is a set of rectangles that were created as neighbours of
	 * rectangles of a previous front #x-1, with a front #1 being solely the
	 * initial rectangle.
	 */
	private ArrayList<HashSet<RectangleArea>> rectanglesFront = new ArrayList<HashSet<RectangleArea>>();
	/**
	 * 
	 */
	private HashSet<RectangleArea> fullyOccupiedRectangles = new HashSet<RectangleArea>();
	private HashSet<RectangleSidePiece> freeSidePiecesOnPreviousFront = new HashSet<RectangleSidePiece>();
	private int lastFullyOccupiedFront = 0;
	private final Range possibleRectangleWidth;

	public RandomGrowingRectangleSystem(int borderWidth, Range possibleRectangleWidth, int amountOfRectangles, Point startingPoint) {
		super(0);
		this.possibleRectangleWidth = possibleRectangleWidth;
		HashSet<RectangleArea> initialFront = new HashSet<RectangleArea>();
		RectangleArea initialRectangle = new RectangleArea(EnhancedRectangle.rectangleByCenterPoint(startingPoint, Chance.rand(possibleRectangleWidth), Chance.rand(possibleRectangleWidth)));
		initialFront.add(initialRectangle);
		rectanglesFront.add(initialFront);
		startNewFront();
		for (int i = 0; i < amountOfRectangles; i++) {
			addRandomlyNewRectangle();
		}
	}
	private void startNewFront() {
		lastFullyOccupiedFront++;
		for (RectangleArea r : rectanglesFront.get(lastFullyOccupiedFront)) {
			for (Side side : Side.EACH_CARDINAL_SIDE) {
				freeSidePiecesOnPreviousFront.addAll(getSidePiecesFreeFromNeighbours(r, side));
			}
		}

	}
	public void addRandomlyNewRectangle() {
		for (RectangleSidePiece piece : freeSidePiecesOnPreviousFront) {
			HashSet<HashSet<RectangleSidePiece>> freeSideJunctions = getFreeSideJunctions();
			if (freeSideJunctions.size() > 0) {

			}
		}
	}
	/**
	 * Finds all places in this RectangleSystem where a rectangle can be placed
	 * neighboring two or more unoccupied RectnalgeSidePieces.
	 * 
	 * @return
	 */
	private HashSet<HashSet<RectangleSidePiece>> getFreeSideJunctions() {
		HashSet<HashSet<RectangleSidePiece>> answer = new HashSet<HashSet<RectangleSidePiece>>();
		for (Pair<RectangleSidePiece, RectangleSidePiece> pair : Utils.combinationPairsIterable(freeSidePiecesOnPreviousFront)) {
			if (canRectangleBePlacedTouching(pair.getLeft(), pair.getRight())) {
				addRandomRectangleBetween(pair.getLeft(), pair.getRight());
			}
		}
	}
	private void addRandomRectangleBetween(RectangleSidePiece piece1, RectangleSidePiece piece2) {
		RectangleSidePiece verticalPiece;
		RectangleSidePiece horizontalPiece;
		if (piece1.isVertical()) {
			verticalPiece = piece1;
			horizontalPiece = piece2;
		} else {
			horizontalPiece = piece1;
			verticalPiece = piece2;
		}

	}
	private boolean canRectangleBePlacedTouching(RectangleSidePiece piece1, RectangleSidePiece piece2) {
		Intersection intersection = piece1.getLine().intersectionWith(piece2.getLine());
		IntercellularLine.intersectionOf(piece1.getLine(), piece2.getLine());
		Side quadrantWhereRectnagleLies = Side.getOrdinalDirection(piece1.side, piece2.side);
		int squareSize = possibleRectangleWidth.max;
		EnhancedRectangle r = EnhancedRectangle.growFromIntersection(line1, line2, quadrantWhereRectnagleLies, squareSize, squareSize);
		return r.touches(piece1) && r.touches(piece2);
	}
	public void setRectangleWidth(int width) {
		this.rectangleWidth = width;
	}
	public void build() {

	}
}
