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
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

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
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
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
	public TestCanvas canvas;
	private DrawingAlgorithm<Rectangle> RED = DrawingRectangle
		.withColor(Color.RED);
	private DrawingAlgorithm<Rectangle> GREEN = DrawingRectangle
		.withColor(Color.GREEN);
	private final StringBuilder log = new StringBuilder();

	public WaveRectangleSystem(int borderWidth, Range possibleRectangleWidth, int amountOfRectangles, RectangleSystem initialRecSys) {
		super(borderWidth);
		if (possibleRectangleWidth.min <= 0) {
			throw new IllegalArgumentException(
				"Range must contain only values > 0");
		}
		this.canvas = new TestCanvasBuilder()
			.setScale(3)
			.setSize(200, 200)
			.setVisiblilty(false)
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
							drawObject(
								piece,
								DrawingRectangleSidePiece.withColor(color));
						}
					}
				})
			.build();
		this.possibleRectangleWidth = possibleRectangleWidth;
		possibleRectangleWidthPlus2BorderWidth = new Range(
			possibleRectangleWidth.min + borderWidth,
			possibleRectangleWidth.max + borderWidth);
		HashSet<RectangleArea> initialWave = new HashSet<RectangleArea>();
		for (RectangleArea r : initialRecSys) {
			initialWave.add(r);
			canvas.draw(r, DrawingRectangle.withColor(Color.BLUE));
		}
		wave.add(initialWave);
		for (int i = 0; i < amountOfRectangles; i++) {
			addRectangleFromVirtualWave();
		}
	}
	private void log(Object... messages) {
		StringBuilder builder = new StringBuilder();
		for (Object message : messages) {
			builder.append(message.toString() + " ");
		}
		log.append(builder.toString() + "\n");
	}
	private String showLog() {
		return log.toString();
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
	private boolean piecesArePerpendicularAndCloseEnough(RectangleSidePiece piece1, RectangleSidePiece piece2) {
		assert piece1 != null;
		assert piece2 != null;
		// if (piece1.segment.asRange().contains(
		// piece2.line.getStaticCoordFromSide(piece2.direction.opposite()))) {
		// return false;
		// }
		// if (piece2.segment.asRange().contains(
		// piece1.line.getStaticCoordFromSide(piece1.direction.opposite()))) {
		// return false;
		// }
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
		private final ArrayList<RectangleArea> virtualRectangles = new ArrayList<RectangleArea>();
		private final RectangleablePiecesCollection pieces;
		private int curWidth = 0;
		private int curHeight = 0;
		private int curOffset = GENERATED_OFFSET;
		private static final int GENERATED_OFFSET = Integer.MIN_VALUE;

		VirtualWave() {
			pieces = new RectangleablePiecesCollection(
				freeSidePiecesOnPreviousWave);
			pieces.buildInitialJunctions();
			pieces.buildJunctions();
			// pieces.buildParallelJunctions();
			// debugPlaceRec(2, Directions.E, 8, 4, 4, 8);
			// debugPlaceRec(2, Directions.W, 3, 3, 2, 0);
			// debugPlaceRec(2, Directions.S, 3, 6, 1, 0);
			// debugPlaceRec(2, Directions.S, 2, 5, 4, 0);
			// debugPlaceRec(1, Directions.S, 5, 5, 2, 0);
			// debugPlaceRec(2, Directions.W, 6, 1, 3, 0);
			// debugPlaceRec(2, Directions.S, 1, 2, 2, 0);

			wh:
			while (true) {
				for (JunctionType type : JunctionType.ORDERED) {
					PiecesJunction junction = pieces
						.getOneJunctionWithOriginalPieces(type);
					if (junction != null) {
						// If at least one n-sided junction found, then start
						// over
						// from 4-sided junctions, because adding a rectangle
						// will
						// probably increase the number of occupied sides in
						// existing junctions, making an x-junction to be
						// (x+1)-junction.
						occupyJunctionWithRectangle(junction, type);
						pieces.buildJunctions();
						continue wh;
					}
				}
				break;
			}
			assert pieces.originalPieces.size() == 0;
		}
		private void drawNewRectangles() {
			for (RectangleArea r : virtualRectangles) {
				canvas.draw(r);
			}
		}
		private void debugPlaceRec(int numberOfSides, CardinalDirection direction, int pieceLength, int width, int height, int offset) {
			PiecesJunction[] junctions = getJunctions(direction, numberOfSides);
			PiecesJunction junction = null;
			for (PiecesJunction j : junctions) {
				if (j.pieces.get(direction).segment.length == pieceLength) {
					junction = j;
				}
			}
			assert junction != null : "No segment from " + direction + " with length " + pieceLength;
			curWidth = width;
			curHeight = height;
			curOffset = offset;
			occupyJunctionWithRectangle(junction, junction.getType());
			curWidth = 0;
			curHeight = 0;
			curOffset = GENERATED_OFFSET;
			pieces.buildJunctions();
		}
		private PiecesJunction[] getJunctions(CardinalDirection direction, int numberOfSides) {
			Collection<PiecesJunction> answer = new ArrayList<PiecesJunction>();
			for (Map.Entry<RectangleSidePiece, PiecesJunction> entry : pieces.junctions
				.entrySet()) {
				PiecesJunction probableJunction = entry.getValue();
				if (probableJunction.amountOfSides() == numberOfSides && probableJunction.pieces
					.containsKey(direction)) {
					answer.add(probableJunction);
				}
			}
			return answer.toArray(new PiecesJunction[answer.size()]);
		}
		private PiecesJunction getJunction(int numberOfSides, CardinalDirection dir) {
			for (Map.Entry<RectangleSidePiece, PiecesJunction> entry : pieces.junctions
				.entrySet()) {
				// if (!pieces.originalToActualPieces
				// .containsValue(entry.getKey())) {
				// continue;
				// }
				PiecesJunction probableJunction = entry.getValue();
				if (probableJunction.amountOfSides() == numberOfSides && probableJunction.pieces
					.containsKey(dir)) {
					return probableJunction;
				}
			}
			throw new RuntimeException("Junction for debug not found");
		}

		/**
		 * 
		 * @param a
		 * @param b
		 * @param c
		 * @param d
		 * @return
		 */
		private RectangleArea getRectangleBetween4Sides(RectangleSidePiece n, RectangleSidePiece e, RectangleSidePiece s, RectangleSidePiece w) {
			assert n != null && n.direction == Directions.N;
			assert e != null && e.direction == Directions.E;
			assert s != null && s.direction == Directions.S;
			assert w != null && w.direction == Directions.W;
			int x = e.line.getStaticCoordFromSide(Directions.E) + borderWidth;
			int y = s.line.getStaticCoordFromSide(Directions.S) + borderWidth;
			int width = w.line.distanceTo(e.line) - borderWidth * 2;
			// TODO: What is not implemented?
			if (width <= 0) {
				throw new NotImplementedException();
			}
			int height = s.line.distanceTo(n.line) - borderWidth * 2;
			if (height <= 0) {
				throw new NotImplementedException();
			}
			RectangleArea rectangle = new RectangleArea(x, y, width, height);
			pieces.modifySidesByPlacingRectangle(rectangle, n, e, s, w);
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
			int minWidthOrHeight = Math.max(
				distanceFromPerpendicularToClosestEndOf(
					perpendicularPiece,
					parallelPiece1),
				distanceFromPerpendicularToClosestEndOf(
					perpendicularPiece,
					parallelPiece2)) + 1;
			assert minWidthOrHeight <= possibleRectangleWidth.max;
			if (parallelPiece1.isVertical()) {
				width = distanceBetweenParallel;
				height = Chance.rand(
					Math.max(possibleRectangleWidth.min, minWidthOrHeight),
					possibleRectangleWidth.max);
			} else {
				height = distanceBetweenParallel;
				width = Chance.rand(
					Math.max(possibleRectangleWidth.min, minWidthOrHeight),
					possibleRectangleWidth.max);
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
			assert rectangle.touches(a);
			assert rectangle.touches(b);
			assert rectangle.touches(c);
			pieces.modifySidesByPlacingRectangle(rectangle, a, b, c);
			return rectangle;
		}
		private int distanceFromPerpendicularToClosestEndOf(RectangleSidePiece perpendicularPiece, RectangleSidePiece parallelPiece) {
			Point point = parallelPiece.segment
				.getEndPoint(perpendicularPiece.direction.opposite());
			if (!perpendicularPiece.line.hasPointFromSide(
				point,
				perpendicularPiece.direction)) {
				return 0;
			}
			return perpendicularPiece.perpendicularDistanceTo(point);
		}
		public RectangleArea getRectangleBetweenParallelSides(RectangleSidePiece piece1, RectangleSidePiece piece2) {
			assert piece1.line.orientation == piece2.line.orientation;
			int minCoordEnd = Math.max(
				piece1.segment.getStartCoord(),
				piece2.segment.getStartCoord());
			int minCoord = minCoordEnd - possibleRectangleWidth.max + 1;
			int maxCoord = Math.min(
				piece1.segment.getEndCoord(),
				piece2.segment.getEndCoord()) + possibleRectangleWidth.max - 1;
			int endCoordsDistance = maxCoord - minCoord;
			assert minCoord < maxCoord;
			int maxStartCoord = minCoord + endCoordsDistance - possibleRectangleWidth.max + 1;
			int startVariableCoord = Chance.rand(minCoord, maxStartCoord);
			int randomizedDimension = Chance.rand(Math.max(
				minCoordEnd - startVariableCoord + 1,
				possibleRectangleWidth.min), Math.min(
				endCoordsDistance - startVariableCoord + minCoord,
				possibleRectangleWidth.max));
			int x, y, width, height;
			int startNonVariableCoord = Math
				.min(
					piece1.line.getStaticCoordFromSide(piece1.direction) + piece1.direction
						.getGrowing() * borderWidth,
					piece2.line.getStaticCoordFromSide(piece2.direction) + piece2.direction
						.getGrowing() * borderWidth);
			if (curOffset != GENERATED_OFFSET) {
				startVariableCoord = minCoord + minCoordEnd - minCoord - (piece1.line.orientation
					.isHorizontal() ? curWidth : curHeight) + 1 + curOffset;
			}
			int distanceBetweenLines = piece1.line.distanceTo(piece2.line);
			if (piece1.line.orientation.isHorizontal()) {
				x = startVariableCoord;
				y = startNonVariableCoord;
				width = randomizedDimension;
				height = distanceBetweenLines;
			} else {
				assert piece1.line.orientation.isVertical();
				x = startNonVariableCoord;
				y = startVariableCoord;
				width = distanceBetweenLines;
				height = randomizedDimension;
			}
			if (curWidth != 0) {
				width = curWidth;
			}
			if (curHeight != 0) {
				height = curHeight;
			}
			RectangleArea rectangle = new RectangleArea(x, y, width, height);
			pieces.modifySidesByPlacingRectangle(rectangle, piece1, piece2);
			return rectangle;
		}
		/**
		 * 
		 * @param piece
		 *            Actual piece
		 * @return
		 */
		public RectangleArea getRectangleOn1Side(RectangleSidePiece originalPiece) {
			assert originalPiece != null;
			int width = Chance.rand(possibleRectangleWidth);
			int height = Chance.rand(possibleRectangleWidth);
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
			if (curOffset != GENERATED_OFFSET) {
				offset = curOffset;
			}
			RectangleArea r = create(
				originalPiece.createRectangle(1),
				originalPiece.direction,
				width,
				height,
				offset);
			pieces.modifySidesByPlacingRectangle(r, originalPiece);
			return r;
		}
		private RectangleSidePiece createMinPieceContainingSameLinePieces(ImmutableSet<RectangleSidePiece> originalPieces) {
			RectangleSidePiece anyPiece = originalPieces.iterator().next();
			int minDynamicCoord = Integer.MAX_VALUE;
			int maxDynaminCoord = Integer.MIN_VALUE;
			for (RectangleSidePiece piece : originalPieces) {
				assert piece.line.equals(anyPiece.line);
				assert piece.direction == anyPiece.direction;
				minDynamicCoord = Math.min(
					minDynamicCoord,
					piece.segment.getStartCoord());
				maxDynaminCoord = Math.max(
					maxDynaminCoord,
					piece.segment.getEndCoord());
			}
			int x = anyPiece.isVertical() ? anyPiece.segment.x : minDynamicCoord;
			int y = anyPiece.isVertical() ? minDynamicCoord : anyPiece.segment.y;
			int length = maxDynaminCoord - minDynamicCoord + 1;
			return new RectangleSidePiece(anyPiece.direction, x, y, length);
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
			int minHeight, minWidth;
			if (!dynamicCoordsRangeContainStaticCoord(horizontal, vertical)) {
				minWidth = Math.abs(horizontal.getSegment().getEndPoint(
					vertical.direction.opposite()).x - vertical
					.getSegment()
					.getEndPoint(horizontal.direction.opposite()).x);
			} else {
				minWidth = 0;
			}
			if (!dynamicCoordsRangeContainStaticCoord(vertical, horizontal)) {
				minHeight = Math
					.abs(vertical.segment.getEndPoint(horizontal.direction
						.opposite()).y - horizontal.segment
						.getEndPoint(vertical.direction.opposite()).y);
			} else {
				minHeight = 0;
			}
			assert minWidth >= 0;
			assert minHeight >= 0;
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
		private boolean dynamicCoordsRangeContainStaticCoord(RectangleSidePiece dynamicCoordPiece, RectangleSidePiece staticCoordPiece) {
			return dynamicCoordPiece.segment.asRange().contains(
				staticCoordPiece.line
					.getStaticCoordFromSide(staticCoordPiece.direction
						.opposite()));
		}
		/**
		 * <b>Places on this VirtualWave all the rectangles that can be built in
		 * it touching 4 RectangleSidePieces on previous wave. Also if any sides
		 * forming {@code junction} are original sides, remove them from the
		 * collection of original sides, as they are now occupied.</p>
		 * <p>
		 * Before placing a rectangle in a junction, algorithm tries to finish
		 * building that junction, adding all possible pieces to it.
		 * 
		 * @return A map where keys are RectangleAreas newly created during
		 *         current call to this method, and values are collections of
		 *         new free pieces these rectangles add to VirtualWave.
		 */
		private void occupyJunctionWithRectangle(PiecesJunction junction, JunctionType type) {
			assert junction != null;
			assert type != null;
			assert junction.amountOfSides() == type.amountOfSides && junction
				.getType() == type;
			pieces.finishBuildingJunction(junction);
			RectangleArea rectangle;
			Iterator<RectangleSidePiece> iter = junction.pieces
				.values()
				.iterator();
			JunctionType updatedType = junction.getType();
			switch (updatedType) {
				case FOUR_PIECES:
					Map<CardinalDirection, RectangleSidePiece> pieces = junction.pieces;
					rectangle = getRectangleBetween4Sides(
						pieces.get(Directions.N),
						pieces.get(Directions.E),
						pieces.get(Directions.S),
						pieces.get(Directions.W));
					break;
				case THREE_PIECES:
					rectangle = getRectangleBetween3Sides(
						iter.next(),
						iter.next(),
						iter.next());
					break;
				case TWO_PARALLEL_PIECES:
					rectangle = getRectangleBetweenParallelSides(
						iter.next(),
						iter.next());
					break;
				case TWO_PERPENDICULAR_PIECES:
					rectangle = getRectangleBetween2Sides(
						iter.next(),
						iter.next());
					break;
				case SINGLE_PIECE:
				default:
					assert type == JunctionType.SINGLE_PIECE;
					rectangle = getRectangleOn1Side(iter.next());
			}
			assert rectangle != null;
			addVirtualRectangle(rectangle);
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
		}
		private boolean piecesAreParallelAndCloseEnough(RectangleSidePiece a, RectangleSidePiece b) {
			if (!a.direction.isOpposite(b.direction)) {
				// Find pieces whose directions are opposite...
				return false;
			}
			int distance = a.line.distanceTo(b.line);
			if (distance > possibleRectangleWidthPlus2BorderWidth.max || distance <= borderWidth * 2) {
				// ... and which are %possibleRectangleWidth% cells far from
				// each
				// other...
				return false;
			}
			if (!a.line.hasPointFromSide(
				b.segment.getEndPoint(b.direction.clockwiseQuarter()),
				a.direction)) {
				return false;
			}
			// TODO: Allow rectangles without intersection by dynamic coord
			// to be rectangleable
			if (a.intersectionByDynamicCoord(b) == 0) {
				// and which can contain a rectangle between them.
				if (canRectangleBePlacedBetweenNonDynIntersectingParallelPieces(
					a,
					b)) {
					return true;
				}
				return false;
			}
			return true;
		}
		private boolean canRectangleBePlacedBetweenNonDynIntersectingParallelPieces(RectangleSidePiece piece1, RectangleSidePiece piece2) {
			return distanceBetweenPiecesByDynamicCoord(piece1, piece2) <= possibleRectangleWidth.max && !hasRectanglesBetweenNonDynIntersectingParallelPieces(
				piece1,
				piece2);
		}
		private boolean hasRectanglesBetweenNonDynIntersectingParallelPieces(RectangleSidePiece piece1, RectangleSidePiece piece2) {
			assert piece1.intersectionByDynamicCoord(piece2) == 0;
			assert piece1.direction.isOpposite(piece2.direction);
			RectangleSidePiece oneMockPiece, anotherMockPiece;
			int mockMinStaticCoord = Math.min(
				piece1.segment.getEndCoord(),
				piece2.segment.getEndCoord());
			int mockMaxStaticCoord = Math.max(
				piece1.segment.getStartCoord(),
				piece2.segment.getStartCoord());
			int minStatic = piece1.line
				.getStaticCoordFromSide(piece1.direction);
			int maxStatic = piece2.line
				.getStaticCoordFromSide(piece2.direction);
			if (minStatic > maxStatic) {
				int buf = minStatic;
				minStatic = maxStatic;
				maxStatic = buf;
			}
			Range rangeBetweenParallels = new Range(minStatic, maxStatic);
			if (piece1.isVertical()) {
				oneMockPiece = new RectangleSidePiece(
					Directions.N,
					0,
					mockMinStaticCoord,
					1);
				anotherMockPiece = new RectangleSidePiece(
					Directions.S,
					0,
					mockMaxStaticCoord,
					1);
			} else {
				oneMockPiece = new RectangleSidePiece(
					Directions.W,
					mockMinStaticCoord,
					0,
					1);
				anotherMockPiece = new RectangleSidePiece(
					Directions.E,
					mockMaxStaticCoord,
					0,
					1);
			}
			for (RectangleSidePiece startMockPiece : new RectangleSidePiece[] {
				oneMockPiece, anotherMockPiece
			}) {
				TreeSet<RectangleSidePiece> treeSet = pieces.pieces
					.get(startMockPiece.direction);
				RectangleSidePiece next = startMockPiece;
				do {
					next = treeSet.lower(next);
					if (next == null) {
						break;
					}
					if (next.segment.asRange().overlaps(rangeBetweenParallels)) {
						return true;
					}
				} while (next.line
					.getStaticCoordFromSide(startMockPiece.direction) < mockMaxStaticCoord);
			}
			return false;
		}
		private int distanceBetweenPiecesByDynamicCoord(RectangleSidePiece piece1, RectangleSidePiece piece2) {
			assert piece1.intersectionByDynamicCoord(piece2) == 0;
			return Math.max(
				piece1.segment.getStartCoord(),
				piece2.segment.getStartCoord()) - Math.min(
				piece1.segment.getEndCoord(),
				piece2.segment.getEndCoord());

		}

		class RectangleablePiecesCollection {
			private Map<CardinalDirection, TreeSet<RectangleSidePiece>> pieces = new HashMap<CardinalDirection, TreeSet<RectangleSidePiece>>();
			private Map<RectangleSidePiece, PiecesJunction> junctions = new HashMap<RectangleSidePiece, PiecesJunction>();
			/** Pieces that form previous front. */
			private final Collection<RectangleSidePiece> originalPieces = new HashSet<RectangleSidePiece>();
			private final Collection<RectangleSidePiece> newPieces = new ArrayList<RectangleSidePiece>();
			/**
			 * Compares which one of two same-direction pieces lies further in
			 * direction they are facing.
			 */
			private final Comparator<RectangleSidePiece> PIECES_COMPARATOR = new Comparator<RectangleSidePiece>() {
				@Override
				public int compare(RectangleSidePiece piece1, RectangleSidePiece piece2) {
					assert piece1.direction == piece2.direction;
					assert piece1 == piece2 || !piece1.overlaps(piece2);
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
			private ArrayList<RectangleSidePiece> addedPieces = Lists
				.newArrayList();
			private Map<Rectangle, ArrayList<RectangleSidePiece>> piecesAddedBy = new HashMap<Rectangle, ArrayList<RectangleSidePiece>>();

			/**
			 * @param originalPieces
			 *            Pieces that form the previous front. Algorithm will
			 *            build new rectangles that touch these pieces.
			 */
			private RectangleablePiecesCollection(Collection<RectangleSidePiece> originalPieces) {
				this.originalPieces.addAll(originalPieces);
				newPieces.addAll(originalPieces);
				for (CardinalDirection direction : CardinalDirection.values()) {
					pieces.put(direction, new TreeSet<RectangleSidePiece>(
						PIECES_COMPARATOR));
				}
			}
			public void buildInitialJunctions() {
				for (RectangleSidePiece piece : newPieces) {
					pieces.get(piece.direction).add(piece);
					newJunctionOf(piece);
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
			 * Adds all possible pieces to {@code junction}, mutating it.
			 * 
			 * @param junction
			 */
			private void finishBuildingJunction(PiecesJunction junction) {
				ImmutableSet<RectangleSidePiece> copySet = ImmutableSet
					.copyOf(junction.pieces.values());
				for (RectangleSidePiece originalPiece : copySet) {
					if (!isOriginal(originalPiece)) {
						continue;
					}
					junctOriginalToAvailableNew(originalPiece);
				}
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
				canvas.draw(piece1);
				canvas.draw(piece2);
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
			public PiecesJunction getOneJunctionWithOriginalPieces(JunctionType type) {
				for (Map.Entry<RectangleSidePiece, PiecesJunction> entry : junctions
					.entrySet()) {
					if (!isOriginal(entry.getKey())) {
						continue;
					}
					PiecesJunction junction = entry.getValue();
					if (junction.getType() == type) {
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
			private PiecesJunction[] getJunctions(CardinalDirection dir) {
				Collection<PiecesJunction> answer = new HashSet<PiecesJunction>();
				for (PiecesJunction junction : junctions.values()) {
					if (!answer.contains(junction) && junction
						.hasPieceFromSide(dir)) {
						answer.add(junction);
					}
				}
				return answer.toArray(new PiecesJunction[answer.size()]);
			}
			private RectangleSidePiece[] getPieces(CardinalDirection dir, int width) {
				Collection<RectangleSidePiece> answer = new ArrayList<RectangleSidePiece>();
				for (RectangleSidePiece piece : pieces.get(dir)) {
					if (piece.segment.length == width) {
						answer.add(piece);
					}
				}
				return answer.toArray(new RectangleSidePiece[answer.size()]);
			}
			private void buildJunctions() {
				// buildParallelJunctions();
				for (RectangleSidePiece originalPiece : originalPieces) {
					junctOriginalToAvailableNew(originalPiece);
				}
				newPieces.clear();
			}
			private void junctOriginalToAvailableNew(RectangleSidePiece piece) {
				assert piece != null;
				assert isInSomeJunction(piece);
				PiecesJunction junction = junctions.get(piece);
				RectangleSidePiece parallelPiece = getParallelRectangleablePiece(piece);
				if (parallelPiece != null) {
					if (!junction.hasPieceParallelTo(piece)) {
						// TODO: Maybe they always can accept a new piece. Try
						// to get rid of this call.
						if (junction.canAcceptParallelPiece(parallelPiece)) {
							if (isInSomeJunction(parallelPiece)) {
								removePieceFromItsJunction(parallelPiece);
							}
							assert !isInSomeJunction(parallelPiece);
							addNewPieceToJunction(piece, parallelPiece);
						}
					}
				}

				PerpendicularPiecesPair perpendicularPieces = getPerpendicularRectangleablePieces(piece);
				if (junction.hasUnoccupiedSidePerpendicularTo(piece)) {
					for (RectangleSidePiece perpendicularPiece : perpendicularPieces) {
						if (!junction.isPieceBetterThanCurrent(
							perpendicularPiece,
							piece)) {
							continue;
						}
						// TODO: Maybe they always can accept a new piece.
						// Try to get rid of this call.
						if (!junction
							.canAcceptPerpendicularPiece(perpendicularPiece)) {
							continue;
						}
						if (isInSomeJunction(perpendicularPiece)) {
							removePieceFromItsJunction(perpendicularPiece);
						}
						assert !isInSomeJunction(perpendicularPiece);
						addNewPieceToJunction(piece, perpendicularPiece);
					}
				}
			}
			private boolean hasParallelPieceInItsJunction(RectangleSidePiece piece) {
				assert isInSomeJunction(piece);
				return junctions.get(piece).pieces.containsKey(piece.direction
					.opposite());
			}
			private boolean isInSomeJunction(RectangleSidePiece piece) {
				return junctions.containsKey(piece);
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
			private void draw(RectangleSidePiece shape) {
				canvas.draw(
					shape,
					DrawingRectangleSidePiece.withColor(Color.RED));
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
				// Place pieces for those sides that have neighbor pieces from
				// previous front.
				for (RectangleSidePiece piece : pieces) {
					assert Sets
						.newHashSet(this.pieces.get(piece.direction))
						.contains(piece);
				}
				for (RectangleSidePiece piece : pieces) {
					RectangleSidePiece rPiece = r
						.getSideAsSidePiece(piece.direction.opposite());
					RectangleSidePiece[] splitPieces = piece
						.splitWithPiece(rPiece);
					TreeSet<RectangleSidePiece> treeSet = this.pieces
						.get(piece.direction);

					assert splitPieces.length == 0 || splitPieces[0] != piece;
					ImmutableSet<RectangleSidePiece> touchingPieces = getPiecesThatTouch(
						rPiece,
						piece);
					assert !touchingPieces.isEmpty();
					// Modify original pieces lying on the same line,
					// splitting them under rPiece.
					for (RectangleSidePiece touchingPiece : touchingPieces) {
						assert rPiece.touches(touchingPiece);
						boolean isOriginal = isOriginal(touchingPiece);
						if (isOriginal) {
							forgetOriginalPiece(touchingPiece);
						}
						if (isInSomeJunction(touchingPiece)) {
							removePieceFromItsJunction(touchingPiece);
						}
						removePiece(touchingPiece);
						for (RectangleSidePiece splitTouchingPiece : touchingPiece
							.splitWithPiece(rPiece)) {
							assert touchingPiece != null;
							if (isOriginal) {
								this.originalPieces.add(splitTouchingPiece);
								newJunctionOf(splitTouchingPiece);
							}
							treeSet.add(splitTouchingPiece);
							newPieces.add(splitTouchingPiece);
							addedPieces.add(splitTouchingPiece);
							rememberPieceAdded(r, splitTouchingPiece);
						}
					}

					directionsUnused.remove(rPiece.direction);
					for (RectangleSidePiece rSplitPiece : rPiece
						.splitWithPieces(touchingPieces)) {
						this.pieces.get(rSplitPiece.direction).add(rSplitPiece);
						newPieces.add(rSplitPiece);
						addedPieces.add(rSplitPiece);
						rememberPieceAdded(r, rSplitPiece);
					}
					assertRectangleNotIntersectsWithOthers(r);
					assertJunctionsHaveRealPieces();
				}

				// Place pieces from other sides of the new rectangle
				for (CardinalDirection dir : directionsUnused) {
					RectangleSidePiece piece = r.getSideAsSidePiece(dir);
					newPieces.add(piece);
					this.pieces.get(piece.direction).add(piece);
					addedPieces.add(piece);
					rememberPieceAdded(r, piece);
				}
				canvas.draw(r);
			}
			private void rememberPieceAdded(Rectangle r, RectangleSidePiece piece) {
				if (!piecesAddedBy.containsKey(r)) {
					piecesAddedBy.put(r, new ArrayList<RectangleSidePiece>());
				}
				piecesAddedBy.get(r).add(piece);
			}
			private ArrayList<RectangleSidePiece> getPiecesAddedBy(Rectangle r) {
				return piecesAddedBy.get(r);
			}
			private boolean isOriginal(RectangleSidePiece piece) {
				return originalPieces.contains(piece);
			}
			/**
			 * Returns a set of pieces all of which touch piece {@code rPiece}.
			 * 
			 * @param rPiece
			 *            The piece that touches some other pieces.
			 * @param modifiedPiece
			 *            One piece that is known to touch rPiece.
			 * @return A set of pieces touching {@code rPiece} including
			 *         {@code modifiedPiece}.
			 */
			private ImmutableSet<RectangleSidePiece> getPiecesThatTouch(RectangleSidePiece rPiece, RectangleSidePiece modifiedPiece) {
				CardinalDirection originalsDirection = rPiece.direction
					.opposite();
				Builder<RectangleSidePiece> builder = ImmutableSet
					.<RectangleSidePiece> builder();
				TreeSet<RectangleSidePiece> treeSet = pieces
					.get(originalsDirection);
				RectangleSidePiece currentPiece = modifiedPiece;
				while (currentPiece != null && currentPiece.touches(rPiece)) {
					builder.add(currentPiece);
					currentPiece = treeSet.higher(currentPiece);
				}
				currentPiece = treeSet.lower(modifiedPiece);
				while (currentPiece != null && currentPiece.touches(rPiece)) {
					builder.add(currentPiece);
					currentPiece = treeSet.lower(currentPiece);
				}
				return builder.build();
			}
			private Map<Rectangle, Collection<Rectangle>> findIntersectingRectangles() {
				Map<Rectangle, Collection<Rectangle>> answer = new HashMap<Rectangle, Collection<Rectangle>>();
				for (RectangleArea r1 : virtualRectangles) {
					for (RectangleArea r2 : virtualRectangles) {
						if (r1 == r2) {
							continue;
						}
						if (r1.intersects(r2)) {
							if (!answer.containsKey(r1)) {
								answer.put(r1, new ArrayList<Rectangle>());
							}
							answer.get(r1).add(r2);
						}
					}
				}
				for (RectangleArea r1 : wave.get(0)) {
					for (RectangleArea r2 : virtualRectangles) {
						if (r1 == r2) {
							continue;
						}
						if (r1.intersects(r2)) {
							if (!answer.containsKey(r1)) {
								answer.put(r1, new ArrayList<Rectangle>());
							}
							answer.get(r1).add(r2);
						}
					}
				}
				return answer;
			}
			private boolean isInPieces(RectangleSidePiece piece) {
				return Sets.newHashSet(pieces.get(piece.direction)).contains(
					piece);
			}
			/**
			 * Tests that all pieces present in {@code junctions} are present in
			 * {@code pieces}
			 */
			private void assertJunctionsHaveRealPieces() {
				for (PiecesJunction junction : junctions.values()) {
					for (RectangleSidePiece piece : junction.pieces.values()) {
						assert Sets
							.newHashSet(pieces.get(piece.direction))
							.contains(piece);
					}
				}
			}
			private void assertRectangleNotIntersectsWithOthers(Rectangle newRectangle) {
				for (RectangleArea existingRectangle : virtualRectangles) {
					assert !newRectangle.intersects(existingRectangle);
				}
				for (RectangleArea rec : wave.get(0)) {
					assert !newRectangle.intersects(rec);
				}
			}
			private RectangleSidePiece[] getPiecesTouching(RectangleArea r) {
				Collection<RectangleSidePiece> answer = Lists.newArrayList();
				for (RectangleSidePiece piece : addedPieces) {
					if (r.getSideAsSidePiece(piece.direction).contains(piece)) {
						answer.add(piece);
					}
				}
				return answer.toArray(new RectangleSidePiece[answer.size()]);
			}
			private int indexOf(Rectangle r) {
				if (virtualRectangles.contains(r)) {
					return virtualRectangles.indexOf(r);
				} else {
					return virtualRectangles.size();
				}
			}
			private void drawAll(Collection objs) {
				for (Object o : objs) {
					canvas.draw(o);
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
					if (piecesArePerpendicularAndCloseEnough(
						testingPiece,
						perpendicularPiece)) {
						return true;
					}
				}
				return false;
			}
			private void removePiece(RectangleSidePiece piece) {
				assert !junctions.containsKey(piece);
				pieces.get(piece.direction).remove(piece);
			}
			/**
			 * Remove a piece from originals. This is necessary when a new
			 * rectangle overlays an original piece.
			 * 
			 * @param original
			 */
			private void forgetOriginalPiece(RectangleSidePiece original) {
				assert original != null;
				assert originalPieces.contains(original);
				boolean removed = originalPieces.remove(original);
				assert removed;
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
				junctions.get(piece).removePiece(piece);
				junctions.remove(piece);
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
					} while (nextPiece != null && !piecesArePerpendicularAndCloseEnough(
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
						.getEndPoint(Directions.S);
					Point startingPointS = piece.segment
						.getEndPoint(Directions.N);
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
						.getEndPoint(Directions.E);
					Point startingPointE = piece.segment
						.getEndPoint(Directions.W);
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
				int x, y;
				if (piece.isVertical()) {
					x = piece.line.getStaticCoordFromSide(piece.direction);
					y = piece.segment.y;
				} else {
					x = piece.segment.x;
					y = piece.line.getStaticCoordFromSide(piece.direction);
				}
				return new RectangleSidePiece(
					piece.direction.opposite(),
					x,
					y,
					piece.segment.length);
			}
			private PiecesJunction newJunctionOf(RectangleSidePiece newPiece) {
				assert !isInSomeJunction(newPiece);
				PiecesJunction newJunction = new PiecesJunction(newPiece);
				junctions.put(newPiece, newJunction);
				return newJunction;
			}
			private void addNewPieceToJunction(RectangleSidePiece pieceInJunction, RectangleSidePiece newPiece) {
				assert pieceInJunction != null;
				assert newPiece != null;
				assert junctions.containsKey(pieceInJunction);
				// if (junctions.containsKey(pieceInJunction)) {
				PiecesJunction junction = junctions.get(pieceInJunction);
				junction.setPiece(newPiece);
				junctions.put(newPiece, junction);
				if (newPieces.contains(newPiece)) {
					newPieces.remove(newPiece);
				}
				// } else {
				// PiecesJunction newJunction = new PiecesJunction(
				// newPiece,
				// pieceInJunction);
				// junctions.put(pieceInJunction, newJunction);
				// junctions.put(newPiece, newJunction);
				// }

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
				public JunctionType getType() {
					if (pieces.size() == 4) {
						return JunctionType.FOUR_PIECES;
					}
					if (pieces.size() == 3) {
						return JunctionType.THREE_PIECES;
					}
					if (pieces.size() == 1) {
						return JunctionType.SINGLE_PIECE;
					}
					if (pieces.containsKey(Directions.N) && pieces
						.containsKey(Directions.S) || pieces
						.containsKey(Directions.W) && pieces
						.containsKey(Directions.E)) {
						return JunctionType.TWO_PARALLEL_PIECES;
					}
					return JunctionType.TWO_PERPENDICULAR_PIECES;
				}
				/**
				 * Checks if a piece is better suited for {@code originalPiece}
				 * 's junction than the piece that is in the junction from that
				 * side right now.
				 * 
				 * @param perpendicularPiece
				 * @param originalPiece
				 * @return
				 */
				private boolean isPieceBetterThanCurrent(RectangleSidePiece perpendicularPiece, RectangleSidePiece originalPiece) {
					assert perpendicularPiece.direction != originalPiece.direction;
					CardinalDirection dir = perpendicularPiece.direction;
					if (!pieces.containsKey(dir)) {
						return true;
					}
					RectangleSidePiece current = pieces.get(dir);
					if (current == perpendicularPiece) {
						return false;
					}
					CardinalDirection originalDir = originalPiece.direction;
					int currentDitanceToOrig = originalPiece
						.perpendicularDistanceTo(current.segment
							.getEndPoint(originalDir));
					int newDistanceToOrig = originalPiece
						.perpendicularDistanceTo(perpendicularPiece.segment
							.getEndPoint(originalDir));
					if (currentDitanceToOrig < newDistanceToOrig) {
						return false;
					}
					return true;
				}
				public boolean canAcceptPerpendicularPiece(RectangleSidePiece newPiece) {
					if (pieces.containsKey(newPiece.direction)) {
						return false;
					}
					for (CardinalDirection dir : pieces.keySet()) {
						if (dir.isPerpendicular(newPiece.direction) && !piecesArePerpendicularAndCloseEnough(
							pieces.get(dir),
							newPiece)) {
							return false;
						}
					}
					// Check if a piece in this junction that would be parallel
					// to newPiece is close enough to newPiece
					RectangleSidePiece parallelPiece = pieces
						.get(newPiece.direction.opposite());
					if (parallelPiece != null && parallelPiece.line
						.distanceTo(newPiece.line) > possibleRectangleWidthPlus2BorderWidth.max) {
						return false;
					}
					return true;
				}
				public boolean canAcceptParallelPiece(RectangleSidePiece newPiece) {
					if (pieces.containsKey(newPiece.direction)) {
						return false;
					}
					CardinalDirection oppositeDir = newPiece.direction
						.opposite();
					if (!pieces.containsKey(newPiece.direction) && !pieces
						.containsKey(oppositeDir)) {
						return true;
					}
					if (!piecesAreParallelAndCloseEnough(
						pieces.get(oppositeDir),
						newPiece)) {
						return false;
					}
					CardinalDirection perpendicular1 = newPiece.direction
						.clockwiseQuarter(), perpendicular2 = newPiece.direction
						.counterClockwiseQuarter();
					if (hasPieceFromSide(perpendicular1) && !piecesArePerpendicularAndCloseEnough(
						newPiece,
						pieces.get(perpendicular1))) {
						return false;
					}
					if (hasPieceFromSide(perpendicular2) && !piecesArePerpendicularAndCloseEnough(
						newPiece,
						pieces.get(perpendicular2))) {
						return false;
					}
					return true;
				}
				public boolean hasPieceFromSide(CardinalDirection direction) {
					return pieces.containsKey(direction);
				}
				public boolean hasUnoccupiedSidePerpendicularTo(RectangleSidePiece originalPiece) {
					return !pieces.containsKey(originalPiece.direction
						.counterClockwiseQuarter()) || !pieces
						.containsKey(originalPiece.direction.clockwiseQuarter());
				}
				public boolean hasPieceParallelTo(RectangleSidePiece originalPiece) {
					return pieces.containsKey(originalPiece.direction
						.opposite());
				}
				public void removePiece(RectangleSidePiece oldPiece) {
					assert pieces.containsKey(oldPiece.direction);
					assert pieces.get(oldPiece.direction) == oldPiece;
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
					assert !isInSomeJunction(newPiece);
					if (pieces.containsKey(newPiece.direction.opposite())) {
						RectangleSidePiece parallelPiece = pieces
							.get(newPiece.direction.opposite());
						assert parallelPiece.line.hasPointFromSide(
							newPiece.segment.getEndPoint(newPiece.direction
								.clockwiseQuarter()),
							parallelPiece.direction) : parallelPiece + " " + newPiece;
					}
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

	private enum JunctionType {
		SINGLE_PIECE(1), TWO_PERPENDICULAR_PIECES(2), TWO_PARALLEL_PIECES(2), THREE_PIECES(
			3), FOUR_PIECES(4);
		private static ArrayList<JunctionType> ORDERED = Lists.newArrayList(
			FOUR_PIECES,
			THREE_PIECES,
			TWO_PARALLEL_PIECES,
			TWO_PERPENDICULAR_PIECES,
			SINGLE_PIECE);
		private final int amountOfSides;

		private JunctionType(int amountOfSides) {
			this.amountOfSides = amountOfSides;
		}
	}
}
