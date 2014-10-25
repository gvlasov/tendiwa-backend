package org.tendiwa.geometry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Directions;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.core.meta.Utils;

import java.util.*;

/**
 * <p> RectangleSystem is one of the most basic yet powerful concepts of terrain generation. Basically, it is a graph
 * where vertices are rectangles. There is an edge between two vertices if these rectangles touch each other's sides.
 * RectangleSystem provides methods </p> <p>RectangleSystem differs from RectangleSequence in that System always
 * remembers what rectangles are neighbours and what rectangles are outer (don't have any neighbors from some
 * side).</p>
 */
public class RectangleSystem extends RectangleSequence {
	private static final Comparator<Rectangle> COMPARATOR_HORIZONTAL = new RectangleComparator(Orientation.HORIZONTAL);
	private static final Comparator<Rectangle> COMPARATOR_VERTICAL = new RectangleComparator(Orientation.VERTICAL);
	public static Comparator<Rectangle> horizontalRectangleComparator = new Comparator<Rectangle>() {
		@Override
		public int compare(Rectangle r1, Rectangle r2) {
			if (r1.getX() > r2.getX()) {
				return 1;
			}
			if (r1.getX() < r2.getX()) {
				return -1;
			}
			return 0;
		}
	};
	public static Comparator<Rectangle> verticalRectangleComparator = new Comparator<Rectangle>() {
		@Override
		public int compare(Rectangle r1, Rectangle r2) {
			if (r1.getY() > r2.getY()) {
				return 1;
			}
			if (r1.getY() < r2.getY()) {
				return -1;
			}
			return 0;
		}
	};
	private final Map<Orientation, TreeSet<Rectangle>> sortedRectangles;
	/**
	 * Amount of cells between two neighbor rectangles. RectangleSystem must obey this rule: to successfully done it,
	 * neighbor rectangles must be %borderWidth% cells away from each other.
	 */
	protected int borderWidth;
	/**
	 * The main part of a RectangleSystem — a graph that depicts connections between neighbor EnhancedRectangles.
	 */
	protected SimpleGraph<Rectangle, Neighborship> graph;
	private OuterSegments outerSegments = new OuterSegments();

	public RectangleSystem(int borderWidth) {
		super();
		this.borderWidth = borderWidth;
		graph = new SimpleGraph<>(Neighborship.class);
		sortedRectangles = new HashMap<>();
		sortedRectangles.put(Orientation.HORIZONTAL, new TreeSet<Rectangle>(COMPARATOR_HORIZONTAL));
		sortedRectangles.put(Orientation.VERTICAL, new TreeSet<Rectangle>(COMPARATOR_VERTICAL));
	}

	/**
	 * Checks if two {@link Rectangle}s have exactly {@code amount} cells between their closest sides, and these
	 * sides overlap by dynamic coordinate. Such rectangles are considered to be "near" in this individual
	 * RectangleSystem.
	 *
	 * @param r1
	 * 	A rectangle from this rectangle system.
	 * @param r2
	 * 	Another rectangle from this rectangle system.
	 * @param amount
	 * 	Expected amount of cells
	 * @return true if distance between rectangles is {@code cells}, false otherwise
	 * @see RectangleSystem#areRectanglesNear(Rectangle, Rectangle)
	 * @see RectangleSystem#areRectanglesUnited(Rectangle, Rectangle)
	 */
	static boolean areRectanglesInXCells(Rectangle r1, Rectangle r2, int amount) {
		if (r1.getX() + r1.getWidth() + amount == r2.getX() || r2.getX() + r2.getWidth() + amount == r1.getX()) {
			// Rectangles share a vertical line
			int a1 = r1.getY();
			int a2 = r1.getY() + r1.getHeight() - 1;
			int b1 = r2.getY();
			int b2 = r2.getY() + r2.getHeight() - 1;
			int intersection = Utils.integersRangeIntersection(a1, a2, b1, b2);
			return intersection >= 1;
		} else if (r1.getY() + r1.getHeight() + amount == r2.getY() || r2.getY() + r2.getHeight() + amount == r1.getY()) {
			// Rectangles share a horizontal line
			int a1 = r1.getX();
			int a2 = r1.getX() + r1.getWidth() - 1;
			int b1 = r2.getX();
			int b2 = r2.getX() + r2.getWidth() - 1;
			int intersection = Utils.integersRangeIntersection(a1, a2, b1, b2);
			return intersection >= 1;
		} else {
			// Rectangles definitely don't share horizontal or vertical lines
			return false;
		}
	}

	public Graph<Rectangle, Neighborship> getGraph() {
		return graph;
	}

