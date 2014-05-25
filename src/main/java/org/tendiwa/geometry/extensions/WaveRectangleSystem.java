package org.tendiwa.geometry.extensions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;
import org.tendiwa.core.GrowingRectangleSystem;
import org.tendiwa.core.OrdinalDirection;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.core.meta.Range;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.drawing.*;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.drawing.extensions.DrawingRectangleSidePiece;
import org.tendiwa.geometry.*;

import java.awt.*;
import java.util.*;

import static org.tendiwa.core.Directions.*;

public class WaveRectangleSystem extends GrowingRectangleSystem {
    public final static boolean DEBUG = false;
    public static final DrawingAlgorithm<Placeable> DRAWING_RECTANGLE = DrawingRectangle.withColorLoop(Color.BLACK, Color.GRAY, Color.LIGHT_GRAY, Color.DARK_GRAY);
    /**
     * <p> Compares same-direction pieces. If two pieces lie on different lines, the greatest is the one that lies
     * further
     * in direction they are facing. If two pieces lie on the same lie, the greatest is the one that has greater
     * dynamic
     * coordinate. </p>
     */
    private static final Comparator<RectangleSidePiece> PIECES_COMPARATOR = new Comparator<RectangleSidePiece>() {
        @Override
        public int compare(RectangleSidePiece piece1, RectangleSidePiece piece2) {
            assert piece1.getDirection() == piece2.getDirection();
            assert piece1 == piece2 || !piece1.overlaps(piece2);
            if (piece1.getDirection() == Directions.N) {
                int dy = piece2.getSegment().getY() - piece1.getSegment().getY();
                if (dy == 0) {
                    return piece1.getSegment().getX() - piece2.getSegment().getX();
                } else {
                    return dy;
                }
            } else if (piece1.getDirection() == Directions.E) {
                int dx = piece1.getSegment().getX() - piece2.getSegment().getX();
                if (dx == 0) {
                    return piece1.getSegment().getY() - piece2.getSegment().getY();
                } else {
                    return dx;
                }
            } else if (piece1.getDirection() == Directions.S) {
                int dy = piece1.getSegment().getY() - piece2.getSegment().getY();
                if (dy == 0) {
                    return piece1.getSegment().getX() - piece2.getSegment().getX();
                } else {
                    return dy;
                }
            } else {
                assert piece1.getDirection() == Directions.W;
                int dx = piece2.getSegment().getX() - piece1.getSegment().getX();
                if (dx == 0) {
                    return piece1.getSegment().getY() - piece2.getSegment().getY();
                } else {
                    return dx;
                }
            }
        }
    };
    private final Range possibleRectangleWidth;
    private final Range possibleRectangleWidthPlus2BorderWidth;
    private final StringBuilder log = new StringBuilder();
    public TestCanvas canvas;
    /**
     * A wave #x is a set of rectangles that were created as neighbors of rectangles of a previous wave #x-1, with a
     * wave #1
     * being the initial rectangle system.
     */
    private ArrayList<HashSet<org.tendiwa.geometry.Rectangle>> wave = new ArrayList<>();
    private HashSet<RectangleSidePiece> freeSidePiecesOnPreviousWave = new HashSet<>();
    private int lastFullyOccupiedWave = -1;
    private VirtualWave virtualCurrentWave;
    private DrawingAlgorithm<Placeable> RED = DrawingRectangle
            .withColor(Color.RED);
    private DrawingAlgorithm<Placeable> GREEN = DrawingRectangle
            .withColor(Color.GREEN);

    public WaveRectangleSystem(int borderWidth, Range possibleRectangleWidth, RectangleSystem initialRecSys) {
        super(borderWidth);
        if (possibleRectangleWidth.min <= 0) {
            throw new IllegalArgumentException(
                    "Range must contain only values > 0");
        }
        this.canvas = new TestCanvas(3, 200, 200);
        if (DEBUG) {
            this.canvas.hide();
        }
        this.possibleRectangleWidth = possibleRectangleWidth;
        possibleRectangleWidthPlus2BorderWidth = new Range(
                possibleRectangleWidth.min + borderWidth,
                possibleRectangleWidth.max + borderWidth);
        HashSet<org.tendiwa.geometry.Rectangle> initialWave = new HashSet<>();
        for (org.tendiwa.geometry.Rectangle r : initialRecSys) {
            initialWave.add(r);
            canvas.draw(r, DrawingRectangle.withColor(Color.BLUE));
        }
        wave.add(initialWave);
        do {
            addRectangleFromVirtualWave(initialRecSys);
        } while (!virtualCurrentWave.virtualRectangles.isEmpty());
    }

    public WaveRectangleSystem(RectangleSystem rs, Range possibleRectangleWidth) {
        super(rs.getBorderWidth());
        this.possibleRectangleWidth = possibleRectangleWidth;
        possibleRectangleWidthPlus2BorderWidth = new Range(
                possibleRectangleWidth.min + borderWidth,
                possibleRectangleWidth.max + borderWidth);
        HashSet<org.tendiwa.geometry.Rectangle> initialWave = new HashSet<>();
        for (org.tendiwa.geometry.Rectangle r : rs) {
            addRectangle(r);
            initialWave.add(r);
        }
    }

