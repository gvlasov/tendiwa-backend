package tendiwa.geometry;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Range;
import tendiwa.core.meta.Utils;
import tendiwa.geometry.RandomGrowingRectangleSystem.VirtualFront.RectangleablePiecesCollection.PiecesJunction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class RandomGrowingRectangleSystem extends GrowingRectangleSystem {
	private int amountOfRectangles;
	private int rectangleWidth;
	/**
	 * A front #x is a set of rectangles that were created as neighbors of
	 * rectangles of a previous front #x-1, with a front #1 being solely the
	 * initial rectangle.
	 */
	private ArrayList<HashSet<RectangleArea>> rectanglesFront = new ArrayList<HashSet<RectangleArea>>();
	/**
	 * 
	 */
	private HashSet<RectangleSidePiece> freeSidePiecesOnPreviousFront = new HashSet<RectangleSidePiece>();
	private int lastFullyOccupiedFront = 0;
	private final Range possibleRectangleWidth;
	private final Range possibleRectangleWidthPlus2BorderWidth;
	private Collection<RectangleArea> virtualCurrentFront;
	private Collection<RectangleSidePiece> virtualCurrentFrontSidePieces;

	public RandomGrowingRectangleSystem(int borderWidth, Range possibleRectangleWidth, int amountOfRectangles, Point startingPoint) {
		super(0);
		this.possibleRectangleWidth = possibleRectangleWidth;
		possibleRectangleWidthPlus2BorderWidth = new Range(
			possibleRectangleWidth.min + borderWidth,
			possibleRectangleWidth.max + borderWidth);
		HashSet<RectangleArea> initialFront = new HashSet<RectangleArea>();
		RectangleArea initialRectangle = new RectangleArea(
			EnhancedRectangle.rectangleByCenterPoint(
				startingPoint,
				Chance.rand(possibleRectangleWidth),
				Chance.rand(possibleRectangleWidth)));
		initialFront.add(initialRectangle);
		rectanglesFront.add(initialFront);
		for (int i = 0; i < amountOfRectangles; i++) {
			addRectangleFromVirtualFront();
		}
	}
	public void addRectangleFromVirtualFront() {
		if (virtualCurrentFront.size() == 0) {
			buildVirtualCurrentFront();
		}
		for (RectangleArea r : virtualCurrentFront) {
			addRectangleArea(r);
			virtualCurrentFront.remove(r);
			rectanglesFront.get(lastFullyOccupiedFront + 1).add(r);
			break;
		}
	}
	public void buildVirtualCurrentFront() {
		new VirtualFront(this);
	}
	/**
	 * 
	 * @param s
	 *            Northern side piece that looks south
	 * @param e
	 *            Eastern side piece that looks west
	 * @param s
	 *            Southern side piece that looks north
	 * @param w
	 *            Western side piece that looks east
	 * @return
	 */
	private RectangleArea getRectangleBetween4Sides(RectangleSidePiece a, RectangleSidePiece b, RectangleSidePiece c, RectangleSidePiece d) {
		RectangleSidePiece n = null, e = null, s = null, w = null;
		if (a.direction == Directions.N) {
			n = a;
		} else if (a.direction == Directions.E) {
			e = a;
		} else if (a.direction == Directions.S) {
			s = a;
		} else {
			w = a;
		}
		if (b.direction == Directions.N) {
			n = b;
		} else if (b.direction == Directions.E) {
			e = b;
		} else if (b.direction == Directions.S) {
			s = b;
		} else {
			w = b;
		}
		if (c.direction == Directions.N) {
			n = c;
		} else if (c.direction == Directions.E) {
			e = c;
		} else if (c.direction == Directions.S) {
			s = c;
		} else {
			w = c;
		}
		if (d.direction == Directions.N) {
			n = d;
		} else if (d.direction == Directions.E) {
			e = d;
		} else if (d.direction == Directions.S) {
			s = d;
		} else {
			w = d;
		}
		int x = w.getLine().getStaticCoordFromSide(Directions.E) + borderWidth;
		int y = s.getLine().getStaticCoordFromSide(Directions.S) + borderWidth;
		int width = w.getLine().distanceTo(e.getLine()) - borderWidth * 2;
		if (width <= 0) {
			throw new NotImplementedException();
		}
		int height = e.getLine().distanceTo(w.getLine()) - borderWidth * 2;
		if (height <= 0) {
			throw new NotImplementedException();
		}
		return new RectangleArea(x, y, width, height);
	}
	private RectangleArea getRectangleBetween3Sides(RectangleSidePiece a, RectangleSidePiece b, RectangleSidePiece c) {
		RectangleSidePiece pieceA = a;
		RectangleSidePiece pieceB;
		if (a.line.orientation != b.line.orientation) {
			pieceB = b;
		} else {
			pieceB = c;
		}
		RectangleSidePiece notParallelPiece = findNotParallelPiece(a, b, c);
		for (RectangleSidePiece existingPiece : virtualCurrentFrontSidePieces) {
			if (piecesAreParallelAndCloseEnough(notParallelPiece, existingPiece)) {
				// If one of rectangles in this front previously placed between
				// 4 sides makes the fourth side
				return getRectangleBetween4Sides(a, b, c, existingPiece);
			}
		}

		OrdinalDirection dirBetweenPieces = (OrdinalDirection) Directions
			.getDirectionBetween(a.direction, b.direction);
		return (RectangleArea) EnhancedRectangle.growFromIntersection(
			pieceA.line,
			pieceB.line,
			dirBetweenPieces,
			Chance.rand(possibleRectangleWidth),
			Chance.rand(possibleRectangleWidth));
	}
	private boolean piecesAreParallelAndCloseEnough(RectangleSidePiece a, RectangleSidePiece b) {
		if (!a.direction.isOpposite(b.direction)) {
			// Find pieces whose directions are opposite...
			return false;
		}
		if (!possibleRectangleWidthPlus2BorderWidth.contains(a
			.getLine()
			.distanceTo(b.getLine()))) {
			// ... and which are %possibleRectangleWidth% cells far from each
			// other...
			return false;
		}
		if (a.intersectionByDynamicCoord(b) == 0) {
			// and which can contain a rectangle between them.
			return false;
		}
		return true;
	}
	/**
	 * Finds a non-parallel piece out of 3 pieces.
	 * 
	 * @param piece1
	 * @param piece2
	 * @param piece3
	 * @return
	 */
	private RectangleSidePiece findNotParallelPiece(RectangleSidePiece piece1, RectangleSidePiece piece2, RectangleSidePiece piece3) {
		if (piece1.line.isParallel(piece2.line)) {
			return piece3;
		}
		if (piece1.line.isParallel(piece3.line)) {
			return piece2;
		}
		if (piece2.line.isParallel(piece3.line)) {
			return piece1;
		}
		throw new IllegalArgumentException("All pieces are parallel");
	}
	/**
	 * Finds all places in this RectangleSystem where a rectangle can be placed
	 * neighboring two or more unoccupied RectnalgeSidePieces.
	 * 
	 * @return
	 */
	private VirtualFront getVirtualFront() {
		return new VirtualFront(this);
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
		OrdinalDirection quadrantWhereRectnagleLies = (OrdinalDirection) Directions
			.getDirectionBetween(piece1.direction, piece2.direction);
		int squareSize = possibleRectangleWidth.max;
		EnhancedRectangle r = EnhancedRectangle.growFromIntersection(
			piece1.line,
			piece1.line,
			quadrantWhereRectnagleLies,
			squareSize,
			squareSize);
		return r.touches(piece1) && r.touches(piece2);
	}
	public void setRectangleWidth(int width) {
		this.rectangleWidth = width;
	}
	public void build() {

	}

	class VirtualFront {
		Collection<Pair<RectangleSidePiece, RectangleSidePiece>> parallelSides = new ArrayList<Pair<RectangleSidePiece, RectangleSidePiece>>();
		private RectangleablePiecesCollection pieces = new RectangleablePiecesCollection();
		private Collection<RectangleArea> fourSidedRectangles = new ArrayList<RectangleArea>();
		private Multimap<RectangleArea, RectangleSidePiece> fourSideFormingPieces = HashMultimap.create();
		private Collection<RectangleArea> threeSidedRectangles = new ArrayList<RectangleArea>();
		private Collection<RectangleArea> twoSidedRectangles = new ArrayList<RectangleArea>();
		private Collection<RectangleArea> parallelSidedRectangles = new ArrayList<RectangleArea>();
		private Collection<RectangleArea> oneSidedRectangles = new ArrayList<RectangleArea>();
		private boolean builtNew;

		VirtualFront() {
			pieces.addAndSplitPieces(freeSidePiecesOnPreviousFront);
			while (true) {
				Multimap<RectangleArea, RectangleSidePiece> newVirtualRectangles = build4SidedRectangles();
				if (newVirtualRectangles.size() > 0) {
					continue;
				}
				splitPiecesWithRectangles(newVirtualRectangles);

				build3SidedRectangles();
				splitPiecesWithRectangles(threeSidedRectangles);
				if (builtNew) {
					continue;
				}
				build2SidedRectangles(twoSidedRectangles);
				buildParallelSidedRectangles(parallelSidedRectangles);
				build1SidedRectangles(oneSidedRectangles);
				break;
			}
		}
		/**
		 * Places on this VirtualFront all the rectangles that can be built in
		 * it touching 4 RectangleSidePieces on previous front.
		 * 
		 * @return A map where keys are RectangleAreas newly created during
		 *         current call to this method, and values are collections of
		 *         new free pieces these rectangles add to VirtualFront.
		 */
		private Multimap<RectangleArea, RectangleSidePiece> build4SidedRectangles() {
			Multimap<RectangleArea, RectangleSidePiece> newRectangles = HashMultimap.create();
			for (PiecesJunction junction : pieces.get4SidedJunctions()) {
				RectangleSidePiece piece1 = pairPair.getLeft().getLeft();
				RectangleSidePiece piece2 = pairPair.getRight().getLeft();
				if (piece1.line.isParallel(piece2.line)) {
					continue;
				}
				if (canRectangleBePlacedTouching(piece1, piece2)) {
					RectangleSidePiece piece3 = pairPair.getLeft().getRight();
					RectangleSidePiece piece4 = pairPair.getRight().getRight();
					RectangleArea r = getRectangleBetween4Sides(
						piece1,
						piece2,
						piece3,
						piece4);
					fourSidedRectangles.add(r);
					ArrayList<RectangleSidePiece> pieces = new ArrayList<RectangleSidePiece>();
					Collections.addAll(pieces, piece1, piece2, piece3, piece4);
					fourSideFormingPieces.put(r, pieces);
					newRectangles.put(r, piece1);
				}
			}
			return newRectangles;
		}
		private void splitPiecesWithRectangles(HashMap<RectangleArea, Collection<RectangleSidePiece>> rectanglesToPieces) {
			Collection<RectangleSidePiece> splitPieces = new ArrayList<RectangleSidePiece>();
			for (RectangleArea r : rectanglesToPieces.keySet()) {
				Collection<RectangleSidePiece> fourPieces = rectanglesToPieces
					.get(r);
				for (RectangleSidePiece piece : rectanglesToPieces.get(r)) {
					RectangleSidePiece rSidePiece = r
						.getSideAsSidePiece(piece.direction.opposite());
					splitPieces.addAll(cutPieceWithParallelPiece(
						piece,
						rSidePiece));
					freeSidePiecesOnPreviousFront.remove(piece);
				}
			}
			freeSidePiecesOnPreviousFront.addAll(splitPieces);
		}
		private void build3SidedRectangles() {

		}
		private void findParallelRectangleablePieces() {
			HashMap<RectangleSidePiece, HashSet<Pair<RectangleSidePiece, RectangleSidePiece>>> candidatesForCandidates = new HashMap<RectangleSidePiece, HashSet<Pair<RectangleSidePiece, RectangleSidePiece>>>();
			ArrayList<Pair<RectangleSidePiece, RectangleSidePiece>> answer = new ArrayList<Pair<RectangleSidePiece, RectangleSidePiece>>();
			for (Pair<RectangleSidePiece, RectangleSidePiece> pieces : Utils
				.combinationPairsIterable(RandomGrowingRectangleSystem.this.freeSidePiecesOnPreviousFront)) {
				RectangleSidePiece piece1 = pieces.getLeft();
				RectangleSidePiece piece2 = pieces.getRight();
				if (!piecesAreParallelAndCloseEnough(piece1, piece2)) {
					continue;
				}
				candidatesForCandidates.get(piece1).add(pieces);
				candidatesForCandidates.get(piece2).add(pieces);
			}
			for (Map.Entry<RectangleSidePiece, HashSet<Pair<RectangleSidePiece, RectangleSidePiece>>> entry : candidatesForCandidates
				.entrySet()) {
				// Among all parallel side pieces that are facing entry.key,
				// find the one that is closest to entry.key
				Pair<RectangleSidePiece, RectangleSidePiece> minDistancePair = Utils
					.getRandomElement(entry.getValue());
				IntercellularLine line1 = minDistancePair.getLeft().getLine();
				IntercellularLine line2 = minDistancePair.getRight().getLine();
				int minDistance = line1.distanceTo(line2);
				for (Pair<RectangleSidePiece, RectangleSidePiece> pair : entry
					.getValue()) {
					line1 = pair.getLeft().getLine();
					line2 = pair.getRight().getLine();
					int distance = line1.distanceTo(line2);
					if (distance < minDistance) {
						minDistancePair = pair;
						minDistance = distance;
					}
				}
				parallelPieces.add(minDistancePair);
			}
		}
		/**
		 * Returns one, two or three pieces that appear when this piece is cut
		 * by end points of a piece parallel to this one.
		 * 
		 * @param rSidePiece
		 */
		private Collection<RectangleSidePiece> cutPieceWithParallelPiece(RectangleSidePiece cuttee, RectangleSidePiece cutter) {
			Collection<RectangleSidePiece> answer = new ArrayList<RectangleSidePiece>();
			Range cutterRange = new Range(
				cutter.segment.getStartCoord(),
				cutter.segment.getEndCoord());
			int cutteeStart = cuttee.segment.getStartCoord();
			int cutteeEnd = cuttee.segment.getEndCoord();
			if (cutteeStart < cutterRange.min) {
				answer
					.add(new RectangleSidePiece(
						cuttee.r,
						cuttee.direction,
						cuttee.line.orientation.isHorizontal() ? cutteeStart : cuttee.segment.x,
						cuttee.line.orientation.isVertical() ? cutteeStart : cuttee.segment.y,
						cutterRange.min - cutteeStart));
			}
			if (cutteeEnd > cutterRange.max) {
				answer
					.add(new RectangleSidePiece(
						cuttee.r,
						cuttee.direction,
						cuttee.line.orientation.isHorizontal() ? cutterRange.max + 1 : cuttee.segment.x,
						cuttee.line.orientation.isVertical() ? cutterRange.max + 1 : cuttee.segment.y,
						cutteeEnd - cutterRange.max));
			}
			Range cutteeRange = new Range(cutteeStart, cutteeEnd);
			answer.add(new RectangleSidePiece(
				cuttee.r,
				cuttee.direction,
				cuttee.line.orientation.isHorizontal() ? cutteeRange
					.intersection(cutterRange).min : cuttee.segment.x,
				cuttee.line.orientation.isVertical() ? cutteeRange
					.intersection(cutterRange).min : cuttee.segment.y,
				Range.lengthOfIntersection(cutteeRange, cutterRange)));
			return answer;
		}

		class RectangleablePiecesCollection {
			private Map<CardinalDirection, Collection<RectangleSidePiece>> pieces = new HashMap<CardinalDirection, Collection<RectangleSidePiece>>();
			private Map<RectangleSidePiece, PiecesJunction> junctions = new HashMap<RectangleSidePiece, PiecesJunction>();
			private Map<RectangleSidePiece, Integer> distanceToCurrentParallelSide = new HashMap<RectangleSidePiece, Integer>();

			private RectangleablePiecesCollection() {

			}
			public Collection<PiecesJunction> get4SidedJunctions() {
				Collection<PiecesJunction> answer = new ArrayList<PiecesJunction>();
				for (PiecesJunction junction : junctions.values()) {
					if (junction.amountOfSides() == 4) {
						answer.add(junction);
					}
				}
				return answer;
			}
			public Pair<Pair<RectangleSidePiece, RectangleSidePiece>, Pair<RectangleSidePiece, RectangleSidePiece>> get4SidedPiecesPairs() {
				// TODO Auto-generated method stub
				return null;
			}
			private void addAndSplitPieces(Collection<RectangleSidePiece> newPieces) {
				Map<RectangleSidePiece, RectangleSidePiece> parallelsMarkedForRecalculation = new HashMap<RectangleSidePiece, RectangleSidePiece>();
				for (RectangleSidePiece newPiece : newPieces) {
					for (RectangleSidePiece oldParallelPiece : pieces
						.get(newPiece.direction)) {
						if (pieceIsBetterThanCurrentParallel(
							oldParallelPiece,
							newPiece)) {
							int distance = newPiece
								.distanceTo(oldParallelPiece);
							distanceToCurrentParallelSide.put(
								oldParallelPiece,
								distance);
							distanceToCurrentParallelSide.put(
								newPiece,
								distance);
							junctions.
						}
					}
					pieces.get(newPiece.direction).add(newPiece);
				}
			}
			private boolean pieceIsBetterThanCurrentParallel(RectangleSidePiece oldParallelPiece, RectangleSidePiece newParallelPiece) {
				boolean cond2 = piecesAreParallelAndCloseEnough(
					newParallelPiece,
					oldParallelPiece);
				boolean cond3 = distanceToCurrentParallelSide
					.get(oldParallelPiece) < newParallelPiece
					.distanceTo(oldParallelPiece);
				return cond2 && cond3;
			}

			class PiecesJunction {
				private Map<CardinalDirection, RectangleSidePiece> pieces = new HashMap<CardinalDirection, RectangleSidePiece>();

				public PiecesJunction() {
				}
				public int amountOfSides() {
					return pieces.size();
				}
				private void setPiece(RectangleSidePiece newPiece) {
					RectangleSidePiece oppositePiece = pieces
						.get(newPiece.direction.opposite());
					junctions.remove(oppositePiece);
					distanceToCurrentParallelSide.put(
						newPiece,
						newPiece.line.distanceTo(oppositePiece.line));
					pieces.put(newPiece.direction, newPiece);
				}
			}
		}
	}
}