	public ImmutableMap<Rectangle, Collection<CardinalDirection>> getOuterSides() {
		return outerSegments.getAllOuterSides();
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	/**
	 * Returns a rectangle from this RectangleSystem that contains a particular cell.
	 *
	 * @param x
	 * 	X-coordinate of a cell
	 * @param y
	 * 	Y-coordinate of a cell
	 * @return A rectangle that contains cell [x:y].
	 */
	public Rectangle findRectangleByCell(int x, int y) {
		for (Rectangle r : graph.vertexSet()) {
			if (r.contains(x, y)) {
				return r;
			}
		}
		throw new RuntimeException(
			"There is no rectangle that contains point {" + x + ":" + y + "}");
	}

	/**
	 * Finds out from which {@link CardinalDirection} is a rectangle located relatively to another Rectangle.
	 *
	 * @param rectangle
	 * 	One rectangle
	 * @param neighbor
	 * 	Another rectangle
	 * @return Side from which neighbor is located relatively to a rectangle.
	 */
	CardinalDirection getNeighborSide(Rectangle rectangle, Rectangle neighbor) {
		if (rectangle.getY() == neighbor.getY() + neighbor.getHeight() + borderWidth) {
			return CardinalDirection.N;
		}
		if (rectangle.getX() + rectangle.getWidth() + borderWidth == neighbor.getX()) {
			return CardinalDirection.E;
		}
		if (rectangle.getY() + rectangle.getHeight() + borderWidth == neighbor.getY()) {
			return CardinalDirection.S;
		}
		if (rectangle.getX() == neighbor.getX() + neighbor.getWidth() + borderWidth) {
			return CardinalDirection.W;
		}
		throw new RuntimeException(
			"Cannot find direction of neighbor rectangle " + neighbor + " for rectangle " + rectangle);
	}

	/**
	 * Returns a set of {@link org.tendiwa.geometry.Segment}s that this system forms with its outer rectangle.
	 *
	 * @param r
	 * 	A rectangle to find free segments of.
	 * @param side
	 * 	A side of that rectangle.
	 * @return A set of all such segments (a zero-length one if a rectangle is not outer)
	 */
	public ImmutableSet<Segment> getSegmentsFreeFromNeighbors(Rectangle r, CardinalDirection side) {
		ArrayList<Rectangle> rectanglesFromThatSide = new ArrayList<>(getRectanglesCloseToSide(r, side));
		// Sort neighbors from that direction from top to bottom or from left to right
		if (side == CardinalDirection.N || side == CardinalDirection.S) {
			Collections.sort(
				rectanglesFromThatSide,
				horizontalRectangleComparator);
		} else {
			Collections.sort(
				rectanglesFromThatSide,
				verticalRectangleComparator);
		}
		ArrayList<Segment> segments = new ArrayList<>();
		// We start from a single segment which fills the whole direction of
		// rectangle r.
		if (side == CardinalDirection.N) {
			segments.add(new Segment(
				r.getX(),
				r.getY(),
				r.getWidth(),
				Orientation.HORIZONTAL
			));
		} else if (side == CardinalDirection.E) {
			segments.add(new Segment(
				r.getX() + r.getWidth() - 1,
				r.getY(),
				r.getHeight(),
				Orientation.VERTICAL
			));
		} else if (side == Directions.S) {
			segments.add(new Segment(
				r.getX(),
				r.getY() + r.getHeight() - 1,
				r.getWidth(),
				Orientation.HORIZONTAL));
		} else {
			// if (direction == DirectionOldSide.W)
			segments.add(new Segment(r.getX(), r.getY(), r.getHeight(), Orientation.VERTICAL));
		}
		int splitSegmentStartCoord, splitSegmentLength;
		// For each neighbor from that direction, we split our initial segment
		// with
		// another segment that is a direction of a neighbor rectangle that
		// touches
		// our initial segment.
		for (int i = 0, l = rectanglesFromThatSide.size(); i < l; i++) {
			Rectangle neighbor = rectanglesFromThatSide.get(i);
			switch (side) {
				case N:
					splitSegmentStartCoord = neighbor.getX() - borderWidth;
					splitSegmentLength = neighbor.getWidth() + borderWidth * 2;
					break;
				case E:
					splitSegmentStartCoord = neighbor.getY() - borderWidth;
					splitSegmentLength = neighbor.getHeight() + borderWidth * 2;
					break;
				case S:
					splitSegmentStartCoord = neighbor.getX() - borderWidth;
					splitSegmentLength = neighbor.getWidth() + borderWidth * 2;
					break;
				case W:
				default:
					splitSegmentStartCoord = neighbor.getY() - borderWidth;
					splitSegmentLength = neighbor.getHeight() + borderWidth * 2;
			}
			// Now, there may be a situation when the whole direction segment is
			// eliminated, and there are still rectangles from that direction.
			// If number of segments here reaches 0, it means that this
			// direction of Rectangle has no neighbor-free segments. Now we
			// can safely break the loop and go straight to the "return" part.
			if (segments.size() == 0) {
				break;
			}
			// Consecutively splitting the last segment with a neighbor
			// rectangle direction segment, we get several segments. That is why
			// we sorted all the neighbors — otherwise we would have to compare
			// each neighbor to each segment on each step.
			Segment[] newSegments = segments
				.get(segments.size() - 1)
				.splitWithSegment(splitSegmentStartCoord, splitSegmentLength);
			// If the segment was split (meaning there is at least one new
			// segment), then the former segment is removed...
			segments.remove(segments.size() - 1);
			// ...and new segments are added (2, 1 or 0 segments can be the
			// result of splitting in different situations).
			if (newSegments[0] != null) {
				segments.add(newSegments[0]);
			}
			if (newSegments[1] != null) {
				segments.add(newSegments[1]);
			}
		}
		// segments.remove(null);
		return ImmutableSet.copyOf(segments);
	}

	public ImmutableSet<RectangleSidePiece> getSidePiecesFreeFromNeighbours(Rectangle r, CardinalDirection side) {
		ImmutableSet<Segment> segmentsFreeFromNeighbors = getSegmentsFreeFromNeighbors(r, side);
		Builder<RectangleSidePiece> answer = ImmutableSet.builder();
		for (Segment segment : segmentsFreeFromNeighbors) {
			// TODO: When Segment will be immutablized, create a constructor
			// that uses an existing segment.
			answer.add(new RectangleSidePiece(
				side,
				segment.getX(),
				segment.getY(),
				segment.getLength()));
		}
		return answer.build();
	}

	/**
	 * Finds neighbors from that direction and also rectangles that touch argument's direction only with their borders.
	 *
	 * @param r
	 * 	A rectangle to find neighbors of.
	 * @param side
	 * 	A side of that rectangle.
	 * @return A set of rectangles that touch a given rectangle
	 * @see RectangleSystem#getNeighborsFromSide(Rectangle, CardinalDirection)
	 */
	Set<Rectangle> getRectanglesCloseToSide(Rectangle r, CardinalDirection side) {
		Set<Rectangle> rectanglesFromThatSide = new HashSet<>();
		// TODO: Add somewhere examples of such rectangles as in comment below.
		/*
		 * Not only neighbors can shorten free segments, but also the rectangles
		 * that touch this rectangle only with their border can shorten free
		 * segments too. That's why we check all the rectangles, and not only
		 * the neighbors.
		 */
		if (side == CardinalDirection.N) {
			for (Rectangle neighbor : content) {
				/*
				 * The part starting from Utils.integersRangeIntersection in
				 * each case checks if a neighbor rectangle touches _side_ (not
				 * border!) of the rectangle r with its direction _or_ border.
				 */
				if (neighbor.getY() + neighbor.getHeight() + borderWidth == r.getY() && Utils
					.integersRangeIntersection(
						neighbor.getX() - borderWidth,
						neighbor.getX() + neighbor.getWidth() - 1 + borderWidth,
						r.getX(),
						r.getX() + r.getWidth() - 1) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}

		} else if (side == CardinalDirection.E) {
			for (Rectangle neighbor : content) {
				if (neighbor.getX() == r.getX() + r.getWidth() + borderWidth && Utils
					.integersRangeIntersection(
						neighbor.getY() - borderWidth,
						neighbor.getY() + neighbor.getHeight() - 1 + borderWidth,
						r.getY(),
						r.getY() + r.getHeight() - 1) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		} else if (side == CardinalDirection.S) {
			for (Rectangle neighbor : content) {
				if (neighbor.getY() == r.getY() + r.getHeight() + borderWidth && Utils
					.integersRangeIntersection(
						neighbor.getX() - borderWidth,
						neighbor.getX() + neighbor.getWidth() - 1 + borderWidth,
						r.getX(),
						r.getX() + r.getWidth() - 1) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		} else {
			// if (direction == SideTest.W)
			for (Rectangle neighbor : content) {
				if (neighbor.getX() + neighbor.getWidth() + borderWidth == r.getX() && Utils
					.integersRangeIntersection(
						neighbor.getY() - borderWidth,
						neighbor.getY() + neighbor.getHeight() - 1 + borderWidth,
						r.getY(),
						r.getY() + r.getHeight() - 1) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		}
		return rectanglesFromThatSide;
	}

	/**
	 * Finds neighbors of a rectangle from a given direction and also rectangles that touch argument's direction <b>or
	 * border</b> only with their borders.
	 *
	 * @param r
	 * 	A rectangle from this rectangle system.
	 * @param side
	 * 	A side of that rectangle.
	 * @return A set of rectangles that touch {@code r}.
	 * @see RectangleSystem#getRectanglesCloseToSide(Rectangle, CardinalDirection)
	 * @see RectangleSystem#getNeighborsFromSide(Rectangle, CardinalDirection)
	 */
	public Set<Rectangle> getRectanglesCloseToSideOrBorder(Rectangle r, CardinalDirection side) {
		Set<Rectangle> rectanglesFromThatSide = new HashSet<>();
		// TODO: Add somewhere examples of such rectangles as in comment below.
	    /*
	     * Not only neighbors can shorten free segments, but also the rectangles
		 * that touch this rectangle only with their border can
		 * too. That's why we check all the rectangles, and not only
		 * the neighbors.
		 */
		if (side == CardinalDirection.N) {
			for (Rectangle neighbor : content) {
		        /*
		         * The part starting from Utils.integersRangeIntersection in
				 * each case checks if a neighbor rectangle touches _side_ (not
				 * border!) of the rectangle r with its direction _or_ border.
				 */
				if (neighbor.getY() + neighbor.getHeight() + borderWidth == r.getY() && Utils
					.integersRangeIntersection(
						neighbor.getX() - borderWidth,
						neighbor.getX() + neighbor.getWidth() - 1 + borderWidth,
						r.getX() - borderWidth,
						r.getX() + r.getWidth() - 1 + borderWidth) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}

		} else if (side == CardinalDirection.E) {
			for (Rectangle neighbor : content) {
				if (
					neighbor.getX() == r.getX() + r.getWidth() + borderWidth
						&& Utils.integersRangeIntersection(
						neighbor.getY() - borderWidth,
						neighbor.getY() + neighbor.getHeight() - 1 + borderWidth,
						r.getY() - borderWidth,
						r.getY() + r.getHeight() - 1 + borderWidth
					) > 0
					) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		} else if (side == CardinalDirection.S) {
			for (Rectangle neighbor : content) {
				if (neighbor.getY() == r.getY() + r.getHeight() + borderWidth && Utils
					.integersRangeIntersection(
						neighbor.getX() - borderWidth,
						neighbor.getX() + neighbor.getWidth() - 1 + borderWidth,
						r.getX() - borderWidth,
						r.getX() + r.getWidth() - 1 + borderWidth) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		} else {
			// if (direction == SideTest.W)
			for (Rectangle neighbor : content) {
				if (neighbor.getX() + neighbor.getWidth() + borderWidth == r.getX() && Utils
					.integersRangeIntersection(
						neighbor.getY() - borderWidth,
						neighbor.getY() + neighbor.getHeight() - 1 + borderWidth,
						r.getY() - borderWidth,
						r.getY() + r.getHeight() - 1 + borderWidth) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		}
		return rectanglesFromThatSide;
	}

	/**
	 * Returns all rectangles that are currently outer, that is they have one or more sides not fully occupied with
	 * neighbors. This set is not a view, that is it is not modified when the RectangleSystem's outer rectangles are
	 * modified.
	 *
	 * @return A set of outer rectangles.
	 */
	public Set<Rectangle> outerRectanglesSet() {
		return outerSegments.getOuterRectangles();
	}

	/**
	 * Returns a set of EnhancedRectangles that touch a given Rectangle from a particular direction.
	 *
	 * @param r
	 * 	A Rectangle to seek neighbors of.
	 * @param side
	 * 	From which direction to seek for neighbors.
	 * @return All neighbors from that direction.
	 */
	public Set<Rectangle> getNeighborsFromSide(Rectangle r, CardinalDirection side) {
		Builder<Rectangle> builder = ImmutableSet.builder();
		assert graph.containsVertex(r);
		for (Neighborship e : graph.edgesOf(r)) {
			if (graph.getEdgeSource(e).equals(r) && getNeighborSide(
				r,
				graph.getEdgeTarget(e)) == side) {
				builder.add(graph.getEdgeTarget(e));
			} else if (graph.getEdgeTarget(e).equals(r) && getNeighborSide(
				r,
				graph.getEdgeSource(e)) == side) {
				builder.add(graph.getEdgeSource(e));
			}
		}
		return builder.build();
	}

	/**
	 * Searches for neighbors in graph.
	 *
	 * @param r
	 * 	A rectangle from this rectangle system.
	 * @return A set of all neighbors of that rectangle.
	 */
	public ImmutableSet<Rectangle> getNeighbors(Rectangle r) {
		Builder<Rectangle> neighbors = ImmutableSet.builder();
		for (Neighborship e : graph.edgesOf(r)) {
			if (graph.getEdgeSource(e) == r) {
				neighbors.add(graph.getEdgeTarget(e));
			} else if (graph.getEdgeTarget(e) == r) {
				neighbors.add(graph.getEdgeSource(e));
			}
		}
		return neighbors.build();
	}

	/**
	 * Returns a segment inside Rectangle r1 by which r1 touches r2.
	 *
	 * @param r1
	 * 	A rectangle from this rectangle system.
	 * @param r2
	 * 	Another rectangle from this rectangle system.
	 * @return A segment that lies inside r1 close to its borders, and is located in front of r2.
	 */
	Segment getAdjacencySegment(Rectangle r1, Rectangle r2) {
		if (!areRectanglesNear(r1, r2)) {
			throw new IllegalArgumentException(
				"Both rectangles must be near each other: " + r1 + " " + r2);
		}
		CardinalDirection side = getNeighborSide(r1, r2);
		switch (side) {
			case N:
				return new Segment(Math.max(r1.getX(), r2.getX()), r1.getY(), Math.min(
					r1.getX() + r1.getWidth() - r2.getX(),
					r2.getX() + r2.getWidth() - r1.getX()), Orientation.HORIZONTAL);
			case E:
				return new Segment(
					r1.getX() + r1.getWidth() - 1,
					Math.max(r1.getY(), r2.getY()),
					Math.min(r1.getY() + r1.getHeight() - r2.getY(), r2.getY() + r2.getHeight() - r2.getY()),
					Orientation.VERTICAL);
			case S:
				return new Segment(
					Math.max(r1.getX(), r2.getX()),
					r1.getY() + r1.getHeight() - 1,
					Math.min(r1.getX() + r1.getWidth() - r2.getX(), r2.getX() + r2.getWidth() - r1.getX()),
					Orientation.HORIZONTAL);
			case W:
			default:
				return new Segment(r1.getX(), Math.max(r1.getY(), r2.getY()), Math.min(
					r1.getY() + r1.getHeight() - r2.getY(),
					r2.getY() + r2.getHeight() - r1.getY()), Orientation.VERTICAL);
		}
	}

	@Override
	public Rectangle addRectangle(Rectangle r) {
		super.addRectangle(r);
		graph.addVertex(r);
		buildEdgesWith(r);
		return r;
	}

	private void buildEdgesWith(Rectangle r) {
		for (Orientation orientation : Orientation.values()) {
			TreeSet<Rectangle> treeSet = sortedRectangles.get(orientation);
			ArrayList<Map<Rectangle, CardinalDirection>> neighbors = findNeighborsInSortedSet(r, orientation);
			for (Map.Entry<Rectangle, CardinalDirection> e : neighbors.get(0).entrySet()) {
				addEdgeBetween(r, e.getKey(), e.getValue(), Neighborship.NeighborshipType.NEIGHBORSHIP);
			}
			for (Map.Entry<Rectangle, CardinalDirection> e : neighbors.get(1).entrySet()) {
				addEdgeBetween(r, e.getKey(), e.getValue(), Neighborship.NeighborshipType.UNION);
			}
			// Final value of ammunitionType is 2
//		System.out.println("Neighbours of "+shortDef(r)+" "+orientation+" are "+neighbors.keySet().stream().map(e -> shortDef(e)).collect(Collectors.toList()));
			treeSet.add(r);
		}
	}

	private String shortDef(Rectangle r) {
		CardinalDirection dir1 = r.getY() == 0 ? CardinalDirection.N : CardinalDirection.S;
		CardinalDirection dir2 = r.getX() == 0 ? CardinalDirection.W : CardinalDirection.E;
		return "[" + dir1 + " " + dir2 + " " + r.getWidth() + " " + r.getHeight() + "]";
	}

	/**
	 * Creates an edge between two rectangles. Created edge object defines which of two rectangles touch with which
	 * borders.
	 *
	 * @param r1
	 * 	Source vertex
	 * @param r2
	 * 	Destination vertex
	 * @param sourceRecOccupiedSide
	 * 	Which side of source rectangle is occupied by destination rectangle.
	 */
	private void addEdgeBetween(Rectangle r1, Rectangle r2, CardinalDirection sourceRecOccupiedSide, Neighborship.NeighborshipType type) {
		graph.addEdge(r1, r2, new Neighborship(sourceRecOccupiedSide, type));
	}

	/**
	 * Comuptes and returns both "near" and "united" neighbors of a newly added rectangle.
	 *
	 * @param r
	 * 	The original rectangle — the one we search for neighbors of.
	 * @param orientation
	 * 	Neighbors from which side of original rectangle are being found. If rectangles are supposed to touch N and S
	 * 	sides,
	 * 	then orientation is VERTICAL, else it is HORIZONTAL.
	 * @return List of two maps from a neighbor rectangle to a direction it is from original rectangle. First index of
	 * the
	 * list contains neighbors of ammunitionType Near, second index contains neighbors of ammunitionType United.
	 */
	private ArrayList<Map<Rectangle, CardinalDirection>> findNeighborsInSortedSet(Rectangle r, Orientation orientation) {
		TreeSet<Rectangle> treeSet = sortedRectangles.get(orientation);
		ArrayList<Map<Rectangle, CardinalDirection>> answer = new ArrayList<>();
		Map<Rectangle, CardinalDirection> nears = new HashMap<>();
		Map<Rectangle, CardinalDirection> uniteds = new HashMap<>();
		answer.add(nears);
		answer.add(uniteds);
		int distance = 0;
		CardinalDirection decreasingSide = orientation.isHorizontal() ? CardinalDirection.W : CardinalDirection.N;
		CardinalDirection increasingSide = orientation.isHorizontal() ? CardinalDirection.E : CardinalDirection.S;
		for (CardinalDirection dir : new CardinalDirection[]{decreasingSide, increasingSide}) {
			Rectangle bufferRectangle;
			for (bufferRectangle = dir == decreasingSide ? treeSet.lower(r) : treeSet.higher(r);
				 bufferRectangle != null && distance <= borderWidth;
				 bufferRectangle = dir == decreasingSide ? treeSet.lower(bufferRectangle) : treeSet.higher(bufferRectangle)
				) {
				if (!areProbableNeighborsByOrientation(r, bufferRectangle, orientation)) {
					assert !areRectanglesNear(r, bufferRectangle) || getOrientationOfTouching(r, bufferRectangle) != orientation;
//				System.out.println(shortDef(bufferRectangle)+" is not neighbour 1");
					continue;
				}
				distance = bufferRectangle.amountOfCellsBetween(r, orientation);
				if (r.overlapsByDynamicRange(bufferRectangle, orientation)) {
					if (distance == borderWidth) {
						// Rectangles will have LocationNeighborship of type Near
						nears.put(bufferRectangle, dir);
					} else if (distance == 0) {
						// Rectangles will have LocationNeighborship of type United
						uniteds.put(bufferRectangle, dir);
					}
				} else {
//				System.out.println(shortDef(bufferRectangle)+" is not neighbour 2");
					assert !areRectanglesNear(r, bufferRectangle);
				}
			}
//		System.out.println("Last distance "+distance+", last object "+bufferRectangle);
		}
		return answer;
	}

	private boolean areProbableNeighborsByOrientation(Rectangle r1, Rectangle r2, Orientation orientation) {
		if (!areRectanglesNear(r1, r2) && !areRectanglesUnited(r1, r2)) {
			return false;
		} else if (r1.overlapsByDynamicRange(r2, orientation.reverted())) {
			return false;
		}
		return true;
	}

	private Orientation getOrientationOfTouching(Rectangle r1, Rectangle r2) {
		assert areRectanglesNear(r1, r2);
		return getNeighborSide(r1, r2).getOrientation();
	}

	/**
	 * Excludes a rectangle from this system. Neighbor rectangles of the excluded rectangle will become outer.
	 *
	 * @param r
	 * 	A Rectangle that exists in this RectangleSystem.
	 */
	@Override
	public void excludeRectangle(Rectangle r) {
		super.excludeRectangle(r);
		for (Rectangle neighbor : getNeighbors(r)) {
			assert graph.containsEdge(neighbor, r);
			outerSegments.uncomputeSide(neighbor, getNeighborSide(neighbor, r));
		}
		outerSegments.uncomputeRectangle(r);
		graph.removeVertex(r);
		for (Set<Rectangle> set : sortedRectangles.values()) {
			set.remove(r);
		}
	}

	/**
	 * Checks if a rectangle is one of the outer rectangles.
	 *
	 * @param r
	 * 	A rectangle from this rectangle system.
	 * @return true if {@code r} is outer, false if it is inner.
	 * @see OuterSegments
	 */
	public boolean isRectangleOuter(Rectangle r) {
		return outerSegments.getOuterSidesOf(r).size() > 0;
	}

	/**
	 * Checks if two {@link Rectangle}s have exactly {@code borderWidth} cells between their closest sides, and
	 * these sides overlap by dynamic coordinate. Such rectangles are considered to be "near" in this individual
	 * RectangleSystem.
	 *
	 * @param r1
	 * 	A rectangle from this rectangle system.
	 * @param r2
	 * 	Another rectangle from this rectangle system.
	 * @return true if distance between rectangles is {@code cells}, false otherwise
	 * @see RectangleSystem#areRectanglesUnited(Rectangle, Rectangle)
	 */
	public boolean areRectanglesNear(Rectangle r1, Rectangle r2) {
		return areRectanglesInXCells(r1, r2, borderWidth);
	}

	/**
	 * Checks if two {@link Rectangle}s have exactly {@code borderWidth} cells between their closest sides, and
	 * these sides overlap by dynamic coordinate. Such rectangles are considered to be "near" in this individual
	 * RectangleSystem.
	 *
	 * @param r1
	 * 	A rectangle from this rectangle system.
	 * @param r2
	 * 	Another rectangle from this rectangle system.
	 * @return true if distance between rectangles is {@code cells}, false otherwise
	 * @see RectangleSystem#areRectanglesNear(Rectangle, Rectangle)
	 */
	private boolean areRectanglesUnited(Rectangle r1, Rectangle r2) {
		return areRectanglesInXCells(r1, r2, 0);
	}

	/**
	 * Checks if a Rectangle exists in this RectangleSystem.
	 *
	 * @param r
	 * 	A rectangle.
	 * @return True if {@code r} exists in this rectangle system, false otherwise.
	 */
	public boolean hasRectangle(Rectangle r) {
		return content.contains(r);
	}

	/**
	 * Make this Rectangle's graph into a directed tree. The shape of a tree will be determined randomly, but a root
	 * vertex can be set.
	 */
	public void convertGraphToDirectedTree() {
		// Randomly select an existing vertex to be a root vertex of a tree
		Rectangle rootVertex = null;
		for (Rectangle r : graph.vertexSet()) {
			rootVertex = r;
			break;
		}
	    /*
	     * Another graph that will contain the same vertices that this.graph,
		 * but with single tree-form edges.
		 */
		SimpleGraph<Rectangle, Neighborship> graph2 = new SimpleGraph<>(Neighborship.class);
		graph2.addVertex(rootVertex);
		// Vertex set of the new graph
		Set<Rectangle> newVertexSet = graph2.vertexSet();
		for (int i = 0, l = graph.vertexSet().size(); i < l; i++) {
			loop:
			for (Rectangle r : newVertexSet) {// Select a random
				for (Neighborship e : graph.edgesOf(r)) {
					Rectangle r2 = graph.getEdgeTarget(e);
					if (r2 == r) {
						r2 = graph.getEdgeSource(e);
					}
					if (!newVertexSet.contains(r2)) {
				        /*
				         * If there is at least one edge coming from this vertex
						 * in the old graph, use this vertex further.
						 */

						graph2.addVertex(r2);
						graph2.addEdge(r, r2);
						break loop;
					}
					// Else continue searching
				}
			}
		}
		graph = graph2;
	}

	/**
	 * <p> Finds all double edges and makes them single. There is no way to predict which vertex will be the sources or
	 * the
	 * targets of the remaining edges. </p>
	 */
	public void convertDoubleEdgesToSingle() {
		for (Neighborship edge : graph.edgeSet()) {
			if (graph.containsEdge(
				graph.getEdgeTarget(edge),
				graph.getEdgeSource(edge))) {
				graph.removeEdge(edge);
			}
		}
	}

	/**
	 * Randomly removes outer EnhancedRectangles. Iterates over all outer rectangles with a {@code chance} percent
	 * chance to
	 * remove each of them.
	 *
	 * @param depth
	 * 	How many runs to make.
	 * @param chance
	 * 	A chance to remove.
	 */
	public void nibbleSystem(int depth, int chance) {
		for (int k = 0; k < depth; k++) {
			Set<Rectangle> removedRectangles = new HashSet<>();
			for (Rectangle r : outerRectanglesSet()) {
				if (Chance.roll(chance)) {
					removedRectangles.add(r);
				}
			}
			for (Rectangle r : removedRectangles) {
				excludeRectangle(r);
			}
		}
	}

	/**
	 * Removes all EnhancedRectangles from this RectangleSystem that are located inside a particular circle.
	 *
	 * @param x
	 * 	X-coordinate of circle's center.
	 * @param y
	 * 	Y-coordinate of circle's center.
	 * @param radius
	 * 	Radius of a circle.
	 * @return A Set of removed rectangles.
	 */
	public Set<Rectangle> removeRectanglesInCircle(int x, int y, int radius) {
		Set<Rectangle> answer = new HashSet<>();
		Set<Rectangle> copy = new HashSet<>(content);
		for (Rectangle r : copy) {
			if (r.isInCircle(x, y, radius)) {
				excludeRectangle(r);
				answer.add(r);
			}
		}
		return answer;
	}

	/**
	 * Splits rectangle into two rectangles, one of them being the initial rectangle, and another one a new rectangle.
	 * Rectangle under current number will be the left one (if dir == DirectionToBeRemoved.V) or the top one (if dir ==
	 * DirectionToBERemoved.H).
	 * <p>
	 * If width < 0, then a rectangle width width/height = -width from right side/bottom will be cut off, and under
	 * current
	 * number still stay right/bottom rectangle, but the old one will still be the left/top one, and the returned one
	 * will
	 * be the right/bottom one.
	 *
	 * @param r
	 * 	A rectangle from this RectangleSystem.
	 * @param orientation
	 * 	Horizontally or vertically.
	 * @param widthOrHeight
	 * 	How much to cut.
	 * @return A new rectangle that was created by splitting the old one.
	 */
	public Rectangle splitRectangle(Rectangle r, Orientation orientation, int widthOrHeight, boolean reverseAreas) {
		// TODO: Optimize size() calls
		if (widthOrHeight == 0) {
			throw new IllegalArgumentException("Argument width can't be 0");
		}
		if (!hasRectangle(r)) {
			throw new IllegalArgumentException("Rectangle " + r + " doesn't exist in this RectangleSystem");
		}
		boolean negativeWidth = widthOrHeight < 0;
		Rectangle newRec;
		if (orientation.isVertical()) {
			// Vertically
			if (negativeWidth) {
				// This will be the width of the old Rectangle
				widthOrHeight = r.getWidth() + widthOrHeight - borderWidth;
			}
			if (widthOrHeight > r.getWidth()) {
				throw new IllegalArgumentException(
					"Width " + widthOrHeight + " in vertical splitting is too big");
			}
			if (widthOrHeight < 1) {
				widthOrHeight = widthOrHeight + borderWidth - r.getWidth();
				throw new IllegalArgumentException(
					"Width " + widthOrHeight + " in vertical splitting is too big");
			}
			int newStartX = r.getX() + widthOrHeight + borderWidth;
			if (reverseAreas) {
				newRec = new Rectangle(r.getX(), r.getY(), widthOrHeight, r.getHeight());
				resizeRectangle(r,
					newStartX,
					r.getY(),
					r.getWidth() - widthOrHeight - borderWidth,
					r.getHeight());
			} else {
				newRec = new Rectangle(
					newStartX,
					r.getY(),
					r.getWidth() - widthOrHeight - borderWidth,
					r.getHeight());
				resizeRectangle(r, widthOrHeight, r.getHeight());
			}
		} else {
			// Horizontally
			if (negativeWidth) {
				// This will be the width of the old Rectangle
				widthOrHeight = r.getHeight() + widthOrHeight - borderWidth;
			}
			if (widthOrHeight > r.getHeight()) {
				throw new IllegalArgumentException(
					"Width " + widthOrHeight + " in horizontal splitting is too big");
			}
			if (widthOrHeight < 1) {
				widthOrHeight = widthOrHeight + borderWidth - r.getHeight();
				throw new IllegalArgumentException(
					"Width " + widthOrHeight + " in horizontal splitting is too big");
			}
			int newStartY = r.getY() + widthOrHeight + borderWidth;
			// Though argument is called width, it is height if a rectangle
			// is split vertically
			if (reverseAreas) {
				newRec = new Rectangle(r.getX(), r.getY(), r.getWidth(), widthOrHeight);
				resizeRectangle(r,
					r.getX(),
					newStartY,
					r.getWidth(),
					r.getHeight() - widthOrHeight - borderWidth);
			} else {
				newRec = new Rectangle(
					r.getX(),
					newStartY,
					r.getWidth(),
					r.getHeight() - widthOrHeight - borderWidth);
				resizeRectangle(r, r.getWidth(), widthOrHeight);
			}
		}
		return addRectangle(newRec);
		// Add empty edges array for new rectangle
	}

	/**
	 * Changes size of a rectangle and updates its edges without creating a new object.
	 *
	 * @param r
	 * 	Rectangle from this system to change size of
	 * @param newWidth
	 * 	New width
	 * @param newHeight
	 * 	New height
	 */
	private void resizeRectangle(Rectangle r, int newWidth, int newHeight) {
		excludeRectangle(r);
		Rectangle newR = new Rectangle(r.getX(), r.getY(), newWidth, newHeight);
		addRectangle(newR);
	}

	/**
	 * Changes size and coordinates of a rectangle and updates its edges without creating a new object.
	 *
	 * @param r
	 * 	Rectangle from this system to changs size and coordinates of.
	 * @param newX
	 * 	New x-coordinate
	 * @param newY
	 * 	New y-coordinate
	 * @param newWidth
	 * 	New width
	 * @param newHeight
	 * 	New height
	 */
	private void resizeRectangle(Rectangle r, int newX, int newY, int newWidth, int newHeight) {
		excludeRectangle(r);
		Rectangle newR = new Rectangle(newX, newY, newWidth, newHeight);
		addRectangle(newR);
	}

	public Rectangle cutRectangleFromSide(Rectangle rectangleToCut, CardinalDirection side, int depth) {
		if (depth < 1) {
			throw new IllegalArgumentException(
				"Depth must be 1 or greater; it is now " + depth);
		}
		if (side == CardinalDirection.N) {
			return splitRectangle(
				rectangleToCut,
				Orientation.HORIZONTAL,
				depth,
				true);
		} else if (side == CardinalDirection.E) {
			return splitRectangle(
				rectangleToCut,
				Orientation.VERTICAL,
				-depth,
				false);
		} else if (side == CardinalDirection.S) {
			return splitRectangle(
				rectangleToCut,
				Orientation.HORIZONTAL,
				-depth,
				false);
		} else if (side == CardinalDirection.W) {
			return splitRectangle(
				rectangleToCut,
				Orientation.VERTICAL,
				depth,
				true);
		} else {
			throw new Error("Unknown direction " + side);
		}
	}

	public Rectangle findRectangleWithMostNeigbors() {
		Rectangle answer = null;
		int maxNumberOfEdges = 0;
		for (Rectangle r : graph.vertexSet()) {
			int size = graph.edgesOf(r).size();
			if (size > maxNumberOfEdges) {
				answer = r;
				maxNumberOfEdges = size;
			}
		}
		if (answer == null) {
			throw new Error("RectangleSystem has no rectangles in it");
		}
		return answer;
	}

	/**
	 * Returns an iterable object that allows iteration over all neighbors of a certain Rectangle in a certain
	 * order.
	 *
	 * @param r
	 * 	A rectangle whose neighbors you need to iterate over.
	 * @see NeighboursIterable
	 */
	public NeighboursIterable getNeighboursIterable(Rectangle r) {
		return new NeighboursIterable(this, r);
	}

	public Collection<Segment> getOuterSegmentsOf(Rectangle r, CardinalDirection side) {
		return outerSegments.getOuterSegmentsOf(r, side);
	}

	private static class RectangleComparator implements Comparator<Rectangle> {

		private final Orientation orientation;

		RectangleComparator(Orientation orientation) {
			this.orientation = orientation;
		}

		@Override
		public int compare(Rectangle o1, Rectangle o2) {
			int staticCoord1 = o1.getMinStaticCoord(orientation);
			int staticCoord2 = o2.getMinStaticCoord(orientation);
			if (staticCoord1 == staticCoord2) {
				int dDynamicCoord = o1.getMinDynamicCoord(orientation) - o2.getMinDynamicCoord(orientation);
				assert dDynamicCoord != 0 || o1 == o2 : o1 + " " + o2;
				return dDynamicCoord;
			} else {
				return staticCoord1 - staticCoord2;
			}
		}
	}

	public static class Neighborship {
		private final CardinalDirection occupiedSideOfSourceRectangle;
		private final NeighborshipType type;

		Neighborship(CardinalDirection occupiedSideOfSourceRectangle, NeighborshipType type) {
			this.occupiedSideOfSourceRectangle = occupiedSideOfSourceRectangle;
			this.type = type;
		}

		public boolean isNeighborship() {
			return type == NeighborshipType.NEIGHBORSHIP;
		}

		public boolean isUnion() {
			return type == NeighborshipType.UNION;
		}

		private enum NeighborshipType {
			NEIGHBORSHIP, UNION;
		}
	}

	class OuterSegments {
		final Map<Rectangle, Map<CardinalDirection, ImmutableSet<Segment>>> recsToSegments = new HashMap<>();
		private Map<Rectangle, Set<CardinalDirection>> allOuterSides;

		Collection<Segment> getOuterSegmentsOf(Rectangle r, CardinalDirection side) {
			if (!recsToSegments.containsKey(r)) {
				return computeOuterSegmentsOf(r, side);
			}
			Map<CardinalDirection, ImmutableSet<Segment>> segments = recsToSegments.get(r);
			if (segments == null || segments.get(side) == null) {
				return computeOuterSegmentsOf(r, side);
			} else {
				// If a segment is already computed.
				return recsToSegments.get(r).get(side);
			}
		}

		void uncomputeSide(Rectangle r, CardinalDirection side) {
			if (recsToSegments.containsKey(r)) {
				recsToSegments.get(r).remove(side);
			}
		}

		/**
		 * Returns an ImmutableMap where key is a rectangle and value is a set of outer sides of that rectangle in this
		 * system.
		 *
		 * @return
		 */
		public Map<Rectangle, Set<CardinalDirection>> outerSidesOfRectangles() {
			ImmutableMap.Builder<Rectangle, Set<CardinalDirection>> builder = ImmutableMap.builder();
			for (Map.Entry<Rectangle, Map<CardinalDirection, ImmutableSet<Segment>>> entry : recsToSegments.entrySet()) {
				assert !entry.getValue().isEmpty();
				Builder<CardinalDirection> valueBuilder = ImmutableSet.builder();
				for (CardinalDirection dir : CardinalDirection.values()) {
					if (!entry.getValue().isEmpty()) {
						valueBuilder.add(dir);
					}
				}
				builder.put(entry.getKey(), valueBuilder.build());
			}
			return builder.build();
		}

		/**
		 * Finds free segments from a side of a rectangle and saves them.
		 *
		 * @param r
		 * 	A rectangle, may be outer or inner.
		 * @param side
		 * 	A side of that rectangle from which you need to find free segments.
		 * @return Saved segments.
		 */
		private Collection<Segment> computeOuterSegmentsOf(Rectangle r, CardinalDirection side) {
			ImmutableSet<Segment> segments = getSegmentsFreeFromNeighbors(r, side);
			Map<CardinalDirection, ImmutableSet<Segment>> map;
			if (recsToSegments.containsKey(r)) {
				map = recsToSegments.get(r);
			} else {
				map = new HashMap<>();
				recsToSegments.put(r, map);
			}
			assert !recsToSegments.get(r).containsKey(side);
			map.put(side, segments);
			return segments;
		}

		private Collection<CardinalDirection> getOuterSidesOf(Rectangle r) {
			Builder<CardinalDirection> builder = ImmutableSet.builder();
			for (CardinalDirection dir : CardinalDirection.values()) {
				Collection<Segment> segments = getOuterSegmentsOf(r, dir);
				if (!segments.isEmpty()) {
					builder.add(dir);
				}
			}
			return builder.build();
		}

		private Set<Rectangle> getOuterRectangles() {
			computeForAllRectangles();
			return recsToSegments.keySet();
		}

		private void computeForAllRectangles() {
			for (Rectangle r : content) {
				if (!recsToSegments.containsKey(r)) {
					for (CardinalDirection side : CardinalDirection.values()) {
						computeOuterSegmentsOf(r, side);
					}
				}
			}
		}

		public ImmutableMap<Rectangle, Collection<CardinalDirection>> getAllOuterSides() {
			computeForAllRectangles();
			ImmutableMap.Builder<Rectangle, Collection<CardinalDirection>> builder = ImmutableMap.builder();
			for (Map.Entry<Rectangle, Map<CardinalDirection, ImmutableSet<Segment>>> entry : recsToSegments.entrySet()) {
				if (!entry.getValue().isEmpty()) {
					builder.put(entry.getKey(), entry.getValue().keySet());
				}
			}
			return builder.build();
		}

		public void uncomputeRectangle(Rectangle r) {
			recsToSegments.remove(r);
		}
	}

	public class NeighboursIterable implements Iterable<Rectangle> {
		private final RectangleSystem rs;
		private final Rectangle centralRectangle;
		private ArrayList<Rectangle> orderedRectangles = new ArrayList<>();
		private boolean orderSet = false;
		private Rectangle startingNeighbour;
		private double centerX;
		private double centerY;

		NeighboursIterable(RectangleSystem rs, Rectangle centralRectangle) {
			this.rs = rs;
			this.centralRectangle = centralRectangle;
			this.centerX = centralRectangle.getCenterX();
			this.centerY = centralRectangle.getCenterY();
		}

		@Override
		public Iterator<Rectangle> iterator() {
			if (!orderSet) {
				throw new Error(
					"You must first set this Iterable's type of order with a set* method");
			}
			return orderedRectangles.iterator();
		}

		/**
		 * Prepares the iterable to iterate over all rectangles in clockwise order. Before calling this method, you
		 * must
		 * set a
		 * starting rectangle either implicitly with {@link NeighboursIterable#setStartingNeigbour(Rectangle)} or
		 * explicitly with {@link NeighboursIterable#setRandomStartingNeighbour()}.
		 *
		 * @return This same object, so you can use a chain of invocations to its methods right inside for (:) loop
		 * heading.
		 */
		public NeighboursIterable setClockwiseOrder() {
			orderedRectangles = getRectanglesSortedClockwise();
			return this;
		}

		public NeighboursIterable setCounterClockwiseOrder() {
			ArrayList<Rectangle> rectangles = getRectanglesSortedClockwise();
			orderedRectangles.add(startingNeighbour);
			for (int i = rectangles.size() - 1; i > 0; i++) {
				// We go from last element backwards to element at index 1,
				// because element at
				// index 0 is the startingNeighbor, and it we manually added it
				// hereinabove.
				orderedRectangles.add(rectangles.get(i));
			}
			return this;
		}

		public NeighboursIterable setRandomOrder() {
			orderedRectangles.addAll(rs.getNeighbors(centralRectangle));
			orderedRectangles.remove(orderedRectangles
				.indexOf(startingNeighbour));
			Collections.shuffle(orderedRectangles);
			return this;
		}

		/**
		 * Returns a list of rectangles sorted clockwise, but doesn't consider the {@link
		 * NeighboursIterable#startingNeighbour}
		 * rectangle. Instead the first rectangle is the one with a center point closest to 0 angle in a polar
		 * coordinate
		 * system whose 0 ray is collinear to world's Cartesian system's x-axis.
		 */
		private ArrayList<Rectangle> getRectanglesSortedClockwise() {
			ArrayList<AngleRectanglePair> rectangles = new ArrayList<>();
			int startingNeighbourIndex = -1;
			for (Rectangle r : rs.getNeighbors(centralRectangle)) {
				// For each rectangle save its center's angle in a polar
				// coordinate system.
				// We'll need the angle solely for sorting.
				if (r == startingNeighbour) {
					startingNeighbourIndex = rectangles.size();
				}
				rectangles.add(new AngleRectanglePair(Math.atan2(
					r.getCenterY() - centerY,
					r.getCenterX() - centerX), r));
			}
			Collections.sort(rectangles, new Comparator<AngleRectanglePair>() {
				// Sort collection by angle value
				@Override
				public int compare(AngleRectanglePair pair1, AngleRectanglePair pair2) {
					if (pair1.angle > pair2.angle) {
						return 1;
					}
					if (pair1.angle < pair2.angle) {
						return -1;
					}
					return 0;
				}
			});
			// Fill the actual list of rectangles in a right (clockwise) order.
			ArrayList<Rectangle> answer = new ArrayList<>();
			for (int i = startingNeighbourIndex, l = rectangles.size(); i < l; i++) {
				answer.add(rectangles.get(i).rectangle);
			}
			for (int i = 0; i < startingNeighbourIndex; i++) {
				answer.add(rectangles.get(i).rectangle);
			}
			return answer;
		}

		public NeighboursIterable setStartingNeigbour(Rectangle neighbour) {
			startingNeighbour = neighbour;
			return this;
		}

		public NeighboursIterable setRandomStartingNeighbour() {
			startingNeighbour = Utils.getRandomElement(rs.getNeighbors(centralRectangle));
			return this;
		}

		class AngleRectanglePair {
			private final double angle;
			private final Rectangle rectangle;

			AngleRectanglePair(double angle, Rectangle rectangle) {
				this.angle = angle;
				this.rectangle = rectangle;
			}
		}
	}
}