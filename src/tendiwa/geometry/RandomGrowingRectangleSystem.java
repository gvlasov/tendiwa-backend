package tendiwa.geometry;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import painting.TestCanvas;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Range;
import tendiwa.geometry.RandomGrowingRectangleSystem.VirtualFront.RectangleablePiecesCollection.PiecesJunction;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Sets;

public class RandomGrowingRectangleSystem extends GrowingRectangleSystem {
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
	private int lastFullyOccupiedFront = -1;
	private final Range possibleRectangleWidth;
	private final Range possibleRectangleWidthPlus2BorderWidth;
	private VirtualFront virtualCurrentFront;
	private Collection<RectangleSidePiece> virtualCurrentFrontSidePieces;
	private TestCanvas canvas;

	public RandomGrowingRectangleSystem(int borderWidth, Range possibleRectangleWidth, int amountOfRectangles, Point startingPoint, TestCanvas canvas) {
		super(borderWidth);
		this.canvas = canvas;
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
		canvas.draw(initialRectangle);
		rectanglesFront.add(initialFront);
		for (int i = 0; i < amountOfRectangles; i++) {
			addRectangleFromVirtualFront();
		}
	}
	public RandomGrowingRectangleSystem(RectangleSystem rs, Range possibleRectangleWidth) {
		super(rs.borderWidth);
		this.possibleRectangleWidth = possibleRectangleWidth;
		possibleRectangleWidthPlus2BorderWidth = new Range(
			possibleRectangleWidth.min + borderWidth,
			possibleRectangleWidth.max + borderWidth);
		HashSet<RectangleArea> initialFront = new HashSet<RectangleArea>();
		for (RectangleArea r : rs) {
			addRectangleArea(r);
			initialFront.add(r);
		}
	}
	public void addRectangleFromVirtualFront() {
		if (virtualCurrentFront == null || virtualCurrentFront.virtualRectangles
			.size() == 0) {
			buildVirtualCurrentFront();
			lastFullyOccupiedFront++;
			rectanglesFront.add(new HashSet<RectangleArea>());
		}
		Iterator<RectangleArea> iterator = virtualCurrentFront.virtualRectangles
			.iterator();
		RectangleArea r = iterator.next();
		addRectangleArea(r);
		rectanglesFront.get(lastFullyOccupiedFront + 1).add(r);
		iterator.remove();
	}
	public void buildFront() {
		if (virtualCurrentFront.virtualRectangles.size() != 0) {
			throw new IllegalStateException();
		}
		for (RectangleArea r : virtualCurrentFront.virtualRectangles) {
			addRectangleArea(r);
			rectanglesFront.get(lastFullyOccupiedFront + 1).add(r);
			virtualCurrentFront.virtualRectangles.clear();
		}

	}
	private void buildVirtualCurrentFront() {
		findFreeSidePieces();
		virtualCurrentFront = new VirtualFront();
		if (virtualCurrentFront.virtualRectangles.size() == 0) {
			throw new IllegalStateException();
		}
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
	private void findFreeSidePieces() {
		for (RectangleArea r : rectanglesFront.get(rectanglesFront.size() - 1)) {
			for (CardinalDirection side : CardinalDirection.values()) {
				freeSidePiecesOnPreviousFront.add(r.getSideAsSidePiece(side));
			}
		}
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
	private boolean canRectangleBePlacedTouching(RectangleSidePiece piece1, RectangleSidePiece piece2) {
		OrdinalDirection quadrantWhereRectnagleLies = (OrdinalDirection) Directions
			.getDirectionBetween(piece1.direction, piece2.direction);
		int squareSize = possibleRectangleWidth.max;
		EnhancedRectangle r = EnhancedRectangle.growFromIntersection(
			piece1.line,
			piece2.line,
			quadrantWhereRectnagleLies,
			squareSize,
			squareSize);
		return r.touches(piece1) && r.touches(piece2);
	}

	class VirtualFront {
		private final Collection<RectangleArea> virtualRectangles = new ArrayList<RectangleArea>();
		private final RectangleablePiecesCollection pieces;

		VirtualFront() {
			pieces = new RectangleablePiecesCollection(
				freeSidePiecesOnPreviousFront);
			pieces.buildJunctions();
			for (int numberOfSides = 4; numberOfSides > 0; numberOfSides--) {
				PiecesJunction junction = pieces
					.getOneJunctionWithOriginalPieces(numberOfSides);
				if (junction != null) {
					// If at least one n-sided junction found, then start over
					// from 4-sided junctions, because adding a rectangle will
					// probably increase the number of occupied sides in
					// existing junctions, making an x-junction to be
					// (x+1)-junction.
					occupyJunctionWithRectangle(junction);
					pieces.buildJunctions();
					numberOfSides = 4 + 1;
				}
			}
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
			RectangleSidePiece e = null, s = null, w = null;
			if (a.direction == Directions.E) {
				e = a;
			} else if (a.direction == Directions.S) {
				s = a;
			} else {
				w = a;
			}
			if (b.direction == Directions.E) {
				e = b;
			} else if (b.direction == Directions.S) {
				s = b;
			} else {
				w = b;
			}
			if (c.direction == Directions.E) {
				e = c;
			} else if (c.direction == Directions.S) {
				s = c;
			} else {
				w = c;
			}
			if (d.direction == Directions.E) {
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
			pieces.removePiecesIfOriginal(a, b, c, d);
			RectangleArea rectangle = new RectangleArea(x, y, width, height);
			pieces.modifySidesByPlacingRectangle(rectangle, a, b, c, d);
			return rectangle;
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
				if (piecesAreParallelAndCloseEnough(
					notParallelPiece,
					existingPiece)) {
					// If one of rectangles in this front previously placed
					// between 4 sides makes the fourth side
					return getRectangleBetween4Sides(a, b, c, existingPiece);
				}
			}

			OrdinalDirection dirBetweenPieces = (OrdinalDirection) Directions
				.getDirectionBetween(a.direction, b.direction);
			pieces.removePiecesIfOriginal(new RectangleSidePiece[] {
				a, b, c
			});
			RectangleArea rectangle = new RectangleArea(
				EnhancedRectangle.growFromIntersection(
					pieceA.line,
					pieceB.line,
					dirBetweenPieces,
					Chance.rand(possibleRectangleWidth),
					Chance.rand(possibleRectangleWidth)));
			pieces.modifySidesByPlacingRectangle(rectangle, a, b, c);
			return rectangle;
		}
		public RectangleArea getRectangleBetweenParallelSides(RectangleSidePiece d, RectangleSidePiece e) {
			// TODO Auto-generated method stub
			return null;
		}
		public RectangleArea getRectangleOn1Side(RectangleSidePiece piece) {
			int width = Chance.rand(possibleRectangleWidth);
			int height = Chance.rand(possibleRectangleWidth);
			int offset = Chance.rand(new Range(
				-(piece.direction.isVertical() ? width : height) + 1,
				piece.segment.length - 1));
			RectangleArea r = create(
				piece.r,
				piece.direction,
				width,
				height,
				offset);
			pieces.modifySidesByPlacingRectangle(r, piece);
			return r;
			// // canvas.draw(piece);
			// // int dynamicCoord = Chance.rand(
			// // piece.segment.getStartCoord(),
			// // piece.segment.getEndCoord());
			// int dynamicCoord = piece.segment.getStartCoord();
			// int staticCoord = piece.line
			// .getStaticCoordFromSide(piece.direction) + (piece.direction
			// .isGrowing() ? borderWidth : -borderWidth);
			// Point startPoint;
			// if (piece.isVertical()) {
			// startPoint = new Point(staticCoord, dynamicCoord);
			// } else {
			// startPoint = new Point(dynamicCoord, staticCoord);
			// }
			// // CardinalDirection growSubdirection = Chance.roll(50) ?
			// // piece.direction
			// // .clockwiseQuarter() :
			// piece.direction.counterClockwiseQuarter();
			// CardinalDirection growSubdirection = piece.direction
			// .clockwiseQuarter();
			// int startX = startPoint.x;
			// int startY = startPoint.y;
			// OrdinalDirection direction = Directions.getDirectionBetween(
			// piece.direction,
			// growSubdirection);
			// System.out.println(direction);
			// RectangleArea rectangle = new RectangleArea(
			// RectangleArea.growFromPoint(
			// startX,
			// startY,
			// direction,
			// Chance.rand(possibleRectangleWidth),
			// Chance.rand(possibleRectangleWidth)));
			// return rectangle;
		}
		public RectangleArea getRectangleBetween2Sides(RectangleSidePiece d, RectangleSidePiece e) {
			// TODO Auto-generated method stub
			return null;
		}
		/**
		 * Places on this VirtualFront all the rectangles that can be built in
		 * it touching 4 RectangleSidePieces on previous front. Also if any
		 * sides forming {@code junction} are original sides, remove them from
		 * the collection of original sides, as they are now occupied.
		 * 
		 * @return A map where keys are RectangleAreas newly created during
		 *         current call to this method, and values are collections of
		 *         new free pieces these rectangles add to VirtualFront.
		 */
		private void occupyJunctionWithRectangle(PiecesJunction junction) {
			RectangleArea r;
			switch (junction.amountOfSides()) {
				case 4:
					r = getRectangleBetween4Sides(
						junction.pieces.get(Directions.N),
						junction.pieces.get(Directions.E),
						junction.pieces.get(Directions.S),
						junction.pieces.get(Directions.W));
					break;
				case 3:
					Collection<RectangleSidePiece> values = junction.pieces
						.values();
					RectangleSidePiece a = values.iterator().next();
					RectangleSidePiece b = values.iterator().next();
					RectangleSidePiece c = values.iterator().next();
					r = getRectangleBetween3Sides(a, b, c);
					break;
				case 2:
					Collection<RectangleSidePiece> values2 = junction.pieces
						.values();
					RectangleSidePiece d = values2.iterator().next();
					RectangleSidePiece e = values2.iterator().next();
					r = d.getLine().isParallel(e.getLine()) ? getRectangleBetweenParallelSides(
						d,
						e) : getRectangleBetween2Sides(d, e);
					break;
				case 1:
					r = getRectangleOn1Side(junction.pieces
						.values()
						.iterator()
						.next());
					break;
				default:
					throw new IllegalStateException();
			}
			addVirtualRectangle(r);
		}

		private void addVirtualRectangle(RectangleArea r) {
			virtualRectangles.add(r);
			Color color = canvas.draw(r);
			System.out.println(TestCanvas.colorName(color));
		}

		class RectangleablePiecesCollection {
			private Map<CardinalDirection, TreeSet<RectangleSidePiece>> pieces = new HashMap<CardinalDirection, TreeSet<RectangleSidePiece>>();
			private Map<RectangleSidePiece, PiecesJunction> junctions = new HashMap<RectangleSidePiece, PiecesJunction>();
			private Map<RectangleSidePiece, Integer> distanceToCurrentParallelSide = new HashMap<RectangleSidePiece, Integer>();
			private final Collection<RectangleSidePiece> originalPieces;
			/**
			 * Pieces forming perimeter of previous front.
			 */
			private final Comparator<RectangleSidePiece> PIECES_COMPARATOR = new Comparator<RectangleSidePiece>() {
				@Override
				public int compare(RectangleSidePiece piece1, RectangleSidePiece piece2) {
					// TODO: Remove after debugging
					if (piece1.direction != piece2.direction) {
						throw new Error(
							"Comparing pieces with different directions");
					}
					switch (piece1.direction) {
						case N:
							return -(piece1.segment.y - piece2.segment.y);
						case E:
							return piece1.segment.x - piece2.segment.x;
						case S:
							return piece1.segment.y - piece2.segment.y;
						case W:
						default:
							return -(piece1.segment.x - piece2.segment.x);
					}
				}
			};
			private final Collection<RectangleSidePiece> newPieces = new ArrayList<RectangleSidePiece>();

			/**
			 * 
			 * @param freeSidePiecesOnPreviousFront
			 * @param piecesOfPreviousFront
			 *            RectangleSidePieces forming the outer perimeter of
			 *            previous front.
			 */
			private RectangleablePiecesCollection(Collection<RectangleSidePiece> originalPieces) {
				this.originalPieces = originalPieces;
				newPieces.addAll(originalPieces);
				for (CardinalDirection direction : CardinalDirection.values()) {
					pieces.put(direction, new TreeSet<RectangleSidePiece>(
						PIECES_COMPARATOR));
				}
			}
			/**
			 * If some of {@code pieces} are in
			 * {@link VirtualFront#originalPieces}, remove them from that
			 * collection.
			 * 
			 * @param pieces
			 */
			private void removePiecesIfOriginal(RectangleSidePiece... pieces) {
				for (RectangleSidePiece piece : pieces) {
					if (originalPieces.contains(piece)) {
						originalPieces.remove(piece);
					}
				}
			}
			/**
			 * Returns one of {@link PiecesJunction}s that:
			 * <ol>
			 * <li>Has exactly {@code numberOdSides} pieces in it;</li>
			 * <li>One of those pieces is an original piece (
			 * {@link VirtualFront#originalPieces}).
			 * </ol>
			 * 
			 * @param numberOfSides
			 * @return That piece or null if it wasn't found.
			 */
			public PiecesJunction getOneJunctionWithOriginalPieces(int numberOfSides) {
				for (Map.Entry<RectangleSidePiece, PiecesJunction> entry : junctions
					.entrySet()) {
					if (!originalPieces.contains(entry.getKey())) {
						continue;
					}
					PiecesJunction junction = entry.getValue();
					if (junction.amountOfSides() == numberOfSides) {
						return junction;
					}
				}
				return null;
			}
			private void buildJunctions() {
				for (RectangleSidePiece newPiece : newPieces) {
					pieces.get(newPiece.direction).add(newPiece);
				}
				// Set parallel pieces
				for (RectangleSidePiece newPiece : newPieces) {
					RectangleSidePiece parallelPiece = getParallelRectangleablePiece(newPiece);
					if (parallelPiece != null) {
						addPieceToJunction(parallelPiece, newPiece);
					} else {
						newJunctionOf(newPiece);
					}
				}
				// Set perpendicular pieces
				for (RectangleSidePiece newPiece : newPieces) {
					RectangleSidePiece[] perpendicularPieces = getPerpendicularRectangleablePieces(newPiece);
					boolean nothingAdded = true;
					for (RectangleSidePiece piece : perpendicularPieces) {
						if (piece != null) {
							addPieceToJunction(piece, newPiece);
							nothingAdded = false;
						}
					}
					if (nothingAdded && !junctions.containsKey(newPiece)) {
						newJunctionOf(newPiece);
					}
				}
				for (RectangleSidePiece newPiece : newPieces) {
					if (!junctions.containsKey(newPiece)) {
						newJunctionOf(newPiece);
					}
				}
				newPieces.clear();
				// Cut and call the method again if there are any derivative
				// pieces.
				for (PiecesJunction junction : junctions.values()) {
					junction.cutParallel();
				}
				if (newPieces.size() > 0) {
					buildJunctions();
				}
			}
			/**
			 * <ul>
			 * <li>Splits each of {@code pieces} with a corresponding opposite
			 * side of RectangleArea {@code r}</li>
			 * <li>Removes the pieces which were split from this
			 * {@link RectangleablePiecesCollection}</li>
			 * <li>Adds newly created split pieces to this
			 * {@link RectangleablePiecesCollection}</li>
			 * <li>Disassociates the removed pieces with their junctions</li>
			 * </ul>
			 * 
			 * @param r
			 * @param pieces
			 */
			private void modifySidesByPlacingRectangle(RectangleArea r, RectangleSidePiece... pieces) {
				Collection<CardinalDirection> directionsUnused = Sets
					.newHashSet(CardinalDirection.values());
				for (RectangleSidePiece piece : pieces) {
					RectangleSidePiece rPiece = r
						.getSideAsSidePiece(piece.direction.opposite());
					TreeSet<RectangleSidePiece> treeSet = this.pieces
						.get(piece.direction);
					RectangleSidePiece[] splitPieces = piece
						.splitWithPiece(rPiece);

					if (splitPieces.length > 0 && splitPieces[0] == piece) {
						// TODO: Added exception only for debugging, remove this
						// part later.
						throw new IllegalStateException(
							"This shit should not happen");
					}
					if (originalPieces.contains(piece)) {
						originalPieces.remove(piece);
						for (RectangleSidePiece splitOriginalPiece : splitPieces) {
							originalPieces.add(splitOriginalPiece);
						}
					}

					for (RectangleSidePiece newPiece : splitPieces) {
						treeSet.add(newPiece);
					}
					for (RectangleSidePiece rSplitPiece : rPiece
						.splitWithPiece(piece)) {
						newPieces.add(rSplitPiece);
					}
					directionsUnused.remove(rPiece.direction);
					removePieceFromItsJunction(piece);
				}
				for (CardinalDirection dir : directionsUnused) {
					newPieces.add(r.getSideAsSidePiece(dir));
				}
			}
			/**
			 * Disassociates a piece and with a junction containing it. Each
			 * piece is a part of only one junction, so no need to provide a
			 * junction as an argument here.
			 * 
			 * @param piece
			 */
			private void removePieceFromItsJunction(RectangleSidePiece piece) {
				junctions.get(piece).pieces.remove(piece.direction);
				junctions.remove(piece);
			}
			private RectangleSidePiece getParallelRectangleablePiece(RectangleSidePiece newPiece) {
				TreeSet<RectangleSidePiece> treeSet = pieces
					.get(newPiece.direction.opposite());
				RectangleSidePiece opposite = inversePiece(newPiece);
				do {
					opposite = treeSet.higher(opposite);
				} while (opposite != null && !piecesAreParallelAndCloseEnough(
					opposite,
					newPiece));
				return opposite;
			}

			int ii = 0;

			private RectangleSidePiece[] getPerpendicularRectangleablePieces(RectangleSidePiece newPiece) {
				RectangleSidePiece[] answer = new RectangleSidePiece[2];
				// TODO: There's no need to hold RectangleArea in minPossible*
				// to compute it; need a more limited class for this purpose.
				RectangleSidePiece[] minPossibles = getMinPossiblePerpendicularPieces(newPiece);
				int index = 0;
				for (RectangleSidePiece minPiece : minPossibles) {

					TreeSet<RectangleSidePiece> treeSetOfMin = pieces
						.get(minPiece.direction);
					RectangleSidePiece nextPiece = minPiece;
					do {
						nextPiece = treeSetOfMin.lower(nextPiece);
					} while (nextPiece != null && !canRectangleBePlacedTouching(
						nextPiece,
						newPiece));
					answer[index] = nextPiece;
					index++;
				}
				if (answer[0] != null || answer[1] != null) {
					if (ii == 1) {
						canvas.draw(newPiece);
						if (answer[0] != null) {
							canvas.draw(answer[0]);
						}
						if (answer[1] != null) {
							canvas.draw(answer[1]);
						}
						// throw new Error();
					}
					ii++;
				}
				return answer;
			}
			/**
			 * Creates two virtual {@link RectangleSidePiece}s (not really
			 * belonging to any RectangleArea) which will be used as starting
			 * points to find perpendicular RectangleSidePieces in a junction
			 * with {@code piece}.
			 * 
			 * @param piece
			 * @return An array of 2 RectangleSidePieces.
			 */
			private RectangleSidePiece[] getMinPossiblePerpendicularPieces(RectangleSidePiece piece) {
				RectangleSidePiece[] answer = new RectangleSidePiece[2];
				if (piece.isVertical()) {
					Point startingPointN = piece.segment
						.getEndPoint(Directions.N);
					Point startingPointS = piece.segment
						.getEndPoint(Directions.S);
					answer[0] = new RectangleSidePiece(
						piece.r,
						Directions.S,
						piece.line.getStaticCoordFromSide(piece.direction),
						startingPointN.y,
						1);
					answer[1] = new RectangleSidePiece(
						piece.r,
						Directions.N,
						piece.line.getStaticCoordFromSide(piece.direction),
						startingPointS.y,
						1);
				} else {
					Point startingPointW = piece.segment
						.getEndPoint(Directions.W);
					Point startingPointE = piece.segment
						.getEndPoint(Directions.E);
					answer[0] = new RectangleSidePiece(
						piece.r,
						Directions.W,
						startingPointE.x,
						piece.line.getStaticCoordFromSide(piece.direction),
						1);
					answer[1] = new RectangleSidePiece(
						piece.r,
						Directions.E,
						startingPointW.x,
						piece.line.getStaticCoordFromSide(piece.direction),
						1);
				}
				return answer;
			}
			/**
			 * Returns a new RectangleSidePiece with the same segment and line,
			 * but with opposite direction.
			 * 
			 * @param piece
			 * @return
			 */
			private RectangleSidePiece inversePiece(RectangleSidePiece piece) {
				return new RectangleSidePiece(
					piece.r,
					piece.direction.opposite(),
					piece.segment.x,
					piece.segment.y,
					piece.segment.length);
			}
			private void newJunctionOf(RectangleSidePiece newPiece) {
				junctions.put(newPiece, new PiecesJunction(newPiece));

			}
			private void addPieceToJunction(RectangleSidePiece key, RectangleSidePiece newPiece) {
				if (key == null) {
					throw new NullPointerException();
				}
				if (junctions.containsKey(key)) {
					junctions.get(key).setPiece(newPiece);
				} else {
					junctions.put(key, new PiecesJunction(newPiece));
				}

			}

			/**
			 * Represents [1..4] pieces with different direction that together
			 * form space where a {@link RectangleArea} with its
			 * {@link RectangleSystem#borderWidth} can be put.
			 * 
			 * @author suseika
			 * 
			 */
			class PiecesJunction {
				private Map<CardinalDirection, RectangleSidePiece> pieces = new HashMap<CardinalDirection, RectangleSidePiece>();

				public PiecesJunction(RectangleSidePiece initialPiece) {
					setPiece(initialPiece);
				}
				public int amountOfSides() {
					return pieces.size();
				}
				@Override
				public String toString() {
					Collection<String> answer = new ArrayList<String>();
					for (Map.Entry<CardinalDirection, RectangleSidePiece> e : pieces
						.entrySet()) {
						CardinalDirection dir = e.getKey();
						RectangleSidePiece piece = e.getValue();
						answer.add(dir + ":" + piece.segment.length);
					}
					return Joiner.on("+").join(answer);
				}
				/**
				 * Places a piece in this junction from a Direction
				 * corresponding to newPiece's direction.
				 * 
				 * @param newPiece
				 */
				private void setPiece(RectangleSidePiece newPiece) {
					RectangleSidePiece oppositePiece = pieces
						.get(newPiece.direction.opposite());
					if (oppositePiece != null) {
						junctions.remove(oppositePiece);
						distanceToCurrentParallelSide.put(
							newPiece,
							newPiece.line.distanceTo(oppositePiece.line));
					}
					pieces.put(newPiece.direction, newPiece);
				}

				/**
				 * Cuts pieces in this PiecesJuntion leaving only that part of a
				 * piece that is inside a rectangle formed by pieces' junction.
				 * This operation mutates the objects stored in
				 * {@link RectangleablePiecesCollection#pieces}. If a
				 * non-virtual piece of previous front was cut, the derivative
				 * pieces are added to the collection of non-virtual pieces.
				 * 
				 * @return A set of pieces that were cut from original pieces
				 *         (original pieces themselves not included).
				 */
				private void cutParallel() {
					// TODO: This method could be written more clearly.
					Set<CardinalDirection> directionsOccupied = pieces.keySet();
					boolean hasHorizontalParallel = directionsOccupied
						.contains(Directions.N) && directionsOccupied
						.contains(Directions.S);
					boolean hasVerticalParallel = directionsOccupied
						.contains(Directions.W) && directionsOccupied
						.contains(Directions.E);
					if (hasVerticalParallel || hasHorizontalParallel) {
						Map<Direction, Integer> coords = new HashMap<Direction, Integer>();
						for (CardinalDirection dir : CardinalDirection.values()) {
							if (directionsOccupied.contains(dir)) {
								coords.put(dir, pieces
									.get(dir)
									.getLine()
									.getStaticCoordFromSide(dir.opposite()));
							}
						}
						Map<Orientation, Range> ranges = new HashMap<Orientation, Range>();
						if (hasVerticalParallel) {
							ranges.put(
								Orientation.VERTICAL,
								new Range(coords.get(Directions.W), coords
									.get(Directions.E)));
						}
						if (hasHorizontalParallel) {
							ranges.put(
								Orientation.HORIZONTAL,
								new Range(coords.get(Directions.N), coords
									.get(Directions.S)));
						}
						for (CardinalDirection dir : directionsOccupied) {
							// Cut each piece that has a parallel piece with its
							// parallel piece.
							if (!directionsOccupied.contains(dir.opposite())) {
								continue;
							}
							RectangleSidePiece piece = pieces.get(dir);
							ImmutableCollection<RectangleSidePiece> cutWithRange = piece
								.cutWithRange(ranges.get(dir.getOrientation()));
							if (originalPieces.contains(piece)) {
								originalPieces.addAll(cutWithRange);
							}
							newPieces.addAll(cutWithRange);
						}
					}
				}
			}
		}
	}

}
