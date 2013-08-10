package tendiwa.geometry;

import static tendiwa.geometry.Directions.CARDINAL_DIRECTIONS;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Range;
import tendiwa.drawing.DrawingAlgorithm;
import tendiwa.drawing.DrawingRectangle;
import tendiwa.drawing.DrawingRectangleSidePiece;
import tendiwa.drawing.TestCanvas;
import tendiwa.drawing.TestCanvasBuilder;
import tendiwa.geometry.WaveRectangleSystem.VirtualWave.RectangleablePiecesCollection.PiecesJunction;

import com.google.common.base.Joiner;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class WaveRectangleSystem extends GrowingRectangleSystem {
	/**
	 * A wave #x is a set of rectangles that were created as neighbors of
	 * rectangles of a previous wave #x-1, with a wave #1 being the initial
	 * rectangle system.
	 */
	private ArrayList<HashSet<RectangleArea>> wave = new ArrayList<HashSet<RectangleArea>>();
	private HashSet<RectangleSidePiece> freeSidePiecesOnPreviousWave = new HashSet<RectangleSidePiece>();
	private int lastFullyOccupiedWave = -1;
	private final Range possibleRectangleWidth;
	private final Range possibleRectangleWidthPlus2BorderWidth;
	private VirtualWave virtualCurrentWave;
	private TestCanvas canvas;

	public WaveRectangleSystem(int borderWidth, Range possibleRectangleWidth, int amountOfRectangles, Point startingPoint) {
		super(borderWidth);
		this.canvas = new TestCanvasBuilder()
			.setScale(3)
			.setDefaultDrawingAlgorithmForClass(
				Rectangle.class,
				DrawingRectangle.withColorLoop(
					Color.BLACK,
					Color.DARK_GRAY,
					Color.gray,
					Color.LIGHT_GRAY))
			.setDefaultDrawingAlgorithmForClass(
				RectangleSidePiece.class,
				DrawingRectangleSidePiece.withColorLoop(
					new Color(0, 170, 90),
					new Color(0, 170, 150),
					new Color(0, 170, 255)))
			.setDefaultDrawingAlgorithmForClass(
				PiecesJunction.class,
				new DrawingAlgorithm<PiecesJunction>() {
					final Iterator<Color> colors = Iterables.cycle(
						new Color[] {
							new Color(100, 0, 190),
							new Color(160, 0, 190),
							new Color(240, 0, 190)
						}).iterator();

					@Override
					public void draw(PiecesJunction shape) {
						Color color = colors.next();
						for (RectangleSidePiece piece : shape.pieces.values()) {
							drawObject(piece, DrawingRectangleSidePiece
								.withColor(color));
						}
					}
				})
			.build();
		this.possibleRectangleWidth = possibleRectangleWidth;
		possibleRectangleWidthPlus2BorderWidth = new Range(
			possibleRectangleWidth.min + borderWidth,
			possibleRectangleWidth.max + borderWidth);
		HashSet<RectangleArea> initialWave = new HashSet<RectangleArea>();
		RectangleArea initialRectangle = new RectangleArea(
			EnhancedRectangle.rectangleByCenterPoint(
				startingPoint,
				Chance.rand(possibleRectangleWidth),
				Chance.rand(possibleRectangleWidth)));
		initialWave.add(initialRectangle);
		canvas.draw(initialRectangle, DrawingRectangle.withColor(Color.BLUE));
		wave.add(initialWave);
		for (int i = 0; i < amountOfRectangles; i++) {
			addRectangleFromVirtualWave();
		}
	}
	public WaveRectangleSystem(RectangleSystem rs, Range possibleRectangleWidth) {
		super(rs.borderWidth);
		this.possibleRectangleWidth = possibleRectangleWidth;
		possibleRectangleWidthPlus2BorderWidth = new Range(
			possibleRectangleWidth.min + borderWidth,
			possibleRectangleWidth.max + borderWidth);
		HashSet<RectangleArea> initialWave = new HashSet<RectangleArea>();
		for (RectangleArea r : rs) {
			addRectangleArea(r);
			initialWave.add(r);
		}
	}
	public void addRectangleFromVirtualWave() {
		if (virtualCurrentWave == null || virtualCurrentWave.virtualRectangles
			.size() == 0) {
			buildVirtualCurrentWave();
			lastFullyOccupiedWave++;
			wave.add(new HashSet<RectangleArea>());
		}
		Iterator<RectangleArea> iterator = virtualCurrentWave.virtualRectangles
			.iterator();
		RectangleArea r = iterator.next();
		addRectangleArea(r);
		wave.get(lastFullyOccupiedWave + 1).add(r);
		iterator.remove();
	}
	public void buildWave() {
		if (virtualCurrentWave.virtualRectangles.size() != 0) {
			throw new IllegalStateException();
		}
		for (RectangleArea r : virtualCurrentWave.virtualRectangles) {
			addRectangleArea(r);
			wave.get(lastFullyOccupiedWave + 1).add(r);
			virtualCurrentWave.virtualRectangles.clear();
		}

	}
	private void buildVirtualCurrentWave() {
		findFreeSidePieces();
		virtualCurrentWave = new VirtualWave();
		if (virtualCurrentWave.virtualRectangles.size() == 0) {
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
		// TODO: Allow rectangles without intersection by dynamic coord to be
		// rectangleable
		if (a.intersectionByDynamicCoord(b) == 0) {
			// and which can contain a rectangle between them.
			return false;
		}
		return true;
	}
	private void findFreeSidePieces() {
		for (RectangleArea r : wave.get(wave.size() - 1)) {
			for (CardinalDirection side : CardinalDirection.values()) {
				freeSidePiecesOnPreviousWave.add(r.getSideAsSidePiece(side));
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

	class VirtualWave {
		private final Collection<RectangleArea> virtualRectangles = new ArrayList<RectangleArea>();
		private final RectangleablePiecesCollection pieces;
		private int curWidth = 0;
		private int curHeight = 0;
		private int curOffset = 0;

		VirtualWave() {
			pieces = new RectangleablePiecesCollection(
				freeSidePiecesOnPreviousWave);
			pieces.addNewPiecesToExistingJunctions();

			debugPlaceRec(1, Directions.N, 5, 1, 4);
			debugPlaceRec(2, Directions.E, 1, 1, 0);
			debugPlaceRec(1, Directions.S, 5, 1, 4);
			debugPlaceRec(2, Directions.E, 5, 1, 0);
			// debugPlaceRec(2, Directions.E, 5, 1, 0);

			// for (int numberOfSides = 4; numberOfSides > 0; numberOfSides--) {
			// PiecesJunction junction = pieces
			// .getOneJunctionWithOriginalPieces(numberOfSides);
			// if (junction != null) {
			// // If at least one n-sided junction found, then start over
			// // from 4-sided junctions, because adding a rectangle will
			// // probably increase the number of occupied sides in
			// // existing junctions, making an x-junction to be
			// // (x+1)-junction.
			// occupyJunctionWithRectangle(junction);
			// pieces.addNewPiecesToExistingJunctions();
			// numberOfSides = 4 + 1;
			// }
			// }

			// for (RectangleSidePiece p : pieces.getAllPieces()) {
			// canvas.draw(p, DrawingRectangleSidePiece.withColors(
			// Color.YELLOW,
			// Color.RED));
			// }
		}
		public void debugPlaceRec(int numberOfSides, CardinalDirection dir, int width, int height, int offset) {
			PiecesJunction junction = null;
			for (Map.Entry<RectangleSidePiece, PiecesJunction> entry : pieces.junctions
				.entrySet()) {
				if (!pieces.originalToActualPieces
					.containsValue(entry.getKey())) {
					continue;
				}
				PiecesJunction probableJunction = entry.getValue();
				if (probableJunction.amountOfSides() == numberOfSides && probableJunction.pieces
					.containsKey(dir)) {
					junction = probableJunction;
					break;
				}
			}
			curWidth = width;
			curHeight = height;
			curOffset = offset;
			occupyJunctionWithRectangle(junction);
			curWidth = 0;
			curHeight = 0;
			curOffset = 0;
			pieces.addNewPiecesToExistingJunctions();
		}
		private Collection<PiecesJunction> getJunctionsWithOriginal() {
			Collection<PiecesJunction> answer = Sets.newHashSet();
			for (Map.Entry<RectangleSidePiece, PiecesJunction> entry : pieces.junctions
				.entrySet()) {
				if (pieces.originalToActualPieces.containsValue(entry.getKey())) {
					answer.add(entry.getValue());
				}
			}
			return answer;
		}

		/**
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
			RectangleArea rectangle = new RectangleArea(x, y, width, height);
			pieces.modifySidesByPlacingRectangle(rectangle, a, b, c, d);
			return rectangle;
		}
		private RectangleArea getRectangleBetween3Sides(RectangleSidePiece a, RectangleSidePiece b, RectangleSidePiece c) {
			assert a != b;
			assert b != c;
			assert a != c;
			assert a != null;
			assert b != null;
			assert c != null;
			RectangleSidePiece parallelPiece1;
			RectangleSidePiece parallelPiece2;
			RectangleSidePiece perpendicularPiece;
			if (a.line.isParallel(b.line)) {
				parallelPiece1 = a;
				parallelPiece2 = b;
				perpendicularPiece = c;
			} else if (a.line.isParallel(c.line)) {
				parallelPiece1 = a;
				parallelPiece2 = c;
				perpendicularPiece = b;
			} else {
				assert b.line.isParallel(c.line);
				parallelPiece1 = b;
				parallelPiece2 = c;
				perpendicularPiece = a;
			}
			// for (RectangleSidePiece existingPiece :
			// virtualCurrentWaveSidePieces) {
			// if (piecesAreParallelAndCloseEnough(
			// notParallelPiece,
			// existingPiece)) {
			// // If one of rectangles in this wave previously placed
			// // between 4 sides makes the fourth side
			// return getRectangleBetween4Sides(a, b, c, existingPiece);
			// }
			// }
			int distanceBetweenParallel = parallelPiece1
				.distanceTo(parallelPiece2);
			assert distanceBetweenParallel > borderWidth;
			int width, height;
			if (parallelPiece1.isVertical()) {
				width = Math.min(
					distanceBetweenParallel,
					Chance.rand(possibleRectangleWidth));
				if (width == distanceBetweenParallel) {
					assert true;
				}
				height = Chance.rand(possibleRectangleWidth);
			} else {
				height = Math.min(
					distanceBetweenParallel,
					Chance.rand(possibleRectangleWidth));
				if (height == distanceBetweenParallel) {
					assert true;
				}
				width = Chance.rand(possibleRectangleWidth);
			}
			if (curWidth != 0) {
				width = curWidth;
			}
			if (curHeight != 0) {
				height = curHeight;
			}

			OrdinalDirection dirBetweenPieces = (OrdinalDirection) Directions
				.getDirectionBetween(
					parallelPiece1.direction,
					perpendicularPiece.direction);
			RectangleArea rectangle = new RectangleArea(
				EnhancedRectangle.growFromIntersection(
					parallelPiece1.line,
					perpendicularPiece.line,
					dirBetweenPieces,
					width,
					height));
			pieces.modifySidesByPlacingRectangle(rectangle, a, b, c);
			return rectangle;
		}
		public RectangleArea getRectangleBetweenParallelSides(RectangleSidePiece d, RectangleSidePiece e) {
			throw new NotImplementedException();
		}
		/**
		 * 
		 * @param piece
		 *            Actual piece
		 * @return
		 */
		public RectangleArea getRectangleOn1Side(RectangleSidePiece piece) {
			assert piece != null;
			int width = Chance.rand(possibleRectangleWidth);
			int height = Chance.rand(possibleRectangleWidth);
			RectangleSidePiece originalPiece = pieces.originalToActualPieces
				.inverse()
				.get(piece);
			int offset = Chance.rand(new Range(
				-(originalPiece.direction.isVertical() ? width : height) + 1,
				originalPiece.segment.length - 1));
			// int offset = 2;
			if (curWidth != 0) {
				width = curWidth;
			}
			if (curHeight != 0) {
				height = curHeight;
			}
			if (curOffset != 0) {
				offset = curOffset;
			}
			RectangleArea r = create(
				originalPiece.createRectangle(1),
				piece.direction,
				width,
				height,
				offset);
			pieces.modifySidesByPlacingRectangle(r, piece);
			return r;
		}
		public RectangleArea getRectangleBetween2Sides(RectangleSidePiece piece1, RectangleSidePiece piece2) {
			OrdinalDirection direction = Directions.getDirectionBetween(
				piece1.direction,
				piece2.direction);
			RectangleSidePiece horizontal, vertical;
			if (piece1.isVertical()) {
				vertical = piece1;
				horizontal = piece2;
			} else {
				vertical = piece2;
				horizontal = piece1;
			}
			int minWidth = horizontal.getSegment().getEndPoint(
				vertical.direction.opposite()).x - vertical
				.getSegment()
				.getEndPoint(horizontal.direction.opposite()).x;
			int minHeight = vertical.getSegment().getEndPoint(
				horizontal.direction.opposite()).y - horizontal
				.getSegment()
				.getEndPoint(vertical.direction.opposite()).y;
			int width = Chance.rand(new Range(Math.max(
				minWidth,
				possibleRectangleWidth.min), possibleRectangleWidth.max));
			int height = Chance.rand(new Range(Math.max(
				minHeight,
				possibleRectangleWidth.min), possibleRectangleWidth.max));
			if (curWidth != 0) {
				width = curWidth;
			}
			if (curHeight != 0) {
				height = curHeight;
			}
			RectangleArea rectangle = new RectangleArea(
				EnhancedRectangle.growFromIntersection(
					piece1.getLine(),
					piece2.getLine(),
					direction,
					width,
					height));
			pieces.modifySidesByPlacingRectangle(rectangle, piece1, piece2);
			return rectangle;
		}
		/**
		 * Places on this VirtualWave all the rectangles that can be built in it
		 * touching 4 RectangleSidePieces on previous wave. Also if any sides
		 * forming {@code junction} are original sides, remove them from the
		 * collection of original sides, as they are now occupied.
		 * 
		 * @return A map where keys are RectangleAreas newly created during
		 *         current call to this method, and values are collections of
		 *         new free pieces these rectangles add to VirtualWave.
		 */
		private void occupyJunctionWithRectangle(PiecesJunction junction) {
			assert junction != null;
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
					Iterator<RectangleSidePiece> iter = values.iterator();
					RectangleSidePiece a = iter.next();
					RectangleSidePiece b = iter.next();
					RectangleSidePiece c = iter.next();
					r = getRectangleBetween3Sides(a, b, c);
					break;
				case 2:
					Collection<RectangleSidePiece> values2 = junction.pieces
						.values();
					Iterator<RectangleSidePiece> iterator = values2.iterator();
					RectangleSidePiece d = iterator.next();
					RectangleSidePiece e = iterator.next();
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

		/**
		 * Adds a new {@link RectangleArea} to this {@link VirtualWave}. Adding
		 * RectangleAreas occurs after placing its {@link RectangleSidePiece}s
		 * into {@link RectangleablePiecesCollection}.
		 * 
		 * @param r
		 */
		private void addVirtualRectangle(RectangleArea r) {
			virtualRectangles.add(r);
			canvas.draw(r);
		}

		class RectangleablePiecesCollection {
			private Map<CardinalDirection, TreeSet<RectangleSidePiece>> pieces = new HashMap<CardinalDirection, TreeSet<RectangleSidePiece>>();
			private Map<RectangleSidePiece, PiecesJunction> junctions = new HashMap<RectangleSidePiece, PiecesJunction>();
			/** Pieces that form previous front. */
			private final Collection<RectangleSidePiece> originalPieces = new HashSet<RectangleSidePiece>();
			/**
			 * Maps original pieces to the actual pieces that might be joined
			 * from several pieces including original ones. When
			 * {@link RectangleablePiecesCollection} object is just constructed,
			 * this map maps original pieces to themselves.
			 */
			private final BiMap<RectangleSidePiece, RectangleSidePiece> originalToActualPieces = HashBiMap
				.create();
			private final Collection<RectangleSidePiece> newPieces = new ArrayList<RectangleSidePiece>();
			/**
			 * Compares which one of two same-direction pieces lies further in
			 * direction tey are facing.
			 */
			private final Comparator<RectangleSidePiece> PIECES_COMPARATOR = new Comparator<RectangleSidePiece>() {
				@Override
				public int compare(RectangleSidePiece piece1, RectangleSidePiece piece2) {
					assert piece1.direction == piece2.direction;
					if (piece1.direction == Directions.N) {
						int dy = piece2.segment.y - piece1.segment.y;
						if (dy == 0) {
							return piece2.segment.x - piece1.segment.x;
						} else {
							return dy;
						}
					} else if (piece1.direction == Directions.E) {
						int dx = piece1.segment.x - piece2.segment.x;
						if (dx == 0) {
							return piece1.segment.y - piece2.segment.y;
						} else {
							return dx;
						}
					} else if (piece1.direction == Directions.S) {
						int dy = piece1.segment.y - piece2.segment.y;
						if (dy == 0) {
							return piece1.segment.x - piece2.segment.x;
						} else {
							return dy;
						}
					} else {
						assert piece1.direction == Directions.W;
						int dx = piece2.segment.x - piece1.segment.x;
						if (dx == 0) {
							return piece2.segment.y - piece1.segment.y;
						} else {
							return dx;
						}
					}
				}
			};

			/**
			 * @param originalPieces
			 *            Pieces that form the previous front.
			 */
			private RectangleablePiecesCollection(Collection<RectangleSidePiece> originalPieces) {
				this.originalPieces.addAll(originalPieces);
				for (RectangleSidePiece piece : originalPieces) {
					originalToActualPieces.put(piece, piece);
				}
				newPieces.addAll(originalPieces);
				for (CardinalDirection direction : CardinalDirection.values()) {
					pieces.put(direction, new TreeSet<RectangleSidePiece>(
						PIECES_COMPARATOR));
				}
			}
			private Collection<RectangleSidePiece> getAllPieces() {
				Collection<RectangleSidePiece> answer = Lists.newArrayList();
				for (CardinalDirection dir : CARDINAL_DIRECTIONS) {
					answer.addAll(pieces.get(dir));
				}
				return answer;
			}
			/**
			 * Transforms two pieces lying on the same line into one piece,
			 * which connects two furthermost points of those pieces. Algorithm
			 * will only attempt to join pieces that lie on the same line. This
			 * method doesn't add the resulting piece to a
			 * {@link RectangleablePiecesCollection}.
			 * 
			 * @param piece1
			 * @param piece2
			 * @return a new RectangleSidePiece.
			 */
			private RectangleSidePiece joinPieces(RectangleSidePiece piece1, RectangleSidePiece piece2) {
				assert piece1.direction == piece2.direction;
				assert piece1 != piece2;
				RectangleSidePiece lesserPiece;
				RectangleSidePiece furthermostPiece;
				// Find out which one of the two segments is closer to the start
				// of coordinate system.
				if (piece1.segment.getStartCoord() < piece2.segment
					.getStartCoord()) {
					lesserPiece = piece1;
					furthermostPiece = piece2;
				} else {
					lesserPiece = piece2;
					furthermostPiece = piece1;
				}
				CardinalDirection lesserEndOfPieces;
				if (lesserPiece.direction.isVertical()) {
					lesserEndOfPieces = Directions.W;
				} else {
					lesserEndOfPieces = Directions.N;
				}
				Point startPoint = lesserPiece.getSegment().getEndPoint(
					lesserEndOfPieces);
				Point endPoint = furthermostPiece.getSegment().getEndPoint(
					lesserEndOfPieces.opposite());
				assert startPoint.x == endPoint.x || startPoint.y == endPoint.y;
				int length;
				if (startPoint.x == endPoint.x) {
					length = endPoint.y - startPoint.y + 1;
				} else {
					length = endPoint.x - startPoint.x + 1;
				}
				RectangleSidePiece answer = new RectangleSidePiece(
					piece1.direction,
					startPoint.x,
					startPoint.y,
					length);
				return answer;
			}
			/**
			 * If some of {@code pieces} are in
			 * {@link VirtualWave#originalPieces}, remove them from that
			 * collection.
			 * 
			 * @param pieces
			 */
			private void removePiecesIfOriginal(RectangleSidePiece... pieces) {
				for (RectangleSidePiece piece : pieces) {
					if (originalPieces.contains(piece)) {
						forgetOriginalPiece(piece);
					}
				}
			}
			/**
			 * Returns one of {@link PiecesJunction}s that:
			 * <ol>
			 * <li>Has exactly {@code numberOdSides} pieces in it;</li>
			 * <li>One of those pieces is an original piece (
			 * {@link VirtualWave#originalPieces}).
			 * </ol>
			 * 
			 * @param numberOfSides
			 * @return That piece or null if it wasn't found.
			 */
			public PiecesJunction getOneJunctionWithOriginalPieces(int numberOfSides) {
				for (Map.Entry<RectangleSidePiece, PiecesJunction> entry : junctions
					.entrySet()) {
					if (!originalToActualPieces.containsValue(entry.getKey())) {
						continue;
					}
					PiecesJunction junction = entry.getValue();
					if (junction.amountOfSides() == numberOfSides) {
						return junction;
					}
				}
				return null;
			}
			private PiecesJunction getJunction(int numberOfSides, CardinalDirection dir, int order) {
				for (PiecesJunction junction : junctions.values()) {
					if (junction.pieces.containsKey(dir) && junction
						.amountOfSides() == numberOfSides) {
						order--;
						if (order == 0) {
							return junction;
						}
					}
				}
				return null;
			}
			private RectangleSidePiece[] getPieces(CardinalDirection dir) {
				return pieces.get(dir).toArray(
					new RectangleSidePiece[pieces.get(dir).size()]);
			}
			private void drawJunctionsOf(int numberOfSides) {
				for (Map.Entry<RectangleSidePiece, PiecesJunction> entry : junctions
					.entrySet()) {
					if (!originalToActualPieces.containsValue(entry.getKey())) {
						continue;
					}
					PiecesJunction junction = entry.getValue();
					if (junction.amountOfSides() == numberOfSides) {
						canvas.draw(junction);
					}
				}
			}
			private void addNewPiecesToExistingJunctions() {
				for (RectangleSidePiece newPiece : newPieces) {
					pieces.get(newPiece.direction).add(newPiece);
				}
				// Set parallel pieces
				for (RectangleSidePiece newPiece : newPieces) {
					RectangleSidePiece parallelPiece = getParallelRectangleablePiece(newPiece);
					if (parallelPiece != null) {
						addPieceToJunction(parallelPiece, newPiece);
					}
				}
				// Set perpendicular pieces
				for (RectangleSidePiece newPiece : newPieces) {
					PerpendicularPiecesPair perpendicularPieces = getPerpendicularRectangleablePieces(newPiece);
					for (RectangleSidePiece perpendicularPiece : perpendicularPieces) {
						PiecesJunction junction = junctions
							.get(perpendicularPiece);
//						if (!junction.pieces.containsKey(newPiece.direction)) {
//							addPieceToJunction(perpendicularPiece, newPiece);
//						}
						if (!arePiecesInSameJunction(newPiece, perpendicularPiece)) {
							joinPiecesToExistingJunction(newPiece, perpendicularPiece);
						}
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
				// for (PiecesJunction junction : junctions.values()) {
				// junction.cutParallel();
				// }
				// if (newPieces.size() > 0) {
				// buildJunctions();
				// }
			}
			private boolean arePiecesInSameJunction(RectangleSidePiece piece1, RectangleSidePiece piece2) {
				if (!junctions.containsKey(piece1)) {
					return false;
				}
				if (!junctions.containsKey(piece2)) {
					return false;
				}
				if (junctions.get(piece1).pieces.containsValue(piece2)) {
					return true;
				}
				return false;
			}
			/**
			 * Finds which one of 2 pieces is already in junction, and sets
			 * another to the same junction.
			 * 
			 * @param piece1
			 * @param piece2
			 */
			private void joinPiecesToExistingJunction(RectangleSidePiece piece1, RectangleSidePiece piece2) {
				if (junctions.containsKey(piece1)) {
					assert !junctions.containsKey(piece2);
					addPieceToJunction(piece1, piece2);
				} else {
					assert junctions.containsKey(piece2);
					assert !junctions.containsKey(piece1);
					addPieceToJunction(piece2, piece1);
				}
			}
			private void draw(Object shape) {
				canvas.draw(shape);
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
				ArrayList<RectangleSidePiece> oldPieces = Lists.newArrayList();
				ArrayList<RectangleSidePiece> rSplitterPieces = Lists
					.newArrayList();
				ArrayList<RectangleSidePiece[]> splitPiecesPairs = Lists
					.newArrayList();
				// Place pieces for those sides that have neighbor pieces from
				// previous front.
				for (RectangleSidePiece piece : pieces) {
					RectangleSidePiece rPiece = r
						.getSideAsSidePiece(piece.direction.opposite());
					RectangleSidePiece[] splitPieces = piece
						.splitWithPiece(rPiece);

					assert splitPieces.length == 0 || splitPieces[0] != piece;
					if (touchesOriginalPiece(rPiece)) {
						// Modify original pieces, splitting them under rPiece.
						RectangleSidePiece originalPiece = originalToActualPieces
							.inverse()
							.get(piece);
						forgetOriginalPiece(originalPiece);
						for (RectangleSidePiece splitOriginalPiece : originalPiece
							.splitWithPiece(rPiece)) {
							originalPieces.add(splitOriginalPiece);
							for (RectangleSidePiece splitActualPiece : splitPieces) {
								if (splitActualPiece
									.contains(splitOriginalPiece)) {
									originalToActualPieces.put(
										splitOriginalPiece,
										splitActualPiece);
								}
							}
						}
					}

					removePieceFromItsJunction(piece);
					oldPieces.add(piece);
					rSplitterPieces.add(rPiece);
					splitPiecesPairs.add(splitPieces);
					directionsUnused.remove(rPiece.direction);
				}
				assert oldPieces.size() == rSplitterPieces.size();
				assert splitPiecesPairs.size() == oldPieces.size();
				// This is a separate loop because all of argument pieces must
				// be split by the rectangle before rebuilding split junctions
				for (int i = 0, l = oldPieces.size(); i < l; i++) {
					RectangleSidePiece piece = oldPieces.get(i);
					RectangleSidePiece rPiece = rSplitterPieces.get(i);
					RectangleSidePiece[] splitPieces = splitPiecesPairs.get(i);
					TreeSet<RectangleSidePiece> treeSet = this.pieces
						.get(piece.direction);

					replaceOldJunctionWithSplitOnes(piece, rPiece, splitPieces);
					for (RectangleSidePiece newPiece : splitPieces) {
						treeSet.add(newPiece);
					}
					for (RectangleSidePiece rSplitPiece : rPiece
						.splitWithPiece(piece)) {
						addNewSidePiece(rSplitPiece);
					}
				}

				// Place pieces from other sides of the new rectangle
				for (CardinalDirection dir : directionsUnused) {
					RectangleSidePiece piece = addNewSidePiece(r
						.getSideAsSidePiece(dir));
					this.pieces.get(piece.direction).add(piece);
				}
			}
			private void replaceOldJunctionWithSplitOnes(RectangleSidePiece oldPiece, RectangleSidePiece splitterPiece, RectangleSidePiece[] splitPiecesArray) {
				if (splitPiecesArray.length == 0) {
					return;
				}
				SameLinePiecesPair splitPieces = new SameLinePiecesPair(
					splitterPiece,
					splitPiecesArray);
				PerpendicularPiecesPair perpendicularPieces = getPerpendicularRectangleablePieces(oldPiece);
				for (RectangleSidePiece splitPiece : splitPieces) {
					RectangleSidePiece parallelPiece = getParallelRectangleablePiece(splitPiece);
					RectangleSidePiece perpendicularOfSameGreatness = perpendicularPieces
						.getPieceOfSameGreatness(splitPiece, splitPieces);
					if (perpendicularOfSameGreatness != null) {
						// If there is a parallel piece, it is supposed to be
						// already in junction with the perpendicular one.
						assert parallelPiece == null || rectangleableWithOneOf(
							parallelPiece,
							perpendicularPieces);
						// addPieceToJunction(
						// splitPiece,
						// perpendicularOfSameGreatness);
					} else if (parallelPiece != null) {
						// addPieceToJunction(splitPiece, parallelPiece);
					} else {
						newJunctionOf(splitPiece);
					}
				}
			}
			/**
			 * Solely for assertion.
			 * 
			 * @param testingPiece
			 * @param candidates
			 * @return
			 */
			private boolean rectangleableWithOneOf(RectangleSidePiece testingPiece, PerpendicularPiecesPair candidates) {
				for (RectangleSidePiece perpendicularPiece : candidates) {
					if (canRectangleBePlacedTouching(
						testingPiece,
						perpendicularPiece)) {
						return true;
					}
				}
				return false;
			}
			/**
			 * Checks if a piece touches any of the original pieces.
			 * 
			 * @param piece
			 * @return
			 */
			private boolean touchesOriginalPiece(RectangleSidePiece piece) {
				for (RectangleSidePiece original : originalPieces) {
					if (piece.touches(original)) {
						return true;
					}
				}
				return false;
			}
			/**
			 * Adds a RectangleSidePiece to
			 * {@link RectangleablePiecesCollection}, joining this piece and
			 * some existing pieces into one if there are some to join.
			 * 
			 * @param newPiece
			 */
			private RectangleSidePiece addNewSidePiece(RectangleSidePiece newPiece) {
				assert newPiece != null;
				RectangleSidePiece[] piecesToTest = {
					pieces.get(newPiece.direction).higher(newPiece),
					pieces.get(newPiece.direction).lower(newPiece)
				};
				for (RectangleSidePiece existingPiece : piecesToTest) {
					if (existingPiece != null && arePiecesJoinable(
						newPiece,
						existingPiece)) {
						newPiece = joinPieces(newPiece, existingPiece);
						if (originalToActualPieces.containsValue(existingPiece)) {
							setNewJoinedPieceForOriginal(
								newPiece,
								existingPiece);
						}
						removePieceFromItsJunction(existingPiece);
					}
				}
				newPieces.add(newPiece);
				return newPiece;
			}
			/**
			 * Maps an original piece to one of pieces.
			 * 
			 * @param newPiece
			 *            Piece that was made of several pieces.
			 * @param oldPiece
			 *            Piece that is currently mapped to some original piece.
			 *            It will be unmapped from that original piece, and
			 *            {@code newPiece} will be mapped to the original.
			 */
			private void setNewJoinedPieceForOriginal(RectangleSidePiece newPiece, RectangleSidePiece oldPiece) {
				assert newPiece != null;
				assert oldPiece != null;
				assert oldPiece != newPiece;
				assert newPiece.direction == oldPiece.direction;
				assert originalToActualPieces.containsValue(oldPiece);

				originalToActualPieces.put(originalToActualPieces
					.inverse()
					.get(oldPiece), newPiece);
			}
			private void drawOriginal() {
				for (RectangleSidePiece p : originalPieces) {
					canvas.draw(p);
				}
			}
			private void drawAll() {
				for (RectangleSidePiece p : getAllPieces()) {
					canvas.draw(
						p,
						DrawingRectangleSidePiece.withColor(Color.YELLOW));
				}
			}
			/**
			 * Remove a piece from the list of original ones. This is necessary
			 * when a new rectangle completely overlays an original piece.
			 * 
			 * @param original
			 */
			private void forgetOriginalPiece(RectangleSidePiece original) {
				assert original != null;
				assert originalPieces.contains(original);
				assert originalToActualPieces.containsKey(original);
				originalPieces.remove(original);
				originalToActualPieces.remove(original);
			}
			/**
			 * <p>
			 * Checks if two pieces can be joined into one. They can, if they:
			 * <ol>
			 * <li>Face the same direction</li>
			 * <li>Lie on equal lines</li>
			 * <li>There is amount of cells between their closest ends equal to
			 * {@link RectangleSystem#borderWidth}.
			 * </ol>
			 * </p>
			 * 
			 * <p>
			 * If this method returns true, then these pieces can be passed to
			 * {@link RectangleablePiecesCollection#joinPieces(RectangleSidePiece, RectangleSidePiece)}
			 * </p>
			 * 
			 * @param piece1
			 *            A piece.
			 * @param piece2
			 *            Another piece.
			 * @return true if pieces are joinable, false if they aren't.
			 */
			private boolean arePiecesJoinable(RectangleSidePiece piece1, RectangleSidePiece piece2) {
				assert piece1 != null && piece2 != null;
				assert piece1 != piece2;
				boolean sameDirection = piece1.direction == piece2.direction;
				boolean sameLine = piece1.getLine().equals(piece2.getLine());

				int lesserStartCoord, greaterStartCoord, lesserStartLength;
				if (piece1.segment.getStartCoord() < piece2.segment
					.getStartCoord()) {
					lesserStartCoord = piece1.segment.getStartCoord();
					greaterStartCoord = piece2.segment.getStartCoord();
					lesserStartLength = piece1.segment.length;
				} else {
					lesserStartCoord = piece2.segment.getStartCoord();
					greaterStartCoord = piece1.segment.getStartCoord();
					lesserStartLength = piece2.segment.length;
				}
				int distance = greaterStartCoord - lesserStartCoord - lesserStartLength;
				boolean answer = sameDirection && sameLine && distance == borderWidth;
				return answer;
			}
			private RectangleSidePiece getParallelRectangleablePiece(RectangleSidePiece newPiece) {
				TreeSet<RectangleSidePiece> treeSet = pieces
					.get(newPiece.direction.opposite());
				RectangleSidePiece opposite = inversePiece(newPiece);
				do {
					opposite = treeSet.lower(opposite);
				} while (opposite != null && !piecesAreParallelAndCloseEnough(
					opposite,
					newPiece));
				return opposite;
			}
			/**
			 * Disassociates a piece with a junction containing it. Each piece
			 * is a part of only one junction, so no need to provide a junction
			 * as an argument here.
			 * 
			 * @param piece
			 *            A piece to remove.
			 */
			private void removePieceFromItsJunction(RectangleSidePiece piece) {
				assert piece != null;
				assert junctions.containsKey(piece);
				junctions.get(piece).pieces.remove(piece.direction);
				junctions.remove(piece);
				pieces.get(piece.direction).remove(piece);
			}
			private PerpendicularPiecesPair getPerpendicularRectangleablePieces(RectangleSidePiece newPiece) {
				assert newPiece != null;
				PerpendicularPiecesPair answer = new PerpendicularPiecesPair();
				// TODO: There's no need to hold RectangleArea in minPossible*
				// to compute it; need a more limited class for this purpose.
				RectangleSidePiece[] minPossibles = getMinPossiblePerpendicularPieces(newPiece);
				for (RectangleSidePiece minPiece : minPossibles) {
					TreeSet<RectangleSidePiece> treeSetOfMin = pieces
						.get(minPiece.direction);
					RectangleSidePiece nextPiece = minPiece;
					do {
						nextPiece = treeSetOfMin.lower(nextPiece);
					} while (nextPiece != null && !canRectangleBePlacedTouching(
						nextPiece,
						newPiece));
					if (nextPiece != null) {
						answer.add(nextPiece);
					}
				}
				return answer;
			}

			class PerpendicularPiecesPair implements
				Iterable<RectangleSidePiece> {
				protected RectangleSidePiece lesser;
				protected RectangleSidePiece greater;

				protected void add(RectangleSidePiece piece) {
					if (piece.direction.isGrowing()) {
						assert lesser == null;
						lesser = piece;
					} else {
						assert greater == null;
						greater = piece;
					}
				}
				boolean isEmpty() {
					return lesser == null && greater == null;
				}
				boolean hasLesser() {
					return lesser != null;
				}
				boolean hasGreater() {
					return greater != null;
				}
				/**
				 * Returns lesser piece of this pair if
				 * {@code pieceFromAnotherPair} is lesser in {@code anotherPair}
				 * . Returns greater piece of this pair if
				 * {@code pieceFromAnotherPair} is greater in
				 * {@code anotherPair}. Returns null if this pair doesn't have a
				 * piece of the same greatness.
				 * 
				 * @param pieceFromAnotherPair
				 * @param anotherPair
				 * @return
				 */
				RectangleSidePiece getPieceOfSameGreatness(RectangleSidePiece pieceFromAnotherPair, PerpendicularPiecesPair anotherPair) {
					if (anotherPair.lesser == pieceFromAnotherPair) {
						return lesser;
					}
					if (anotherPair.greater == pieceFromAnotherPair) {
						return greater;
					}
					return null;
				}
				@Override
				public Iterator<RectangleSidePiece> iterator() {
					return new Iterator<RectangleSidePiece>() {
						private int iteratorState = 0;

						@Override
						public boolean hasNext() {
							return iteratorState == 0 && (lesser != null || greater != null) || iteratorState == 1 && greater != null;
						}
						@Override
						public RectangleSidePiece next() {
							if (iteratorState == 0) {
								if (lesser != null) {
									iteratorState++;
									return lesser;
								}
								if (greater != null) {
									iteratorState++;
									return greater;
								}
								assert false;
							} else if (iteratorState == 1) {
								if (greater != null) {
									iteratorState++;
									return greater;
								}
								assert false;
							}
							assert false;
							throw new IllegalStateException();
						}
						@Override
						public void remove() {
							throw new NotImplementedException();
						}
					};
				}
			}

			class SameLinePiecesPair extends PerpendicularPiecesPair {
				public SameLinePiecesPair(RectangleSidePiece splitterPiece, RectangleSidePiece[] pieces) {
					for (RectangleSidePiece splitPiece : pieces) {
						if (splitPiece.segment.getStartCoord() < splitterPiece.segment
							.getStartCoord()) {
							lesser = splitPiece;
						} else if (splitPiece.segment.getStartCoord() > splitterPiece.segment
							.getStartCoord()) {
							greater = splitPiece;
						} else {
							assert false;
						}
					}
				}

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
						Directions.S,
						piece.line.getStaticCoordFromSide(piece.direction),
						startingPointN.y,
						1);
					answer[1] = new RectangleSidePiece(
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
						Directions.W,
						startingPointE.x,
						piece.line.getStaticCoordFromSide(piece.direction),
						1);
					answer[1] = new RectangleSidePiece(
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
					piece.direction.opposite(),
					piece.segment.x,
					piece.segment.y,
					piece.segment.length);
			}
			private PiecesJunction newJunctionOf(RectangleSidePiece newPiece) {
				PiecesJunction newJunction = new PiecesJunction(newPiece);
				junctions.put(newPiece, newJunction);
				return newJunction;
			}
			private void addPieceToJunction(RectangleSidePiece pieceInJunction, RectangleSidePiece newPiece) {
				assert pieceInJunction != null;
				assert newPiece != null;
				assert junctions.containsKey(pieceInJunction);
				if (junctions.containsKey(pieceInJunction)) {
					PiecesJunction junction = junctions.get(pieceInJunction);
					junction.setPiece(newPiece);
					junctions.put(newPiece, junction);
				} else {
					// PiecesJunction newJunction = new PiecesJunction(
					// newPiece,
					// pieceInJunction);
					// junctions.put(pieceInJunction, newJunction);
					// junctions.put(newPiece, newJunction);
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

				public PiecesJunction(RectangleSidePiece... initialPieces) {
					for (RectangleSidePiece piece : initialPieces) {
						setPiece(piece);
					}
				}
				public void removePiece(RectangleSidePiece oldPiece) {
					assert pieces.containsValue(oldPiece);
					assert pieces.containsKey(oldPiece.direction);
					pieces.remove(oldPiece.direction);
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
					assert !pieces.containsKey(newPiece.direction);
					pieces.put(newPiece.direction, newPiece);
				}

				/**
				 * Cuts pieces in this PiecesJuntion leaving only that part of a
				 * piece that is inside a rectangle formed by pieces' junction.
				 * This operation mutates the objects stored in
				 * {@link RectangleablePiecesCollection#pieces}.
				 * 
				 * @return A set of pieces that were cut from original pieces
				 *         (original pieces themselves not included).
				 */
				private void cutParallel() {
					Set<CardinalDirection> directionsOccupied = pieces.keySet();
					boolean hasHorizontalParallel = directionsOccupied
						.contains(Directions.N) && directionsOccupied
						.contains(Directions.S);
					boolean hasVerticalParallel = directionsOccupied
						.contains(Directions.W) && directionsOccupied
						.contains(Directions.E);
					if (hasVerticalParallel || hasHorizontalParallel) {
						Map<Direction, Integer> coords = new HashMap<Direction, Integer>();
						for (CardinalDirection dir : directionsOccupied) {
							coords.put(dir, pieces
								.get(dir)
								.getLine()
								.getStaticCoordFromSide(dir.opposite()));
						}
						Map<Orientation, Range> ranges = Maps.newHashMap();
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
								.cutWithRange(ranges.get(dir
									.getOrientation()
									.reverted()));
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