    /**
     * Returns a new RectangleSidePiece with the same line, but with opposite direction.
     *
     * @param piece
     * @return
     */
    private RectangleSidePiece inversePiece(RectangleSidePiece piece) {
        int x, y;
        if (piece.isVertical()) {
            x = piece.getLine().getStaticCoordFromSide(piece.getDirection());
            y = piece.getSegment().getY();
        } else {
            x = piece.getSegment().getX();
            y = piece.getLine().getStaticCoordFromSide(piece.getDirection());
        }
        return new RectangleSidePiece(
                piece.getDirection().opposite(),
                x,
                y,
                piece.getSegment().getLength());
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

    public void addRectangleFromVirtualWave(RectangleSystem irs) {
        if (virtualCurrentWave == null || virtualCurrentWave.virtualRectangles
                .size() == 0) {
            buildVirtualCurrentWave(irs);
            lastFullyOccupiedWave++;
            wave.add(new HashSet<org.tendiwa.geometry.Rectangle>());
        }
        Iterator<org.tendiwa.geometry.Rectangle> iterator = virtualCurrentWave.virtualRectangles
                .iterator();
        org.tendiwa.geometry.Rectangle r = iterator.next();
        addRectangle(r);
        wave.get(lastFullyOccupiedWave + 1).add(r);
        iterator.remove();
    }

    public void buildWave() {
        if (virtualCurrentWave.virtualRectangles.size() != 0) {
            throw new IllegalStateException();
        }
        for (org.tendiwa.geometry.Rectangle r : virtualCurrentWave.virtualRectangles) {
            addRectangle(r);
            wave.get(lastFullyOccupiedWave + 1).add(r);
            virtualCurrentWave.virtualRectangles.clear();
        }

    }

    private void buildVirtualCurrentWave(RectangleSystem irs) {
        findFreeSidePieces(irs);
        virtualCurrentWave = new VirtualWave();
        if (virtualCurrentWave.virtualRectangles.size() == 0) {
            throw new IllegalStateException();
        }
    }

    private void findFreeSidePieces(RectangleSystem irs) {
        for (org.tendiwa.geometry.Rectangle r : wave.get(wave.size() - 1)) {
            for (CardinalDirection side : CardinalDirection.values()) {
                freeSidePiecesOnPreviousWave.addAll(irs
                        .getSidePiecesFreeFromNeighbours(r, side));
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
        if (piece1.getLine().isParallel(piece2.getLine())) {
            return piece3;
        }
        if (piece1.getLine().isParallel(piece3.getLine())) {
            return piece2;
        }
        if (piece2.getLine().isParallel(piece3.getLine())) {
            return piece1;
        }
        throw new IllegalArgumentException("All pieces are parallel");
    }

    class VirtualWave {
        private static final int GENERATED_OFFSET = Integer.MIN_VALUE;
        private final ArrayList<org.tendiwa.geometry.Rectangle> virtualRectangles = new ArrayList<org.tendiwa.geometry.Rectangle>();
        private final RectangleablePiecesCollection pieces;
        public Comparator<RectangleSidePiece> PIECES_START_COORD_COMPARATOR = new Comparator<RectangleSidePiece>() {
            @Override
            public int compare(RectangleSidePiece piece1, RectangleSidePiece piece2) {
                return piece1.getSegment().getStartCoord() - piece2.getSegment()
                        .getStartCoord();
            }
        };
        private int curWidth = 0;
        private int curHeight = 0;
        private int curOffset = GENERATED_OFFSET;

        VirtualWave() {
            pieces = new RectangleablePiecesCollection(
                    freeSidePiecesOnPreviousWave);
            if (DEBUG) {
                pieces.createAndPlaceNewRectangle();
            } else {
                while (pieces.originalPieces.size() > 0) {
                    pieces.createAndPlaceNewRectangle();
                }
            }
            assert pieces.originalPieces.size() == 0;

        }

        /**
         * <p> Checks if two perpendicular pieces are close enough to be rectangleable. </p> <p> Meeting this condition
         * is not
         * enough to be able to placeIn a rectangle between these two pieces: you'd also need to ckeck whether there
         * are
         * pieces
         * that would intersect the resulting rectangle. </p>
         *
         * @param piece1
         * @param piece2
         * @return true if they are, false otherwise.
         */
        private boolean arePerpendicularRectangleable(RectangleSidePiece piece1, RectangleSidePiece piece2) {
            assert piece1 != null;
            assert piece2 != null;
            assert piece1.getDirection().isPerpendicular(piece2.getDirection());
            OrdinalDirection quadrantWhereRectnagleLies = (OrdinalDirection) Directions
                    .getDirectionBetween(piece1.getDirection(), piece2.getDirection());
            int squareSize = possibleRectangleWidth.max;
            org.tendiwa.geometry.Rectangle r = Recs.growFromIntersection(
                    piece1.getLine(),
                    piece2.getLine(),
                    quadrantWhereRectnagleLies,
                    squareSize,
                    squareSize);
            return r.touches(piece1) && r.touches(piece2);
        }

        private void draw(RectangleSidePiece shape) {
            canvas.draw(shape, DrawingRectangleSidePiece.withColor(Color.RED));
        }


        private org.tendiwa.geometry.Rectangle getRectangleBetween4Sides(RectangleSidePiece n, RectangleSidePiece e, RectangleSidePiece s, RectangleSidePiece w) {
            assert n != null && n.getDirection() == Directions.N;
            assert e != null && e.getDirection() == Directions.E;
            assert s != null && s.getDirection() == Directions.S;
            assert w != null && w.getDirection() == Directions.W;
            int x = e.getLine().getStaticCoordFromSide(Directions.E) + borderWidth;
            int y = s.getLine().getStaticCoordFromSide(Directions.S) + borderWidth;
            int width = w.getLine().distanceTo(e.getLine()) - borderWidth * 2;
            // TODO: What is not implemented?
            if (width <= 0) {
                throw new UnsupportedOperationException();
            }
            int height = s.getLine().distanceTo(n.getLine()) - borderWidth * 2;
            if (height <= 0) {
                throw new UnsupportedOperationException();
            }
            org.tendiwa.geometry.Rectangle rectangle = new org.tendiwa.geometry.Rectangle(x, y, width, height);
            pieces.modifySidesByPlacingRectangle(rectangle, n, e, s, w);
            return rectangle;
        }

        private org.tendiwa.geometry.Rectangle getRectangleBetween3Sides(RectangleSidePiece a, RectangleSidePiece b, RectangleSidePiece c) {
            assert a != b;
            assert b != c;
            assert a != c;
            assert a != null;
            assert b != null;
            assert c != null;
            RectangleSidePiece parallelPiece1;
            RectangleSidePiece parallelPiece2;
            RectangleSidePiece perpendicularPiece;
            if (a.getLine().isParallel(b.getLine())) {
                parallelPiece1 = a;
                parallelPiece2 = b;
                perpendicularPiece = c;
            } else if (a.getLine().isParallel(c.getLine())) {
                parallelPiece1 = a;
                parallelPiece2 = c;
                perpendicularPiece = b;
            } else {
                assert b.getLine().isParallel(c.getLine());
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
                            parallelPiece1.getDirection(),
                            perpendicularPiece.getDirection());
            org.tendiwa.geometry.Rectangle rectangle = new org.tendiwa.geometry.Rectangle(
                    Recs.growFromIntersection(
                            parallelPiece1.getLine(),
                            perpendicularPiece.getLine(),
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
            Cell point = parallelPiece.getSegment()
                    .getEndPoint(perpendicularPiece.getDirection().opposite());
            if (!perpendicularPiece.getLine().hasPointFromSide(
                    point,
                    perpendicularPiece.getDirection())) {
                return 0;
            }
            return perpendicularPiece.perpendicularDistanceTo(point);
        }

        public org.tendiwa.geometry.Rectangle getRectangleBetweenParallelSides(RectangleSidePiece piece1, RectangleSidePiece piece2) {
            assert piece1.getLine().getOrientation() == piece2.getLine().getOrientation();
            int minCoordEnd = Math.max(
                    piece1.getSegment().getStartCoord(),
                    piece2.getSegment().getStartCoord());
            int minCoord = minCoordEnd - possibleRectangleWidth.max + 1;
            int maxCoord = Math.min(
                    piece1.getSegment().getEndCoord(),
                    piece2.getSegment().getEndCoord()) + possibleRectangleWidth.max - 1;
            int endCoordsDistance = maxCoord - minCoord;
            assert minCoord < maxCoord;
            int maxStartCoord = minCoord + endCoordsDistance - possibleRectangleWidth.max + 1;
            int startVariableCoord = Chance.rand(minCoord, maxStartCoord);
            int randomizedDimension = Chance.rand(Math.max(
                    minCoordEnd - startVariableCoord + 1,
                    1), Math.min(
                    endCoordsDistance - startVariableCoord + minCoord + 1,
                    possibleRectangleWidth.max));
            int x, y, width, height;
            int startNonVariableCoord = Math
                    .min(
                            piece1.getLine().getStaticCoordFromSide(piece1.getDirection()) + piece1.getDirection()
                                    .getGrowing() * borderWidth,
                            piece2.getLine().getStaticCoordFromSide(piece2.getDirection()) + piece2.getDirection()
                                    .getGrowing() * borderWidth);
            if (curOffset != GENERATED_OFFSET) {
                startVariableCoord = minCoord + minCoordEnd - minCoord - (piece1.getLine().getOrientation()
                        .isHorizontal() ? curWidth : curHeight) + 1 + curOffset;
            }
            int distanceBetweenLines = piece1.getLine().distanceTo(piece2.getLine());
            if (piece1.getLine().getOrientation().isHorizontal()) {
                x = startVariableCoord;
                y = startNonVariableCoord;
                width = randomizedDimension;
                height = distanceBetweenLines;
            } else {
                assert piece1.getLine().getOrientation().isVertical();
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
            org.tendiwa.geometry.Rectangle rectangle = new org.tendiwa.geometry.Rectangle(x, y, width, height);
            pieces.modifySidesByPlacingRectangle(rectangle, piece1, piece2);
            return rectangle;
        }

        public org.tendiwa.geometry.Rectangle getRectangleOn1Side(RectangleSidePiece originalPiece) {
            assert originalPiece != null;
            int width = Chance.rand(possibleRectangleWidth);
            int height = Chance.rand(possibleRectangleWidth);
            int offset = Chance.rand(new Range(
                    -(originalPiece.getDirection().isVertical() ? width : height) + 1,
                    originalPiece.getSegment().getLength() - 1));
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
            org.tendiwa.geometry.Rectangle r = create(
                    originalPiece.createRectangle(1),
                    originalPiece.getDirection(),
                    width,
                    height,
                    offset);
            pieces.modifySidesByPlacingRectangle(r, originalPiece);
            return r;
        }

        public org.tendiwa.geometry.Rectangle getRectangleBetween2Sides(RectangleSidePiece piece1, RectangleSidePiece piece2) {
            OrdinalDirection direction = Directions.getDirectionBetween(
                    piece1.getDirection(),
                    piece2.getDirection());
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
                        vertical.getDirection().opposite()).getX() - vertical
                        .getSegment()
                        .getEndPoint(horizontal.getDirection().opposite()).getX());
            } else {
                minWidth = 0;
            }
            if (!dynamicCoordsRangeContainStaticCoord(vertical, horizontal)) {
                minHeight = Math
                        .abs(vertical.getSegment().getEndPoint(horizontal.getDirection()
                                .opposite()).getY() - horizontal.getSegment()
                                .getEndPoint(vertical.getDirection().opposite()).getY());
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
            org.tendiwa.geometry.Rectangle rectangle = new org.tendiwa.geometry.Rectangle(
                    Recs.growFromIntersection(
                            piece1.getLine(),
                            piece2.getLine(),
                            direction,
                            width,
                            height));
            pieces.modifySidesByPlacingRectangle(rectangle, piece1, piece2);
            return rectangle;
        }

        private boolean dynamicCoordsRangeContainStaticCoord(RectangleSidePiece dynamicCoordPiece, RectangleSidePiece staticCoordPiece) {
            return dynamicCoordPiece.getSegment().asRange().contains(
                    staticCoordPiece.getLine()
                            .getStaticCoordFromSide(staticCoordPiece.getDirection()
                                    .opposite()));
        }

        /**
         * Adds a new {@link org.tendiwa.geometry.Rectangle} to this {@link VirtualWave}. Adding EnhancedRectangles
         * occurs
         * after placing its {@link RectangleSidePiece}s into {@link RectangleablePiecesCollection}.
         *
         * @param r
         */
        private void addVirtualRectangle(org.tendiwa.geometry.Rectangle r) {
            virtualRectangles.add(r);
        }

        private boolean piecesAreParallelAndCloseEnough(RectangleSidePiece a, RectangleSidePiece b) {
            if (!a.getDirection().isOpposite(b.getDirection())) {
                // Find pieces whose directions are opposite...
                return false;
            }
            int distance = a.getLine().distanceTo(b.getLine());
            if (distance > possibleRectangleWidthPlus2BorderWidth.max || distance <= borderWidth * 2) {
                // ... and which are %possibleRectangleWidth% cells far from
                // each
                // other...
                return false;
            }
            if (!a.getLine().hasPointFromSide(
                    b.getSegment().getEndPoint(b.getDirection().clockwiseQuarter()),
                    a.getDirection())) {
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
            return distanceBetweenPiecesByDynamicCoord(piece1, piece2) < possibleRectangleWidth.max && !hasRectanglesBetweenNonDynIntersectingParallelPieces(
                    piece1,
                    piece2);
        }

        private boolean hasRectanglesBetweenNonDynIntersectingParallelPieces(RectangleSidePiece piece1, RectangleSidePiece piece2) {
            assert piece1.intersectionByDynamicCoord(piece2) == 0;
            assert piece1.getDirection().isOpposite(piece2.getDirection());
            RectangleSidePiece oneMockPiece, anotherMockPiece;
            RectangleSidePiece minPiece = piece1.getSegment().getEndCoord() < piece2.getSegment()
                    .getEndCoord() ? piece1 : piece2;
            RectangleSidePiece maxPiece = minPiece == piece1 ? piece2 : piece1;
            int mockMinStaticCoord = minPiece.getSegment().getEndCoord();
            int mockMaxStaticCoord = maxPiece.getSegment().getStartCoord();
            int minDynamicCoord = minPiece.getStaticCoordInFront();
            int maxDynamicCoord = maxPiece.getStaticCoordInFront();
            int minStatic = piece1.getLine()
                    .getStaticCoordFromSide(piece1.getDirection());
            int maxStatic = piece2.getLine()
                    .getStaticCoordFromSide(piece2.getDirection());
            if (minStatic > maxStatic) {
                int buf = minStatic;
                minStatic = maxStatic;
                maxStatic = buf;
            }
            Range rangeBetweenParallels = new Range(minStatic, maxStatic);
            if (piece1.isVertical()) {
                oneMockPiece = new RectangleSidePiece(
                        Directions.N,
                        minDynamicCoord,
                        mockMinStaticCoord,
                        1);
                anotherMockPiece = new RectangleSidePiece(
                        Directions.S,
                        maxDynamicCoord,
                        mockMaxStaticCoord,
                        1);
            } else {
                oneMockPiece = new RectangleSidePiece(
                        Directions.W,
                        mockMinStaticCoord,
                        minDynamicCoord,
                        1);
                anotherMockPiece = new RectangleSidePiece(
                        Directions.E,
                        mockMaxStaticCoord,
                        maxDynamicCoord,
                        1);
            }
            for (RectangleSidePiece startMockPiece : new RectangleSidePiece[]{
                    oneMockPiece, anotherMockPiece
            }) {
                TreeSet<RectangleSidePiece> treeSet = pieces.pieces
                        .get(startMockPiece.getDirection());
                RectangleSidePiece next = treeSet.lower(startMockPiece);
                while (next != null && next.getDirection().furthestCoordOf(
                        next.getLine().getStaticCoordFromSide(startMockPiece.getDirection()),
                        mockMaxStaticCoord) != mockMaxStaticCoord) {
                    if (next == null) {
                        break;
                    }
                    if (next.getSegment().asRange().overlaps(rangeBetweenParallels)) {
                        return true;
                    }
                    next = treeSet.lower(next);
                }
            }
            return false;
        }

        private int distanceBetweenPiecesByDynamicCoord(RectangleSidePiece piece1, RectangleSidePiece piece2) {
            assert piece1.intersectionByDynamicCoord(piece2) == 0;
            return Math.max(
                    piece1.getSegment().getStartCoord(),
                    piece2.getSegment().getStartCoord()) - Math.min(
                    piece1.getSegment().getEndCoord(),
                    piece2.getSegment().getEndCoord());

        }

        class RectangleablePiecesCollection {
            private static final int MIN_RECTANGLE_DIMENSION = 1;
            /**
             * Pieces that form previous front.
             */
            private final Collection<RectangleSidePiece> originalPieces = new HashSet<RectangleSidePiece>();
            private Map<CardinalDirection, TreeSet<RectangleSidePiece>> pieces = new HashMap<CardinalDirection, TreeSet<RectangleSidePiece>>();

            /**
             * @param originalPieces
             *         Pieces that form the previous front. Algorithm will done new rectangles that touch these pieces.
             */
            private RectangleablePiecesCollection(Collection<RectangleSidePiece> originalPieces) {
                this.originalPieces.addAll(originalPieces);
                for (CardinalDirection direction : CardinalDirection.values()) {
                    pieces.put(direction, new TreeSet<RectangleSidePiece>(
                            PIECES_COMPARATOR));
                }
                for (RectangleSidePiece piece : originalPieces) {
                    pieces.get(piece.getDirection()).add(piece);
                }
            }

            public void createAndPlaceNewRectangle() {

                RectangleSidePiece randomOriginalPiece;
                if (DEBUG) {
                    randomOriginalPiece = getPieces(W, 6)[0];
                } else {
                    randomOriginalPiece = Utils
                            .getRandomElement(originalPieces);
                }

                Map<CardinalDirection, Collection<RectangleSidePiece>> dirsToPieces = new HashMap<CardinalDirection, Collection<RectangleSidePiece>>();
                Collection<RectangleSidePiece> parallelPieces = getAllParallelRectangleablePiecesForPiece(randomOriginalPiece);
                Collection<RectangleSidePiece> perpendicularPieces = getAllPerpendicularRectangleablePiecesForPiece(randomOriginalPiece);
                Collection<RectangleSidePiece> copyParallel = Lists.newArrayList(parallelPieces);
                Collection<RectangleSidePiece> copyPerpendicular = Lists.newArrayList(perpendicularPieces);
                dirsToPieces.put(
                        randomOriginalPiece.getDirection().opposite(),
                        parallelPieces);
                dirsToPieces.put(
                        randomOriginalPiece.getDirection().clockwiseQuarter(),
                        new ArrayList<RectangleSidePiece>());
                dirsToPieces.put(
                        randomOriginalPiece.getDirection().counterClockwiseQuarter(),
                        new ArrayList<RectangleSidePiece>());

                for (RectangleSidePiece piece : perpendicularPieces) {
                    dirsToPieces.get(piece.getDirection()).add(piece);
                }
                for (CardinalDirection dir : new ArrayList<CardinalDirection>(
                        dirsToPieces.keySet())) {
                    if (dirsToPieces.get(dir).isEmpty()) {
                        dirsToPieces.remove(dir);
                    }
                }
                for (RectangleSidePiece perpendicularPiece : perpendicularPieces) {
                    // TODO: Don't get a collection on each iteration.
                    dirsToPieces.get(perpendicularPiece.getDirection()).add(
                            perpendicularPiece);
                }
                // TODO: Let chance of selecting particular piece not depend on
                // amount of pieces from that side.
                ArrayList<RectangleSidePiece> selectedPieces = new ArrayList<RectangleSidePiece>();
                selectedPieces.add(randomOriginalPiece);

                // Select second rectangleable piece. It is more trivial than
                // selecting third and fourth pieces, because we already have a
                // Map all pieces in which are rectangleable with the
                // initialPiece.
                if (!dirsToPieces.isEmpty()) {
                    RectangleSidePiece selectedPiece = selectRandomPiece(dirsToPieces);
                    selectedPieces.add(selectedPiece);
                    dirsToPieces.remove(selectedPiece.getDirection());
                }

                // Select third and fourth rectangleable pieces
                while (!dirsToPieces.isEmpty()) {
                    RectangleSidePiece selectedPiece = selectRandomPiece(dirsToPieces);
                    // RectangleSidePiece selectedPiece = getPieces(S, 40)[0];
                    RectangleSidePiece[] selectedPiecesArray = selectedPieces
                            .toArray(new RectangleSidePiece[selectedPieces.size()]);
                    if (selectedPiecesArray.length == 2) {
                        RectangleSidePiece secondPiece = selectedPiecesArray[1];
                        if (!areasInFrontOfPiecesIntersect(
                                secondPiece,
                                selectedPiece)) {
                            removePieceFromRectangleable(
                                    selectedPiece,
                                    dirsToPieces);
                            continue;
                        }
                        if (!arePiecesRectangleable(selectedPiece, secondPiece)) {
                            removePieceFromRectangleable(
                                    selectedPiece,
                                    dirsToPieces);
                            continue;
                        }
                    }
                    if (selectedPiecesArray.length == 3) {
                        RectangleSidePiece secondPiece = selectedPiecesArray[1];
                        RectangleSidePiece thirdPiece = selectedPiecesArray[2];
                        if (!areasInFrontOfPiecesIntersect(
                                secondPiece,
                                selectedPiece) || !areasInFrontOfPiecesIntersect(
                                thirdPiece,
                                selectedPiece)) {
                            removePieceFromRectangleable(
                                    selectedPiece,
                                    dirsToPieces);
                            continue;
                        }
                        if (!arePiecesRectangleable(selectedPiece, secondPiece)) {
                            removePieceFromRectangleable(
                                    selectedPiece,
                                    dirsToPieces);
                            continue;
                        }
                        if (!arePiecesRectangleable(selectedPiece, thirdPiece)) {
                            removePieceFromRectangleable(
                                    selectedPiece,
                                    dirsToPieces);
                            continue;
                        }
                    }
                    // Create a new temporary array with old pieces plus new
                    // piece for storing arguments.
                    RectangleSidePiece[] selectedPiecesWithNew = Arrays.copyOf(
                            selectedPiecesArray,
                            selectedPiecesArray.length + 1);
                    selectedPiecesWithNew[selectedPiecesArray.length] = selectedPiece;
                    if (doPiecesExistThatIntersectRectangle(
                            selectedPiece,
                            getMinimumRectangle(selectedPiecesWithNew))) {
                        removePieceFromRectangleable(
                                selectedPiece,
                                dirsToPieces);
                        continue;
                    }
                    selectedPieces.add(selectedPiece);
                    dirsToPieces.remove(selectedPiece.getDirection());
                }
                org.tendiwa.geometry.Rectangle r;
                switch (selectedPieces.size()) {
                    case 1:
                        r = getRectangleOn1Side(selectedPieces.get(0));
                        break;
                    case 2:
                        RectangleSidePiece piece1 = selectedPieces.get(0);
                        RectangleSidePiece piece2 = selectedPieces.get(1);
                        if (piece1.getDirection().isOpposite(piece2.getDirection())) {
                            r = getRectangleBetweenParallelSides(piece1, piece2);
                        } else {
                            r = getRectangleBetween2Sides(piece1, piece2);
                        }
                        break;
                    case 3:
                        r = getRectangleBetween3Sides(
                                selectedPieces.get(0),
                                selectedPieces.get(1),
                                selectedPieces.get(2));
                        break;
                    case 4:
                    default:
                        HashMap<CardinalDirection, RectangleSidePiece> dirToPiece = new HashMap<CardinalDirection, RectangleSidePiece>();
                        for (RectangleSidePiece piece : selectedPieces) {
                            dirToPiece.put(piece.getDirection(), piece);
                        }
                        r = getRectangleBetween4Sides(
                                dirToPiece.get(N),
                                dirToPiece.get(E),
                                dirToPiece.get(S),
                                dirToPiece.get(W));

                }
                assert r.getWidth() <= possibleRectangleWidthPlus2BorderWidth.max;
                assert r.getHeight() <= possibleRectangleWidthPlus2BorderWidth.max;
                addVirtualRectangle(r);
            }

            private boolean arePiecesRectangleable(RectangleSidePiece selectedPiece, RectangleSidePiece secondPiece) {
                if (selectedPiece.getDirection()
                        .isPerpendicular(secondPiece.getDirection())) {
                    if (!arePerpendicularRectangleable(
                            selectedPiece,
                            secondPiece)) {
                        return false;
                    }
                } else {
                    if (!piecesAreParallelAndCloseEnough(
                            selectedPiece,
                            secondPiece)) {
                        return false;
                    }
                }
                return true;
            }

            private void removePieceFromRectangleable(RectangleSidePiece selectedPiece, Map<CardinalDirection, Collection<RectangleSidePiece>> dirsToPieces) {
                Collection<RectangleSidePiece> thatSidePieces = dirsToPieces
                        .get(selectedPiece.getDirection());
                thatSidePieces.remove(selectedPiece);
                if (thatSidePieces.isEmpty()) {
                    dirsToPieces.remove(selectedPiece.getDirection());
                }
            }

            private boolean areasInFrontOfPiecesIntersect(RectangleSidePiece piece1, RectangleSidePiece piece2) {
                if (piece1.getDirection().isPerpendicular(piece2.getDirection())) {
                    return arePerpendicularRectangleable(piece1, piece2);
                } else {
                    return true;
                }
            }

            /**
             * Returns a random piece from map.
             *
             * @param dirsToPieces
             *         A Map that connects pieces to their cardinal directions.
             * @return A random piece from map.
             */
            private RectangleSidePiece selectRandomPiece(Map<CardinalDirection, Collection<RectangleSidePiece>> dirsToPieces) {
                CardinalDirection randomDirection = Utils
                        .getRandomElement(dirsToPieces.keySet());
                return Utils
                        .getRandomElement(dirsToPieces.get(randomDirection));
            }

            private Collection<RectangleSidePiece> getAllPieces() {
                Collection<RectangleSidePiece> answer = Lists.newArrayList();
                for (CardinalDirection dir : CARDINAL_DIRECTIONS) {
                    answer.addAll(pieces.get(dir));
                }
                return answer;
            }

            private RectangleSidePiece[] getPieces(CardinalDirection dir, int width) {
                Collection<RectangleSidePiece> answer = new ArrayList<RectangleSidePiece>();
                assert !pieces.isEmpty();
                for (RectangleSidePiece piece : pieces.get(dir)) {
                    if (piece.getSegment().getLength() == width) {
                        answer.add(piece);
                    }
                }
                return answer.toArray(new RectangleSidePiece[answer.size()]);
            }

            private Collection<RectangleSidePiece> getAllPerpendicularRectangleablePiecesForPiece(RectangleSidePiece piece) {
                RectangleSidePiece[] minPossibles = getMinPossiblePerpendicularPieces(piece);
                Collection<RectangleSidePiece> answer = new HashSet<RectangleSidePiece>();
                for (RectangleSidePiece mockMinPiece : minPossibles) {
                    TreeSet<RectangleSidePiece> treeSet = pieces
                            .get(mockMinPiece.getDirection());

                    for (RectangleSidePiece nextPiece = treeSet
                            .lower(mockMinPiece); nextPiece != null; nextPiece = treeSet
                            .lower(nextPiece)) {
                        if (!arePerpendicularRectangleable(nextPiece, piece)) {
                            continue;
                        }
                        // TODO: Seems like operations with these two variables
                        // are useless
                        boolean isFullyInFrontOf = piece.getLine().hasPointFromSide(
                                nextPiece.getSegment().getEndPoint(piece.getDirection()
                                        .opposite()),
                                piece.getDirection());
                        boolean isInSegmentByStaticCoord = piece.getSegment()
                                .asRange()
                                .contains(
                                        nextPiece.getLine()
                                                .getStaticCoordFromSide(nextPiece.getDirection()));
                        if (!isFullyInFrontOf && isInSegmentByStaticCoord) {
                            continue;
                        } else {
                            org.tendiwa.geometry.Rectangle rectangle = getMinimumRectangle(
                                    piece,
                                    nextPiece);
                            if (doPiecesExistThatIntersectRectangle(
                                    piece,
                                    rectangle) || doPiecesExistThatIntersectRectangle(
                                    nextPiece,
                                    rectangle)) {
                                continue;
                            }
                        }
                        answer.add(nextPiece);
                    }
                }
                return answer;
            }

            private Collection<RectangleSidePiece> getAllParallelRectangleablePiecesForPiece(RectangleSidePiece piece) {
                Collection<RectangleSidePiece> answer = new ArrayList<RectangleSidePiece>();
                RectangleSidePiece inversePiece = inversePiece(piece);
                TreeSet<RectangleSidePiece> treeSet = pieces
                        .get(inversePiece.getDirection());

                for (RectangleSidePiece parallelPiece = treeSet
                        .lower(inversePiece); parallelPiece != null && parallelPiece
                        .distanceTo(piece) <= possibleRectangleWidthPlus2BorderWidth.max; parallelPiece = treeSet
                        .lower(parallelPiece)) {
                    if (!piecesAreParallelAndCloseEnough(piece, parallelPiece)) {
                        continue;
                    }
                    if (isParallelPieceCoveredByOtherPieces(
                            piece,
                            parallelPiece)) {
                        continue;
                    }
                    answer.add(parallelPiece);
                }
                return answer;
            }

            private boolean isParallelPieceCoveredByOtherPieces(RectangleSidePiece piece, RectangleSidePiece parallelPiece) {
                assert piece.getDirection().isOpposite(parallelPiece.getDirection());
                // TODO: Change to code that doesn't create new Range objects
                if (piece.getSegment().asRange().overlaps(
                        parallelPiece.getSegment().asRange())) {
                    return isParallelPieceInFrontCoveredByOtherPieces(
                            piece,
                            parallelPiece);
                } else {
                    return isParallelPieceNotInFrontBotheredByOtherPieces(
                            piece,
                            parallelPiece);
                }
            }

            /**
             * Checks if there are pieces that bother placing rectangle in junction between two parallel pieces whose
             * dynamic
             * ranges don't intersect.
             *
             * @param piece
             *         A piece parallel to {@code parallelPiece} whose dynamic range doesn't intersect that piece
             *         range.
             * @param parallelPiece
             * @return
             */
            private boolean isParallelPieceNotInFrontBotheredByOtherPieces(RectangleSidePiece piece, RectangleSidePiece parallelPiece) {
                assert piece.getDirection().isOpposite(parallelPiece.getDirection());
                assert !piece.getSegment().asRange().overlaps(
                        parallelPiece.getSegment().asRange());
                RectangleSidePiece minStartCoordRange, maxStartCoordRange;
                if (piece.getSegment().getStartCoord() < parallelPiece.getSegment()
                        .getStartCoord()) {
                    minStartCoordRange = piece;
                    maxStartCoordRange = parallelPiece;
                } else {
                    minStartCoordRange = parallelPiece;
                    maxStartCoordRange = piece;
                }
                int dMin = minStartCoordRange.getSegment().getEndCoord();
                int sMin = minStartCoordRange.getStaticCoordInFront();
                int dMax = maxStartCoordRange.getSegment().getStartCoord();
                int sMax = maxStartCoordRange.getStaticCoordInFront();
                if (sMin > sMax) {
                    int buf = sMin;
                    sMin = sMax;
                    sMax = buf;
                }
                int xMin, xMax, yMin, yMax;
                if (piece.isVertical()) {
                    xMin = sMin;
                    xMax = sMax;
                    yMin = dMin;
                    yMax = dMax;
                } else {
                    xMin = dMin;
                    xMax = dMax;
                    yMin = sMin;
                    yMax = sMax;
                }
                org.tendiwa.geometry.Rectangle rectangle = Recs
                        .rectangleByMinAndMaxCoords(xMin, yMin, xMax, yMax);
                return doPiecesExistThatIntersectRectangle(piece, rectangle) || doPerpendicularPiecesExistThatIntersectRectangle(
                        piece,
                        rectangle);
            }

            /**
             * Checks if other pieces between {@code piece} and {@code parallelPiece} bother placing a rectangle
             * between
             * those two
             * pieces, that is, no rectangle can be placed between those two pieces.
             *
             * @param piece
             *         Order of arguments doesn't matter.
             * @param parallelPiece
             * @return
             */
            private boolean isParallelPieceInFrontCoveredByOtherPieces(RectangleSidePiece piece, RectangleSidePiece parallelPiece) {
                // TODO: Maybe pieces could be remembered to calculate coverage
                // of next pieces.
                assert piece.getDirection().isOpposite(parallelPiece.getDirection());
                assert piece.getSegment().asRange().overlaps(
                        parallelPiece.getSegment().asRange());
                Collection<RectangleSidePiece> sortedCoveringPieces = getAllPiecesIntersectingSpaceBetweenParallelPieces(
                        piece,
                        parallelPiece);
                if (sortedCoveringPieces.isEmpty()) {
                    return false;
                }
                RectangleSidePiece firstPiece = sortedCoveringPieces
                        .iterator()
                        .next();
                int maxDynamicCoord = firstPiece.getSegment().getEndCoord();
                int minDynamicCoord = firstPiece.getSegment().getStartCoord();
                Range dynamicRange = getDynamicRangeOfParallels(
                        piece,
                        parallelPiece);
                if (minDynamicCoord > dynamicRange.min) {
                    return false;
                }
                for (RectangleSidePiece p : sortedCoveringPieces) {
                    if (p.getSegment().getStartCoord() > maxDynamicCoord + 1) {
                        return false;
                    } else {
                        maxDynamicCoord = Math.max(
                                maxDynamicCoord,
                                p.getSegment().getEndCoord());
                    }
                }
                if (maxDynamicCoord < dynamicRange.max) {
                    return false;
                }
                return true;
            }

            private Range getDynamicRangeOfParallels(RectangleSidePiece piece1, RectangleSidePiece piece2) {
                assert piece1.getDirection().isOpposite(piece2.getDirection());
                // TODO: Change to code that doesn't create new Range objects
                return piece1.getSegment().asRange().intersection(
                        piece2.getSegment().asRange());
            }

            private Range[] getStaticAndDynamicRangeForRectangleInFront(RectangleSidePiece nextPiece, RectangleSidePiece piece) {
                int staticMin = nextPiece.getSegment().getEndCoord();
                int staticMax = piece.getLine()
                        .getStaticCoordFromSide(piece.getDirection());
                assert staticMin != staticMax;
                if (staticMin > staticMax) {
                    int buf = staticMin;
                    staticMin = staticMax;
                    staticMax = buf;
                }
                Range staticRange = new Range(staticMin, staticMax);
                int dynamicStart = nextPiece.getLine()
                        .getStaticCoordFromSide(nextPiece.getDirection());
                int dynamicMin, dynamicMax;
                if (nextPiece.getDirection().isGrowing()) {
                    dynamicMin = dynamicStart;
                    dynamicMax = dynamicStart + borderWidth * 2 + MIN_RECTANGLE_DIMENSION;
                } else {
                    dynamicMax = dynamicStart;
                    dynamicMin = dynamicStart - borderWidth * 2 - MIN_RECTANGLE_DIMENSION;
                }
                Range dynamicRange = new Range(dynamicMin, dynamicMax);
                return new Range[]{
                        staticRange, dynamicRange
                };
            }

            private Range[] getStaticAndDynamicRangeForLateralRectangle(RectangleSidePiece nextPiece, RectangleSidePiece piece) {
                int staticMin, staticMax;
                if (nextPiece.getSegment().asRange().contains(
                        piece.getSegment().getStaticCoord())) {
                    staticMin = staticMax = piece.getStaticCoordInFront();
                } else {
                    staticMin = nextPiece.getSegment().getEndCoord();
                    staticMax = piece.getLine()
                            .getStaticCoordFromSide(piece.getDirection());
                    assert staticMin != staticMax;
                    if (staticMin > staticMax) {
                        int buf = staticMin;
                        staticMin = staticMax;
                        staticMax = buf;
                    }
                }
                int dynamicStart = nextPiece.getLine()
                        .getStaticCoordFromSide(nextPiece.getDirection());
                int dynamicMin, dynamicMax;
                int perpendicularDistance = nextPiece
                        .perpendicularDistanceTo(piece.getSegment()
                                .getEndPoint(nextPiece.getDirection().opposite()));
                if (nextPiece.getDirection().isGrowing()) {
                    dynamicMin = dynamicStart;
                    dynamicMax = dynamicStart + perpendicularDistance;
                } else {
                    dynamicMax = dynamicStart;
                    dynamicMin = dynamicStart - perpendicularDistance;
                }
                return new Range[]{
                        new Range(staticMin, staticMax),
                        new Range(dynamicMin, dynamicMax)
                };
            }

            /**
             * Returns a minimal possible rectange that must not intersect any pieces in order for argument pieces to
             * be
             * rectangleable.
             *
             * @param pieces
             * @return
             */
            private org.tendiwa.geometry.Rectangle getMinimumRectangle(RectangleSidePiece... pieces) {
                Map<CardinalDirection, RectangleSidePiece> directionToPiece = new HashMap<CardinalDirection, RectangleSidePiece>();
                for (RectangleSidePiece piece : pieces) {
                    directionToPiece.put(piece.getDirection().opposite(), piece);
                }
                Map<CardinalDirection, Integer> limitCoordFromSide = new HashMap<CardinalDirection, Integer>();
                for (CardinalDirection dir : Directions.CARDINAL_DIRECTIONS) {
                    if (directionToPiece.containsKey(dir)) {
                        limitCoordFromSide.put(dir, directionToPiece
                                .get(dir)
                                .getStaticCoordInFront());
                    } else {
                        limitCoordFromSide.put(
                                dir,
                                minCoordInDirection(dir, pieces));
                    }
                }
                int x = limitCoordFromSide.get(Directions.W);
                int y = limitCoordFromSide.get(Directions.N);
                int width = limitCoordFromSide.get(Directions.E) - x + 1;
                int height = limitCoordFromSide.get(Directions.S) - y + 1;
                return new org.tendiwa.geometry.Rectangle(x, y, width, height);
            }

            /**
             * Returns coordinate on x or y axis which limits position of a side of a rectangle between pieces.
             *
             * @param dir
             *         A side of rectangle that will be limited by the resulting coord.
             * @param pieces
             *         Pieces that form a junction where a rectangle will be placed.
             * @return
             */
            private int minCoordInDirection(CardinalDirection dir, RectangleSidePiece[] pieces) {
                int minCoord = dir.closestCoordOf(
                        Integer.MAX_VALUE,
                        Integer.MIN_VALUE);
                for (RectangleSidePiece piece : pieces) {
                    if (piece.getDirection() == dir) {
                        minCoord = dir.furthestCoordOf(
                                minCoord,
                                piece.getStaticCoordInFront());
                    } else {
                        assert piece.getDirection().isPerpendicular(dir);
                        minCoord = dir.furthestCoordOf(minCoord, piece.getSegment()
                                .getEndPoint(dir.opposite())
                                .getDynamicCoord(dir.getOrientation()));
                    }
                }
                assert minCoord != Integer.MAX_VALUE && minCoord != Integer.MIN_VALUE;
                return minCoord;
            }

            private TreeSet<RectangleSidePiece> getAllPiecesIntersectingSpaceBetweenParallelPieces(RectangleSidePiece piece1, RectangleSidePiece piece2) {
                Range dynamicRange = piece1.getSegment().asRange().intersection(
                        piece2.getSegment().asRange());
                TreeSet<RectangleSidePiece> answer = new TreeSet<RectangleSidePiece>(
                        PIECES_START_COORD_COMPARATOR);
                TreeSet<RectangleSidePiece> treeSet = pieces
                        .get(piece1.getDirection());
                RectangleSidePiece nextPiece = treeSet.higher(piece1);
                int staticMax = piece2.getSegment().getStaticCoord();
                while (nextPiece != null && nextPiece.getDirection()
                        .furthestCoordOf(
                                nextPiece.getStaticCoordInFront(),
                                staticMax) == staticMax) {
                    if (Range.overlap(
                            nextPiece.getSegment().getStartCoord(),
                            nextPiece.getSegment().getEndCoord(),
                            dynamicRange.min,
                            dynamicRange.max)) {
                        answer.add(nextPiece);
                    }
                    nextPiece = treeSet.higher(nextPiece);
                }
                return answer;
            }

            /**
             * Checks if there are pieces that intersect a given rectangle.
             *
             * @param minPiece
             *         Marks a starting point for searching pieces.
             * @param rectangle
             *         A rectangle in front of {@code minPiece}
             * @return true if at least one such piece was found, false if none of them were found.
             */
            private boolean doPiecesExistThatIntersectRectangle(RectangleSidePiece minPiece, org.tendiwa.geometry.Rectangle rectangle) {
                // TODO: Change rectangle object to ints
                Range staticRange, dynamicRange;
                if (minPiece.isVertical()) {
                    dynamicRange = new Range(
                            rectangle.getY(),
                            rectangle.getY() + rectangle.getHeight() - 1);
                    staticRange = new Range(
                            rectangle.getX(),
                            rectangle.getX() + rectangle.getWidth() - 1);
                } else {
                    dynamicRange = new Range(
                            rectangle.getX(),
                            rectangle.getX() + rectangle.getWidth() - 1);
                    staticRange = new Range(
                            rectangle.getY(),
                            rectangle.getY() + rectangle.getHeight() - 1);
                }
                // TODO: Probably need only 1 run (without inversePiece) if
                // isInSegmentByStaticCoord is true.

                // Check parallel pieces
                for (RectangleSidePiece startPiece : new RectangleSidePiece[]{
                        minPiece, inversePiece(minPiece)
                }) {
                    TreeSet<RectangleSidePiece> treeSet = pieces
                            .get(startPiece.getDirection());
                    RectangleSidePiece nextPiece;
                    if (startPiece == minPiece) {
                        nextPiece = treeSet.higher(startPiece);
                    } else {
                        nextPiece = treeSet.lower(startPiece);
                    }
                    while (nextPiece != null && nextPiece
                            .distanceTo(startPiece) <= staticRange.getLength()) {
                        if (staticRange.contains(nextPiece.getSegment()
                                .getStaticCoord())) {
                            if (nextPiece.getSegment().asRange().overlaps(
                                    dynamicRange)) {
                                return true;
                            }
                        }
                        if (minPiece.getDirection() == nextPiece.getDirection()) {
                            nextPiece = treeSet.higher(nextPiece);
                        } else {
                            nextPiece = treeSet.lower(nextPiece);
                        }
                    }
                }
                return false;
            }

            private boolean doPerpendicularPiecesExistThatIntersectRectangle(RectangleSidePiece minPiece, org.tendiwa.geometry.Rectangle rectangle) {
                RectangleSidePiece[] minPossiblePerpendicularPieces = getMinPossiblePerpendicularPieces(minPiece);
                int recStartCoord = minPiece.isVertical() ? rectangle.getY() : rectangle.getX();
                CardinalDirection dirOfMinPossiblePiece;
                if (minPiece.getSegment().asRange().contains(recStartCoord)) {
                    if (minPiece.getDirection().isVertical()) {
                        dirOfMinPossiblePiece = E;
                    } else {
                        dirOfMinPossiblePiece = S;
                    }
                } else {
                    if (minPiece.getDirection().isVertical()) {
                        dirOfMinPossiblePiece = W;
                    } else {
                        dirOfMinPossiblePiece = N;
                    }
                }
                RectangleSidePiece startPiece = null;
                for (RectangleSidePiece minPossiblePiece : minPossiblePerpendicularPieces) {
                    if (minPossiblePiece.getDirection() == dirOfMinPossiblePiece) {
                        startPiece = minPossiblePiece;
                        break;
                    }
                }
                assert startPiece != null;
                startPiece = inversePiece(startPiece);
                startPiece = movePieceOneCellToItsDirection(startPiece);
                TreeSet<RectangleSidePiece> treeSet = pieces
                        .get(startPiece.getDirection());
                RectangleSidePiece nextPiece = treeSet.lower(startPiece);
                int maxDynamicCoord = rectangle
                        .getSideAsSidePiece(startPiece.getDirection().opposite()).getSegment()
                        .getStaticCoord();

                // TODO: Change to code that doesn't create new Range objects.
                Range staticRange;
                if (minPiece.getDirection().isVertical()) {
                    staticRange = new Range(
                            rectangle.getY(),
                            rectangle.getY() + rectangle.getHeight() - 1);
                } else {
                    staticRange = new Range(
                            rectangle.getX(),
                            rectangle.getX() + rectangle.getWidth() - 1);
                }
                while (nextPiece != null && nextPiece.getDirection().closestCoordOf(
                        nextPiece.getSegment().getStaticCoord(),
                        maxDynamicCoord) == maxDynamicCoord) {
                    if (nextPiece.getSegment().asRange().overlaps(staticRange)) {
                        return true;
                    }
                    nextPiece = treeSet.lower(nextPiece);
                }
                return false;
            }

            private RectangleSidePiece movePieceOneCellToItsDirection(RectangleSidePiece piece) {
                int x = piece.getSegment().getX();
                int y = piece.getSegment().getY();
                if (piece.getDirection().isVertical()) {
                    y += piece.getDirection().getGrowing();
                } else {
                    x += piece.getDirection().getGrowing();
                }
                return new RectangleSidePiece(piece.getDirection(), x, y, 1);
            }

            /**
             * <ul> <li>Splits each of {@code pieces} with a corresponding opposite side of Rectangle {@code r}</li>
             * <li>Removes
             * the pieces which were split from this {@link RectangleablePiecesCollection}</li> <li>Adds newly created
             * split
             * pieces to this {@link RectangleablePiecesCollection}</li> <li>Disassociates the removed pieces with
             * their
             * junctions</li> </ul>
             *
             * @param r
             * @param pieces
             */
            private void modifySidesByPlacingRectangle(org.tendiwa.geometry.Rectangle r, RectangleSidePiece... pieces) {
                Collection<CardinalDirection> directionsUnused = Sets
                        .newHashSet(CardinalDirection.values());
                // Place pieces for those sides that have neighbor pieces from
                // previous front.
                for (RectangleSidePiece piece : pieces) {
                    assert Sets
                            .newHashSet(this.pieces.get(piece.getDirection()))
                            .contains(piece);
                }
                for (RectangleSidePiece piece : pieces) {
                    RectangleSidePiece rPiece = r
                            .getSideAsSidePiece(piece.getDirection().opposite());
                    RectangleSidePiece[] splitPieces = piece
                            .splitWithPiece(rPiece);
                    TreeSet<RectangleSidePiece> treeSet = this.pieces
                            .get(piece.getDirection());

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
                        removePiece(touchingPiece);
                        for (RectangleSidePiece splitTouchingPiece : touchingPiece
                                .splitWithPiece(rPiece)) {
                            assert touchingPiece != null;
                            if (isOriginal) {
                                this.originalPieces.add(splitTouchingPiece);
                            }
                            treeSet.add(splitTouchingPiece);
                        }
                    }

                    directionsUnused.remove(rPiece.getDirection());
                    for (RectangleSidePiece rSplitPiece : rPiece
                            .splitWithPieces(touchingPieces)) {
                        this.pieces.get(rSplitPiece.getDirection()).add(rSplitPiece);
                    }
                    assertRectangleNotIntersectsWithOthers(r);
                }

                // Place pieces from other sides of the new rectangle
                for (CardinalDirection dir : directionsUnused) {
                    RectangleSidePiece piece = r.getSideAsSidePiece(dir);
                    this.pieces.get(piece.getDirection()).add(piece);
                }
                canvas.draw(r, DRAWING_RECTANGLE);
            }

            private boolean isOriginal(RectangleSidePiece piece) {
                return originalPieces.contains(piece);
            }

            /**
             * Returns a set of pieces all of which touch piece {@code rPiece}.
             *
             * @param rPiece
             *         The piece that touches some other pieces.
             * @param modifiedPiece
             *         One piece that is known to touch rPiece.
             * @return A set of pieces touching {@code rPiece} including {@code modifiedPiece}.
             */
            private ImmutableSet<RectangleSidePiece> getPiecesThatTouch(RectangleSidePiece rPiece, RectangleSidePiece modifiedPiece) {
                CardinalDirection originalsDirection = rPiece.getDirection()
                        .opposite();
                Builder<RectangleSidePiece> builder = ImmutableSet
                        .<RectangleSidePiece>builder();
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

            /**
             * Tests that all pieces present in {@code junctions} are present in {@code pieces}
             */
            private void assertRectangleNotIntersectsWithOthers(org.tendiwa.geometry.Rectangle newRectangle) {
                for (org.tendiwa.geometry.Rectangle existingRectangle : virtualRectangles) {
                    assert !newRectangle.intersects(existingRectangle);
                }
                for (org.tendiwa.geometry.Rectangle rec : wave.get(0)) {
                    assert !newRectangle.intersects(rec);
                }
            }

            private void removePiece(RectangleSidePiece piece) {
                pieces.get(piece.getDirection()).remove(piece);
            }

            /**
             * Remove a piece from originals. This is necessary when a new rectangle overlays an original piece.
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
             * <p> Checks if two pieces can be joined into one. They can, if they: <ol> <li>Face the same
             * direction</li>
             * <li>Lie
             * on equal lines</li> <li>There is amount of cells between their closest ends equal to {@link
             * RectangleSystem#borderWidth}. </ol> </p>
             *
             * @param newPiece
             * @return true if pieces are joinable, false if they aren't.
             */
            private RectangleSidePiece getParallelRectangleablePiece(RectangleSidePiece newPiece) {
                TreeSet<RectangleSidePiece> treeSet = pieces
                        .get(newPiece.getDirection().opposite());
                RectangleSidePiece opposite = inversePiece(newPiece);
                do {
                    opposite = treeSet.lower(opposite);
                } while (opposite != null && !piecesAreParallelAndCloseEnough(
                        opposite,
                        newPiece));
                return opposite;
            }

            /**
             * Disassociates a piece with a junction containing it. Each piece is a part of only one junction, so no
             * need to
             * provide a junction as an argument here.
             *
             * @param newPiece
             * @param dir
             */
            private PerpendicularPiecesPair getPerpendicularRectangleablePieces(RectangleSidePiece newPiece, CardinalDirection dir) {
                assert newPiece != null;
                PerpendicularPiecesPair answer = new PerpendicularPiecesPair();
                // TODO: There's no need to hold Rectangle in minPossible*
                // to compute it; need a more limited class for this purpose.
                RectangleSidePiece[] minPossibles = getMinPossiblePerpendicularPieces(newPiece);
                for (RectangleSidePiece minPiece : minPossibles) {
                    TreeSet<RectangleSidePiece> treeSetOfMin = pieces
                            .get(minPiece.getDirection());
                    RectangleSidePiece nextPiece = minPiece;
                    do {
                        nextPiece = treeSetOfMin.lower(nextPiece);
                    } while (nextPiece != null && !arePerpendicularRectangleable(
                            nextPiece,
                            newPiece));
                    if (nextPiece != null) {
                        answer.add(nextPiece);
                    }
                }
                return answer;
            }

            /**
             * Creates two virtual {@link RectangleSidePiece}s (not really belonging to any Rectangle) which will be
             * used as
             * starting points to find perpendicular RectangleSidePieces in a junction with {@code piece}.
             *
             * @param piece
             * @return An array of 2 RectangleSidePieces.
             */
            private RectangleSidePiece[] getMinPossiblePerpendicularPieces(RectangleSidePiece piece) {
                RectangleSidePiece[] answer = new RectangleSidePiece[2];
                if (piece.isVertical()) {
                    Cell startingPointN = piece.getSegment()
                            .getEndPoint(Directions.S);
                    Cell startingPointS = piece.getSegment()
                            .getEndPoint(Directions.N);
                    answer[0] = new RectangleSidePiece(
                            Directions.S,
                            piece.getLine().getStaticCoordFromSide(piece.getDirection()),
                            startingPointN.getY(),
                            1);
                    answer[1] = new RectangleSidePiece(
                            Directions.N,
                            piece.getLine().getStaticCoordFromSide(piece.getDirection()),
                            startingPointS.getY(),
                            1);
                } else {
                    Cell startingPointW = piece.getSegment()
                            .getEndPoint(Directions.E);
                    Cell startingPointE = piece.getSegment()
                            .getEndPoint(Directions.W);
                    answer[0] = new RectangleSidePiece(
                            Directions.W,
                            startingPointE.getX(),
                            piece.getLine().getStaticCoordFromSide(piece.getDirection()),
                            1);
                    answer[1] = new RectangleSidePiece(
                            Directions.E,
                            startingPointW.getX(),
                            piece.getLine().getStaticCoordFromSide(piece.getDirection()),
                            1);
                }
                return answer;
            }

            class PerpendicularPiecesPair implements
                    Iterable<RectangleSidePiece> {
                protected RectangleSidePiece lesser;
                protected RectangleSidePiece greater;

                protected void add(RectangleSidePiece piece) {
                    if (piece.getDirection().isGrowing()) {
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
                 * Returns lesser piece of this pair if {@code pieceFromAnotherPair} is lesser in {@code anotherPair} .
                 * Returns
                 * greater piece of this pair if {@code pieceFromAnotherPair} is greater in {@code anotherPair}.
                 * Returns
                 * null if this
                 * pair doesn't have a piece of the same greatness.
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
                            throw new UnsupportedOperationException();
                        }
                    };
                }
            }

            class SameLinePiecesPair extends PerpendicularPiecesPair {
                public SameLinePiecesPair(RectangleSidePiece splitterPiece, RectangleSidePiece[] pieces) {
                    for (RectangleSidePiece splitPiece : pieces) {
                        if (splitPiece.getSegment().getStartCoord() < splitterPiece.getSegment()
                                .getStartCoord()) {
                            lesser = splitPiece;
                        } else if (splitPiece.getSegment().getStartCoord() > splitterPiece.getSegment()
                                .getStartCoord()) {
                            greater = splitPiece;
                        } else {
                            assert false;
                        }
                    }
                }

            }
        }
    }
}
