package tendiwa.geometry;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import tendiwa.core.Location;
import tendiwa.core.StaticData;
import tendiwa.core.TerrainModifier;
import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Utils;
import tests.SideTest;

/**
 * <p>
 * RectangleSystem is one of the most basic yet powerful concepts of terrain
 * generation. Basically it is a graph, where vertices are rectangles. There is
 * an edge between two vertices if these rectangles touch each other's sides.
 * RectangleSystem provides methods
 * </p>
 */
public class RectangleSystem implements Iterable<RectangleArea> {
	public static final EdgeFactory<RectangleArea, DefaultEdge> edgeFactory = new EdgeFactory<RectangleArea, DefaultEdge>() {
		@Override
		public DefaultEdge createEdge(RectangleArea sourceVertex, RectangleArea targetVertex) {
			return new DefaultEdge();
		}
	};
	protected boolean isBuilt = false;
	/**
	 * A minimum probable Rectangle that contains all RectangleAreas of this
	 * RectangleSystem. This field is set once the edges between RectangleAreas
	 * are built with RectangleSystem#buildEdges
	 */
	protected Rectangle boundingRectangle;
	/**
	 * Amount of cells between two neighbor rectangles. RectangleSystem must
	 * obey this rule: to successfully build it, neighbor rectangles must be
	 * %borderWidth% cells away from each other.
	 */
	protected int borderWidth;
	/**
	 * RectangleAreas that are excluded from a RectangleSystems with methods
	 * {@link RectangleSystem#excludeRectangle(int)},
	 * {@link RectangleSystem#excludeRectangle(Rectangle)} or
	 * {@link RectangleSystem#excludeRectangle(RectangleArea)} are saved here.
	 * This also means that they are not collected by GC until the
	 * RectangleSystem itself is collected.
	 */
	private HashSet<RectangleArea> excluded;
	/**
	 * The main part of a RectangleSystem — a graph that depicts connections
	 * between neighbor RectangleAreas.
	 */
	protected SimpleGraph<RectangleArea, DefaultEdge> graph;
	/**
	 * RectangleAreas that are parts of this RectangleSystem.
	 */
	protected HashSet<RectangleArea> content;
	/**
	 * For some RectangleAreas in this RectangleSystem here is saved a list of
	 * {@link SideTest}s of that RectangleArea that are exposed outside of
	 * RectangleSystem (means they have no neighbors in the RectangleSystem from
	 * that direction).
	 */
	HashMap<RectangleArea, Set<CardinalDirection>> outerSides;

	public static Comparator<RectangleArea> horizontalRectangleComparator = new Comparator<RectangleArea>() {
		@Override
		public int compare(RectangleArea r1, RectangleArea r2) {
			if (r1.x > r2.x) {
				return 1;
			}
			if (r1.x < r2.x) {
				return -1;
			}
			return 0;
		}
	};
	public static Comparator<RectangleArea> verticalRectangleComparator = new Comparator<RectangleArea>() {
		@Override
		public int compare(RectangleArea r1, RectangleArea r2) {
			if (r1.y > r2.y) {
				return 1;
			}
			if (r1.y < r2.y) {
				return -1;
			}
			return 0;
		}
	};

	public RectangleSystem(int borderWidth) {
		this.borderWidth = borderWidth;
		excluded = new HashSet<RectangleArea>();
		content = new HashSet<RectangleArea>();
		graph = new SimpleGraph<RectangleArea, DefaultEdge>(edgeFactory);
	}
	/**
	 * <p>
	 * A method necessary to be called before using the system.
	 * </p>
	 * <ul>
	 * <li>Builds system's graph, creating edges between RectangleAreas</li>
	 * <li>Makes this RectangleSystem immutable</li>
	 * <li>Sets this system's {@link RectangleSystem#boundingRectangle}
	 * available</li>
	 * </ul>
	 */
	public void build() {
		// Previously all RectangleAreas were added to RectangleSystem#content
		// field. Now putting them all into graph as vertices.
		for (RectangleArea r : content) {
			graph.addVertex(r);
		}
		// Making a copy of collection to iterate and delete from the copied
		// collection
		Collection<RectangleArea> rectangles = new HashSet<RectangleArea>(content);
		Iterator<RectangleArea> i = rectangles.iterator();

		// The point of this loop is "check each RectangleArea in this
		// RectangleSystem if it is near another RectangleArea and build edges
		// between RectangleAreas that are.
		while (i.hasNext()) {
			// r1 is taken from collection copy, and at the and of the loop
			// collection will be empty — its elements are deleted at the end
			// of each iteration.
			RectangleArea r1 = i.next();
			if (r1 == null) {
				throw new RuntimeException("Dafuq");
			}
			Iterator<RectangleArea> i2 = rectangles.iterator();
			while (i2.hasNext()) {
				// The second iterator picks all elements except of current
				RectangleArea r2 = i2.next();
				if (r1 == r2) {
					continue;
				}
				if (r2 == null) {
					throw new RuntimeException("Dafuq");
				}
				if (areRectanglesNear(r1, r2)) {
					graph.addEdge(r1, r2);
				}
			}
			// Each element in this top loop will be removed, because otherwise
			// the same elements would be checked several times (Reduces the
			// whole number of iterations from n*(n-1) to (n*(n+1))/2)
			i.remove();
		}
		isBuilt = true;
		/*
		 * Find a rectangle defined by 2 points {startX:startY} and {endX:endY}
		 * and set it as this RectangleSystem's bounding rectangle.
		 */
		int endX = Integer.MIN_VALUE, endY = Integer.MIN_VALUE, startX = Integer.MAX_VALUE, startY = Integer.MAX_VALUE;
		for (Rectangle r : content) {
			if (r.x < startX) {
				startX = r.x;
			}
			if (r.y < startY) {
				startY = r.y;
			}
			if (r.x + r.width > endX) {
				endX = r.x + r.width;
			}
			if (r.y + r.height > endY) {
				endY = r.y + r.height;
			}
		}
		boundingRectangle = new Rectangle(startX, startY, endX - startX, endY - startY);
		findOuterRectangles();
	}
	/* ========== GETTERS ========== */
	public Graph<RectangleArea, DefaultEdge> getGraph() {
		return graph;
	}
	public Map<RectangleArea, Set<CardinalDirection>> getOuterSides() {
		return Collections.unmodifiableMap(outerSides);
	}

	/**
	 * Returns the minimum possible Rectangle that contains the RectangleAreas
	 * of this RectangleSystem. This rectangle will be in the absolute
	 * coordinates, unlike {@link TerrainModifier}'s
	 * {@link TerrainModifier#getBoundingRectangle()}, which returns a Rectangle
	 * with its start coordinates relative to {@link Location}'s absolute start
	 * coordinates.
	 * 
	 * @return
	 */
	public Rectangle getBoundingRectangle() {
		if (!isBuilt) {
			throw new RuntimeException("Can't get bounding rectangle before edges are built");
		}
		return boundingRectangle;
	}
	public Set<RectangleArea> getContent() {
		return Collections.unmodifiableSet(content);
	}
	public boolean isBuilt() {
		return isBuilt;
	}
	public int getBorderWidth() {
		return borderWidth;
	}
	/**
	 * Returns a RectangleArea from this RectangleSystem that contains a
	 * particular cell.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public RectangleArea findRectangleByCell(int x, int y) {
		for (RectangleArea r : graph.vertexSet()) {
			if (r.contains(x, y)) {
				return r;
			}
		}
		throw new RuntimeException("There is no rectangle that contains point {" + x + ":" + y + "}");
	}
	/**
	 * Returns a view to all RectangleArea existing in this RectangleSystem.
	 * 
	 * @return An unmodifiable collection of all the RectangleAreas.
	 */
	public Set<RectangleArea> rectangleSet() {
		return Collections.unmodifiableSet(content);
	}
	/**
	 * Returns a random RectangleArea existing in this RectangleSystem.
	 * 
	 * @return
	 */
	public Rectangle getRandomRectangle() {
		ArrayList<RectangleArea> rectangles = new ArrayList<RectangleArea>(content);
		return rectangles.get(Chance.rand(0, rectangles.size() - 1));
	}
	/**
	 * Returns a random RectangleArea existing in this RectangleSystem that is
	 * outer.
	 * 
	 * @return
	 * @see RectangleSystem#outerSides
	 */
	public Rectangle getRandomOuterRectangle() {
		ArrayList<RectangleArea> a = new ArrayList<RectangleArea>(outerSides.keySet());
		return a.get(Chance.rand(0, a.size()));
	}
	/**
	 * Finds out from which {@link SideTest} is a RectangleArea located
	 * relatively to another RectangleArea. Both shound exist in this
	 * RectangleSystem.
	 * 
	 * @param rectangle
	 * @param neighbor
	 * @return
	 */
	public CardinalDirection getNeighborSide(Rectangle rectangle, Rectangle neighbor) {
		if (rectangle.y == neighbor.y + neighbor.height + borderWidth) {
			return CardinalDirection.N;
		}
		if (rectangle.x + rectangle.width + borderWidth == neighbor.x) {
			return CardinalDirection.E;
		}
		if (rectangle.y + rectangle.height + borderWidth == neighbor.y) {
			return CardinalDirection.S;
		}
		if (rectangle.x == neighbor.x + neighbor.width + borderWidth) {
			return CardinalDirection.W;
		}
		throw new RuntimeException("Cannot find direction of neighbor rectangle " + neighbor + " for rectangle " + rectangle);
	}
	/**
	 * Returns length in cells of a zone of contact between two neighbor
	 * rectangles existing in this RectangleArea.
	 * 
	 * @param r1
	 * @param r2
	 */
	public int lengthOfAdjacentZone(Rectangle r1, Rectangle r2) {
		if (!areRectanglesNear(r1, r2)) {
			throw new RuntimeException("Rectangles don't touch each other");
		}
		CardinalDirection side = getNeighborSide(r1, r2);
		switch (side) {
			case N:
			case S:
				return Math.min(r1.x + r1.width, r2.x + r2.width) - Math.max(r1.x, r2.x);
			case E:
			case W:
			default:
				return Math.min(r1.y + r1.height, r2.y + r2.height) - Math.max(r1.y, r2.y);

		}
	}
	/**
	 * Returns segments on a RectangleArea's direction that don't touch any
	 * neighbors.
	 * 
	 * @param r
	 * @param direction
	 * @return A set of all such segments.
	 */
	public Set<Segment> getSegmentsFreeFromNeighbors(RectangleArea r, CardinalDirection side) {
		if (!isBuilt) {
			throw new RuntimeException("RectangleSystem must be built before calling this method");
		}
		ArrayList<RectangleArea> rectanglesFromThatSide = new ArrayList<RectangleArea>(getRectanglesCloseToSide(r, side));
		// Sort neighbors from that direction from top to bottom or from left to
		// right
		if (side == CardinalDirection.N || side == CardinalDirection.S) {
			Collections.sort(rectanglesFromThatSide, horizontalRectangleComparator);
		} else {
			Collections.sort(rectanglesFromThatSide, verticalRectangleComparator);
		}
		ArrayList<Segment> segments = new ArrayList<Segment>();
		// We start from a single segment which fills the whole direction of
		// rectangle r.
		if (side == CardinalDirection.N) {
			segments.add(new Segment(r.x, r.y, r.width, Orientation.HORIZONTAL));
		} else if (side == CardinalDirection.E) {
			segments.add(new Segment(r.x + r.width - 1, r.y, r.height, Orientation.VERTICAL));
		} else if (side == Directions.S) {
			segments.add(new Segment(r.x, r.y + r.height - 1, r.width, Orientation.HORIZONTAL));
		} else {
			// if (direction == DirectionOldSide.W)
			segments.add(new Segment(r.x, r.y, r.height, Orientation.VERTICAL));
		}
		int splitSegmentStartCoord, splitSegmentLength;
		// For each neighbor from that direction, we split our initial segment with
		// another segment that is a direction of a neighbor rectangle that touches
		// our initial segment.
		for (int i = 0, l = rectanglesFromThatSide.size(); i < l; i++) {
			Rectangle neighbor = rectanglesFromThatSide.get(i);
			switch (side) {
				case N:
					splitSegmentStartCoord = neighbor.x - borderWidth;
					splitSegmentLength = neighbor.width + borderWidth * 2;
					break;
				case E:
					splitSegmentStartCoord = neighbor.y - borderWidth;
					splitSegmentLength = neighbor.height + borderWidth * 2;
					break;
				case S:
					splitSegmentStartCoord = neighbor.x - borderWidth;
					splitSegmentLength = neighbor.width + borderWidth * 2;
					break;
				case W:
				default:
					splitSegmentStartCoord = neighbor.y - borderWidth;
					splitSegmentLength = neighbor.height + borderWidth * 2;
			}
			// Now, there may be a situation when the whole direction segment is
			// eliminated, and there are still rectangles from that direction. If
			// number of segments here reaches 0, it means that this direction of
			// RectangleArea has no neighbor-free segments. Now we can safely
			// break the loop and go straight to the "return" part.
			if (segments.size() == 0) {
				break;
			}
			// Consecutively splitting the last segment with a neighbor
			// rectangle direction segment, we get several segments. That is why we
			// sorted all the neighbors — otherwise we would have to compare
			// each neighbor to each segment on each step.
			Segment[] newSegments = segments.get(segments.size() - 1).splitWithSegment(splitSegmentStartCoord, splitSegmentLength);
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
		return new HashSet<Segment>(segments);
	}
	public Set<RectangleSidePiece> getSidePiecesFreeFromNeighbours(RectangleArea r, CardinalDirection side) {

	}
	/**
	 * A method similar to
	 * {@link RectangleSystem#getNeighborsFromSide(RectangleArea, SideTest)}
	 * finds the neighbors from that direction and also RectangleAreas that touch
	 * argument's direction only with their borders.
	 * 
	 * @return
	 */
	Set<RectangleArea> getRectanglesCloseToSide(RectangleArea r, CardinalDirection side) {
		Set<RectangleArea> rectanglesFromThatSide = new HashSet<RectangleArea>();
		// TODO: Add somewhere examples of such rectangles as in comment below.
		/*
		 * Not only neighbors can shorten free segments, but also the rectangles
		 * that touch this rectangle only with their border can shorten free
		 * segments too. That's why we check all the rectangles, and not only
		 * the neighbors.
		 */
		if (side == CardinalDirection.N) {
			for (RectangleArea neighbor : content) {
				/*
				 * The part starting from Utils.integersRangeIntersection in
				 * each case checks if a neighbor rectangle touches _side_ (not
				 * border!) of the rectangle r with its direction _or_ border.
				 */
				if (neighbor.y + neighbor.height + borderWidth == r.y && Utils.integersRangeIntersection(neighbor.x - borderWidth, neighbor.x + neighbor.width - 1 + borderWidth, r.x, r.x + r.width - 1) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}

		} else if (side == CardinalDirection.E) {
			for (RectangleArea neighbor : content) {
				if (neighbor.x == r.x + r.width + borderWidth && Utils.integersRangeIntersection(neighbor.y - borderWidth, neighbor.y + neighbor.height - 1 + borderWidth, r.y, r.y + r.height - 1) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		} else if (side == CardinalDirection.S) {
			for (RectangleArea neighbor : content) {
				if (neighbor.y == r.y + r.height + borderWidth && Utils.integersRangeIntersection(neighbor.x - borderWidth, neighbor.x + neighbor.width - 1 + borderWidth, r.x, r.x + r.width - 1) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		} else {
			// if (direction == SideTest.W)
			for (RectangleArea neighbor : content) {
				if (neighbor.x + neighbor.width + borderWidth == r.x && Utils.integersRangeIntersection(neighbor.y - borderWidth, neighbor.y + neighbor.height - 1 + borderWidth, r.y, r.y + r.height - 1) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		}
		return rectanglesFromThatSide;
	}
	/**
	 * <p>
	 * A method similar to
	 * {@link RectangleSystem#getNeighborsFromSide(RectangleArea, SideTest)}
	 * finds the neighbors from that direction and also RectangleAreas that touch
	 * argument's direction _or border_ only with their borders.
	 * </p>
	 * 
	 * @return
	 */
	public Set<RectangleArea> getRectanglesCloseToSideOrBorder(RectangleArea r, CardinalDirection side) {
		Set<RectangleArea> rectanglesFromThatSide = new HashSet<RectangleArea>();
		// TODO: Add somewhere examples of such rectangles as in comment below.
		/*
		 * Not only neighbors can shorten free segments, but also the rectangles
		 * that touch this rectangle only with their border can shorten free
		 * segments too. That's why we check all the rectangles, and not only
		 * the neighbors.
		 */
		if (side == CardinalDirection.N) {
			for (RectangleArea neighbor : content) {
				/*
				 * The part starting from Utils.integersRangeIntersection in
				 * each case checks if a neighbor rectangle touches _side_ (not
				 * border!) of the rectangle r with its direction _or_ border.
				 */
				if (neighbor.y + neighbor.height + borderWidth == r.y && Utils.integersRangeIntersection(neighbor.x - borderWidth, neighbor.x + neighbor.width - 1 + borderWidth, r.x - borderWidth, r.x + r.width - 1 + borderWidth) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}

		} else if (side == CardinalDirection.E) {
			for (RectangleArea neighbor : content) {
				if (neighbor.x == r.x + r.width + borderWidth && Utils.integersRangeIntersection(neighbor.y - borderWidth, neighbor.y + neighbor.height - 1 + borderWidth, r.y - borderWidth, r.y + r.height - 1 + borderWidth) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		} else if (side == CardinalDirection.S) {
			for (RectangleArea neighbor : content) {
				if (neighbor.y == r.y + r.height + borderWidth && Utils.integersRangeIntersection(neighbor.x - borderWidth, neighbor.x + neighbor.width - 1 + borderWidth, r.x - borderWidth, r.x + r.width - 1 + borderWidth) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		} else {
			// if (direction == SideTest.W)
			for (RectangleArea neighbor : content) {
				if (neighbor.x + neighbor.width + borderWidth == r.x && Utils.integersRangeIntersection(neighbor.y - borderWidth, neighbor.y + neighbor.height - 1 + borderWidth, r.y - borderWidth, r.y + r.height - 1 + borderWidth) > 0) {
					rectanglesFromThatSide.add(neighbor);
				}
			}
		}
		return rectanglesFromThatSide;
	}
	/**
	 * Returns all outer rectangles of this RectangleSystem at this exact
	 * moment. This set is not a view — it is not modified when the
	 * RectangeSystem's outer rectangles are modified.
	 * 
	 * @return
	 */
	public Set<RectangleArea> outerRectanglesSet() {
		if (!isBuilt) {
			throw new RuntimeException("RectangleSystem must be built before calling this method");
		}
		Set<RectangleArea> answer = new HashSet<RectangleArea>();
		for (Map.Entry<RectangleArea, Set<CardinalDirection>> e : outerSides.entrySet()) {
			if (e.getValue().size() > 0) {
				answer.add(e.getKey());
			}
		}
		return answer;
	}
	/**
	 * Returns a set of RectangleAreas that touch a RectangleArea from a
	 * particular direction.
	 * 
	 * @param r
	 *            A RectangleArea to seek neighbors of.
	 * @param direction
	 *            From which direction to seek for neighbors.
	 * @return All neighbors from that direction.
	 */
	public Set<RectangleArea> getNeighborsFromSide(RectangleArea r, CardinalDirection side) {
		Set<RectangleArea> neighborsFromThatSide = new HashSet<RectangleArea>();
		for (DefaultEdge e : graph.edgesOf(r)) {
			if (graph.getEdgeSource(e) == r && getNeighborSide(r, graph.getEdgeTarget(e)) == side) {
				neighborsFromThatSide.add(graph.getEdgeTarget(e));
			} else if (graph.getEdgeTarget(e) == r && getNeighborSide(r, graph.getEdgeSource(e)) == side) {
				neighborsFromThatSide.add(graph.getEdgeSource(e));
			}
		}
		return neighborsFromThatSide;
	}
	/**
	 * Return all neighbors of a rectangle
	 * 
	 * @param r
	 * @return
	 */
	public Set<RectangleArea> getNeighbors(RectangleArea r) {
		Set<RectangleArea> neighbors = new HashSet<RectangleArea>();
		for (DefaultEdge e : graph.edgesOf(r)) {
			if (graph.getEdgeSource(e) == r) {
				neighbors.add(graph.getEdgeTarget(e));
			} else if (graph.getEdgeTarget(e) == r) {
				neighbors.add(graph.getEdgeSource(e));
			}
		}
		return neighbors;
	}
	/**
	 * Returns a segment inside RectangleArea r1 by which r1 touches r2.
	 * 
	 * @param r1
	 * @param r2
	 * @return A segment that lies inside r1 close to its borders, and is
	 *         located in front of r2.
	 */
	public Segment getAdjacencySegment(RectangleArea r1, RectangleArea r2) {
		if (!areRectanglesNear(r1, r2)) {
			throw new IllegalArgumentException("Both rectangles must be near each other: " + r1 + " " + r2);
		}
		CardinalDirection side = getNeighborSide(r1, r2);
		switch (side) {
			case N:
				return new Segment(Math.max(r1.x, r2.x), r1.y, Math.min(r1.x + r1.width - r2.x, r2.x + r2.width - r1.x), Orientation.HORIZONTAL);
			case E:
				return new Segment(r1.x + r1.width - 1, Math.max(r1.y, r2.y), Math.min(r1.y + r1.height - r2.y, r2.y + r2.height - r2.y), Orientation.VERTICAL);
			case S:
				return new Segment(Math.max(r1.x, r2.x), r1.y + r1.height - 1, Math.min(r1.x + r1.width - r2.x, r2.x + r2.width - r1.x), Orientation.HORIZONTAL);
			case W:
			default:
				return new Segment(r1.x, Math.max(r1.y, r2.y), Math.min(r1.y + r1.height - r2.y, r2.y + r2.height - r1.y), Orientation.VERTICAL);
		}
	}
	/**
	 * Returns a RecntalgeArea with a particular id existing in this
	 * RectangleSystem.
	 * 
	 * @param id
	 *            Id of a RectnagleArea to be found.
	 * @return RectangleArea with that id or null if a RecntalgeArea with this
	 *         id does not exist in this RectnangleSystem.
	 */
	public RectangleArea getRectangleById(int id) {
		for (RectangleArea r : content) {
			if (r.getId() == id) {
				return r;
			}
		}
		return null;
	}

	/* ========== SETTERS ========== */
	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public RectangleArea addRectangleArea(int x, int y, int width, int height) {
		if (isBuilt) {
			throw new RuntimeException("Can't add any more rectangles when system is already built");
		}
		RectangleArea r = new RectangleArea(x, y, width, height);
		content.add(r);
		return r;
	}
	protected RectangleArea addRectangleArea(RectangleArea r) {
		content.add(r);
		return r;
	}
	protected RectangleArea addRectangleArea(Rectangle r) {
		return addRectangleArea(new RectangleArea(r));
	}
	/**
	 * Excludes a RectangleArea from this RectangleSystem. This method can be
	 * called on both build or not build system. If the RectangleSystem is
	 * built, neighbor rectangles of the excluded rectangle will become outer.
	 * 
	 * @param r
	 *            A RectangleArea that exists in this RectangleSystem.
	 */
	public void excludeRectangle(RectangleArea r) {
		if (isBuilt) {
			// Make neighbor rectangles outer.
			for (DefaultEdge e : graph.edgesOf(r)) {
				// Get the neighbor rectangle
				RectangleArea neighbor = graph.getEdgeSource(e);
				if (neighbor == r) {
					neighbor = graph.getEdgeTarget(e);
				}
				CardinalDirection side = getNeighborSide(neighbor, r);
				Set<CardinalDirection> neighborSides = outerSides.get(neighbor);
				if (!neighborSides.contains(side)) {
					// If neighbor rectangle doesn't have the outer direction from
					// which the excluded rectangle was, make this his direction
					// outer.
					neighborSides.add(side);
				}
			}
		}
		boolean rectangleExists = false;
		for (RectangleArea r2 : content) {
			if (r2 == r) {
				rectangleExists = true;
			}
		}
		if (rectangleExists) {
			if (isBuilt) {
				outerSides.remove(r);
				graph.removeVertex(r);
			}
			excluded.add(r);
			content.remove(r);
		} else {
			throw new RuntimeException("No rectangle " + r + "present in system");
		}
	}
	/* ========== CHECKS ========== */
	/**
	 * Checks if a RectangleArea is one of the outer rectangles.
	 * 
	 * @param r
	 * @return
	 * @see {@link RectangleSystem#outerSides}
	 */
	public boolean isRectangleOuter(RectangleArea r) {
		return outerSides.get(r).size() > 0;
	}
	/**
	 * Checks if two RectangleAreas existing in this RectangleSystem can be
	 * neighbors.
	 * 
	 * @param r1
	 * @param r2
	 * @return
	 */
	public boolean areRectanglesNear(Rectangle r1, Rectangle r2) {
		if (r1.x + r1.width + borderWidth == r2.x || r2.x + r2.width + borderWidth == r1.x) {
			// Rectangles share vertical line
			int a1 = r1.y;
			int a2 = r1.y + r1.height - 1;
			int b1 = r2.y;
			int b2 = r2.y + r2.height - 1;
			int intersection = Utils.integersRangeIntersection(a1, a2, b1, b2);
			if (intersection >= 1) {
				return true;
			} else {
				return false;
			}
		} else if (r1.y + r1.height + borderWidth == r2.y || r2.y + r2.height + borderWidth == r1.y) {
			// Rectangles share horizontal line
			int a1 = r1.x;
			int a2 = r1.x + r1.width - 1;
			int b1 = r2.x;
			int b2 = r2.x + r2.width - 1;
			int intersection = Utils.integersRangeIntersection(a1, a2, b1, b2);
			if (intersection >= 1) {
				return true;
			} else {
				return false;
			}
		} else {
			// Rectangles definitely don't share horizontal or vertical lines
			return false;
		}
	}
	/**
	 * Checks if a RectangleArea exists in this RectangleSystem.
	 * 
	 * @param r
	 * @return
	 */
	public boolean hasRectangle(RectangleArea r) {
		return content.contains(r);
	}
	/* ======== PROCESSORS ======== */
	/**
	 * Make this RectangleArea's graph into a directed tree. The shape of a tree
	 * will be determined randomly, but a root vertex can be set.
	 */
	public void convertGraphToDirectedTree() {
		// Randomly select an existing vertex to be a root vertex of a tree
		RectangleArea rootVertex = null;
		for (RectangleArea r : graph.vertexSet()) {
			rootVertex = r;
			break;
		}
		/*
		 * Another graph that will contain the same vertices that this.graph,
		 * but with single tree-form edges.
		 */
		SimpleGraph<RectangleArea, DefaultEdge> graph2 = new SimpleGraph<RectangleArea, DefaultEdge>(edgeFactory);
		graph2.addVertex(rootVertex);
		// Vertex set of the new graph
		Set<RectangleArea> newVertexSet = graph2.vertexSet();
		for (int i = 0, l = graph.vertexSet().size(); i < l; i++) {
			loop: for (RectangleArea r : newVertexSet) {// Select a random
				for (DefaultEdge e : graph.edgesOf(r)) {
					RectangleArea r2 = graph.getEdgeTarget(e);
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
	 * <p>
	 * Finds all double edges and makes them single. There is no way to predict
	 * which vertex will be the sources or the targets of the remaining edges.
	 * </p>
	 * 
	 * <p>
	 * RectangleSystem must be built with {@link RectangleSystem#build()} before
	 * calling this method.
	 * </p>
	 */
	public void convertDoubleEdgesToSingle() {
		if (!isBuilt) {
			throw new RuntimeException("RectangeSystem must be built in order to find rectangles at bounds");
		}
		for (DefaultEdge edge : graph.edgeSet()) {
			if (graph.containsEdge(graph.getEdgeTarget(edge), graph.getEdgeSource(edge))) {
				graph.removeEdge(edge);
			}
		}
	}

	/**
	 * Finds out which RectangleAreas touch a bounding rectangle with their
	 * sides.
	 * 
	 * @see RectangleSystem#outerRectanglesSet()
	 */
	public void findRectanglesAtBounds() {
		if (!isBuilt) {
			throw new RuntimeException("RectangeSystem must be built in order to find rectangles at bounds");
		}
		outerSides = new HashMap<RectangleArea, Set<CardinalDirection>>();
		for (RectangleArea r : content) {
			HashSet<CardinalDirection> set = new HashSet<CardinalDirection>();
			if (r.y == boundingRectangle.y) {
				// North side
				set.add(CardinalDirection.N);
			}
			if (r.x + r.width == boundingRectangle.x + boundingRectangle.width) {
				// East side
				set.add(CardinalDirection.E);
			}
			if (r.y + r.height == boundingRectangle.y + boundingRectangle.height) {
				// South side
				set.add(CardinalDirection.S);
			}
			if (r.x == boundingRectangle.x) {
				// West side
				set.add(CardinalDirection.W);
			}
			if (set.size() > 0) {
				outerSides.put(r, set);
			}
		}
	}
	/**
	 * Finds out which RectangleAreas in this RectangleSystem don't have a side
	 * or several sides that are exposed to an area free of neighbors.
	 */
	public void findOuterRectangles() {
		if (!isBuilt) {
			throw new RuntimeException("RectangleArea must be built before calling this method");
		}
		outerSides = new HashMap<RectangleArea, Set<CardinalDirection>>();
		for (RectangleArea r : content) {
			Set<CardinalDirection> outerSidesOfRectangle = new HashSet<CardinalDirection>();
			for (CardinalDirection side : CardinalDirection.ALL) {
				Set<Segment> segments = getSegmentsFreeFromNeighbors(r, side);
				if (segments.size() > 0) {
					outerSidesOfRectangle.add(side);
				}
			}
			outerSides.put(r, outerSidesOfRectangle);
		}
	}
	/**
	 * <p>
	 * Finds out which RectangleAreas don't have neighbors from some sides and
	 * which sides are these. RectangleSystem must be built with
	 * {@link RectangleSystem#build()} before calling this method.
	 * </p>
	 * 
	 * <p>
	 * After calling the method results will be available via
	 * {@link RectangleSystem#outerRectanglesSet()}
	 * </p>
	 */
	public void findRectanglesWithNoNeighbors() {
		if (!isBuilt) {
			throw new RuntimeException("RectangleArea must be build before calling this method");
		}
		outerSides = new HashMap<RectangleArea, Set<CardinalDirection>>();
		for (RectangleArea r : graph.vertexSet()) {
			// Here are saved Sides that are occupied by neighbors of
			// RectangleArea r. Sides are saved under indexes that are sides'
			// int values.
			CardinalDirection[] occupiedSides = new CardinalDirection[8];
			for (DefaultEdge e : graph.edgesOf(r)) {
				// Finding neighbors by looking at all edges of this vertex.
				// Find out whether neighbor rectangle is on one end of an edge
				// or another.
				RectangleArea neighbor = graph.getEdgeSource(e);
				if (neighbor == r) {
					neighbor = graph.getEdgeTarget(e);
				}
				CardinalDirection side = getNeighborSide(r, neighbor);
				// Only indexes 0, 2, 4 or 6 can be occupied in this array.
				occupiedSides[side.toInt()] = side;
			}
			// After we saved all sides occupied by neighbor, add all the other
			// sides to outerSides map.
			// Unoccupied sides are saved here:
			HashSet<CardinalDirection> outerSidesOfRec = new HashSet<CardinalDirection>();
			for (int i = 0; i < 8; i += 2) {
				// Looking for a saved direction under indexes 0, 2, 4 and 6.
				if (occupiedSides[i] == null) {
					outerSidesOfRec.add((CardinalDirection) Directions.intToDirection(i));
				}
			}
			if (outerSidesOfRec.size() > 0) {
				// If any outer sides were found, save them in field.
				outerSides.put(r, outerSidesOfRec);
			}
		}
		// Now we have all the outer rectangles in outerSides map, but some of
		// the saved rectangles may be not outer — this error appears if
		// rectangle's side touches not another rectangles, but only system's
		// border. Is is only possible if rectangle's width/height<=borderWidth.
		// So we seek for all the possible error-rectangles:
		HashSet<RectangleArea> errorRectangles = new HashSet<RectangleArea>();
		for (RectangleArea r : outerSides.keySet()) {
			if (r.width <= borderWidth || r.height <= borderWidth) {
				errorRectangles.add(r);
			}
		}
		// Check each of such rectangles if it is really surrounded by other
		// rectangles (thus not being outer).
		for (RectangleArea r : errorRectangles) {
			// Both dimensions are checked separately.
			// Only check from that side(s) that is/are considered outer by now
			// (sides considered not outer are definitely not outer)
			Set<CardinalDirection> sides = outerSides.get(r);
			if (sides.contains(CardinalDirection.N)) {
				if (hasDiagonallyAdjacentNeighbor(r, Directions.NW, Orientation.HORIZONTAL) && hasDiagonallyAdjacentNeighbor(r, Directions.NE, Orientation.HORIZONTAL)) {
					sides.remove(Directions.N);
				}
			}
			if (sides.contains(Directions.S)) {
				if (hasDiagonallyAdjacentNeighbor(r, Directions.SW, Orientation.HORIZONTAL) && hasDiagonallyAdjacentNeighbor(r, OrdinalDirection.SE, Orientation.HORIZONTAL)) {
					sides.remove(CardinalDirection.S);
				}
			}
			if (sides.contains(CardinalDirection.W)) {
				if (hasDiagonallyAdjacentNeighbor(r, OrdinalDirection.SW, Orientation.VERTICAL) && hasDiagonallyAdjacentNeighbor(r, OrdinalDirection.NW, Orientation.VERTICAL)) {
					sides.remove(Directions.W);
				}
			}
			if (sides.contains(Directions.E)) {
				if (hasDiagonallyAdjacentNeighbor(r, Directions.NE, Orientation.VERTICAL) && hasDiagonallyAdjacentNeighbor(r, Directions.SE, Directions.V)) {
					sides.remove(Directions.E);
				}
			}
			// If after removing error sides RectangleArea has no more
			// outerSides, remove its entry from outerSides map.

			if (sides.isEmpty()) {
				outerSides.remove(r);
			}
		}
	}
	/**
	 * Quite a specific arithmetic helper method for
	 * {@link RectangleSystem#findOuterRectangles()}, checks if there is a
	 * RectangleArea adjacent to this RectangleArea "diagonally" (dx almost
	 * equals dy), considering {@link RectangleSystem#borderWidth}. It may be
	 * simpler to just read the code than trying to explain what exactly this
	 * method does.
	 * 
	 * @param checkedRec
	 *            A RectangleArea that is being tested.
	 * @param checkSide
	 *            SideTest.NW, SideTest.NE, SideTest.SW or SideTest.SE —
	 *            diagonal destination to look for a diagonally adjacent
	 *            RectangleArea.
	 * @param checkDimension
	 *            Which two sides of checkedRec are being checked - the two
	 *            horizontal sides if the argument is DirectionToBERemoved.H, or
	 *            the two vertical sides if the argument is
	 *            DirectionToBERemoved.V.
	 * @return true is that neighbor of a RectangleArea exists, false otherwise.
	 */
	private boolean hasDiagonallyAdjacentNeighbor(RectangleArea checkedRec, OrdinalDirection checkSide, Orientation checkDimension) {
		if (checkSide == null) {
			throw new NullPointerException();
		}
		// To check a rectangle we build four points, %borderWidth%+1 cells
		// far diagonally from each corner of a rectangle.
		if (checkDimension.isHorizontal()) {
			// If rectangle touches border joint with its horizontal side
			int x1 = checkedRec.x - 1;
			int y1 = checkedRec.y - borderWidth - 1;
			int x2 = checkedRec.x + checkedRec.width;
			int y2 = y1;
			int x3 = x2;
			int y3 = checkedRec.y + checkedRec.height + borderWidth;
			int x4 = x1;
			int y4 = y3;
			int steps = borderWidth - checkedRec.width + 1; // If the rectangle
															// itself is thiner
															// than borderWidth,
															// it will take more
															// that one
															// iteration to test
															// the rectangle.
			for (int i = 0; i < steps; i++) {
				switch (checkSide) {
					case NW:
						for (Rectangle r : content) {
							if (r.x + r.width - 1 == x1 && r.y + r.height - 1 == y1) {
								return true;
							}
						}
						x1--;
						break;
					case NE:
						for (Rectangle r : content) {
							if (r.x == x2 && r.y + r.height - 1 == y2) {
								return true;
							}
						}
						x2++;
						break;
					case SE:
						for (Rectangle r : content) {
							if (r.x == x3 && r.y == y3) {
								return true;
							}
						}
						x3++;
						break;
					case SW:
					default:
						for (Rectangle r : content) {
							if (r.x + r.width - 1 == x4 && r.y == y4) {
								return true;
							}
						}
						x4--;
				}
			}
			return false;
		} else {
			// If checkDimension == DirectionToBERemoved.V
			int x1 = checkedRec.x - borderWidth - 1;
			int y1 = checkedRec.y - 1;
			int x2 = checkedRec.x + checkedRec.width + borderWidth;
			int y2 = y1;
			int x3 = x2;
			int y3 = checkedRec.y + checkedRec.height;
			int x4 = x1;
			int y4 = y3;
			int steps = borderWidth - checkedRec.height + 1;
			for (int i = 0; i < steps; i++) {
				switch (checkSide) {
					case NW:
						for (Rectangle r : content) {
							if (r.x + r.width - 1 == x1 && r.y + r.height - 1 == y1) {
								return true;
							}
						}
						y1--;
						break;
					case NE:
						for (Rectangle r : content) {
							if (r.x == x2 && r.y + r.height - 1 == y2) {
								return true;
							}
						}
						y2--;
						break;
					case SE:
						for (Rectangle r : content) {
							if (r.x == x3 && r.y == y3) {
								return true;
							}
						}
						y3++;
						break;
					case SW:
					default:
						for (Rectangle r : content) {
							if (r.x + r.width - 1 == x4 && r.y == y4) {
								return true;
							}
						}
						y4++;
				}
			}
			return false;
		}
	}
	public static void ddd(Location loc, Rectangle checkedRec) {
		// To check a rectangle we build four points, %borderWidth%+1 cells
		// far diagonally from each corner of a rectangle.
		int borderWidth = 3;
		int wall = StaticData.getObjectType("wall_grey_stone").getId();
		int x1 = checkedRec.x - borderWidth - 1;
		int y1 = checkedRec.y - 1;
		int x2 = checkedRec.x + checkedRec.width + borderWidth;
		int y2 = y1;
		int x3 = x2;
		int y3 = checkedRec.y + checkedRec.height;
		int x4 = x1;
		int y4 = y3;
		int steps = borderWidth - checkedRec.height + 1; // If the rectangle
															// itself is thiner
															// than
															// borderWidth, it
															// will
															// take more that
															// one
															// iteration to test
															// the
															// rectangle.
		for (int i = 0; i < steps; i++) {
			loc.setObject(x1, y1, wall);
			loc.setObject(x2, y2, wall);
			loc.setObject(x3, y3, wall);
			loc.setObject(x4, y4, wall);
			y1--;
			y2--;
			y3++;
			y4++;
		}
	}
	/**
	 * Randomly removes outer RectangleAreas.
	 * 
	 * @param depth
	 * @param chance
	 */
	public void nibbleSystem(int depth, int chance) {
		if (!isBuilt) {
			throw new RuntimeException("RectangleSystem must be built before calling this method");
		}
		int u = 0;
		for (int k = 0; k < depth; k++) {
			Set<RectangleArea> removedRectangles = new HashSet<RectangleArea>();
			for (RectangleArea r : outerRectanglesSet()) {
				if (Chance.roll(chance)) {
					u++;
					removedRectangles.add(r);
				}
			}
			for (RectangleArea r : removedRectangles) {
				excludeRectangle(r);
			}
		}
	}
	/**
	 * Removes all RectangleAreas from this RectangleSystem that are located
	 * inside a particular circle.
	 * 
	 * @param x
	 *            X-coordinate of circle's center.
	 * @param y
	 *            Y-coordinate of circle's center.
	 * @param radius
	 *            Radius of a circle.
	 * @return A Set of removed rectangles.
	 */
	public Set<Rectangle> removeRectanglesInCircle(int x, int y, int radius) {
		Set<Rectangle> answer = new HashSet<Rectangle>();
		Set<RectangleArea> copy = new HashSet<RectangleArea>(content);
		for (RectangleArea r : copy) {
			if (r.isInCircle(x, y, radius)) {
				excludeRectangle(r);
				answer.add(r);
			}
		}
		return answer;
	}
	/**
	 * <p>
	 * Splits rectangle into two rectangles, one of them being the initial
	 * rectangle, and another one a new rectangle. Rectangle under current
	 * number will be the left one (if dir == DirectionToBERemoved.V) or the top
	 * one (if dir == DirectionToBERemoved.H).
	 * </p>
	 * <p>
	 * If width < 0, then a rectangle width width/height = -width from right
	 * side/bottom will be cut off, and under current number still stay
	 * right/bottom rectangle, but the old one will still be the left/top one,
	 * and the returned one will be the right/bottom one.
	 * </p>
	 * 
	 * @param r
	 *            A RectangleArea existing in this RectangleSystem.
	 * @param direction
	 *            Horizontally or vertically.
	 * @param width
	 *            How much to cut.
	 * @return A new rectangle that was created by splitting the old one.
	 */
	public RectangleArea splitRectangle(RectangleArea r, Orientation orientation, int width, boolean reverseAreas) {
		/* */// Optimize size() calls
		if (width == 0) {
			throw new IllegalArgumentException("Argument width can't be 0");
		}
		if (!hasRectangle(r)) {
			throw new IllegalArgumentException("RectangleArea " + r + " doesn't exist in this RectangleSystem");
		}
		boolean negativeWidth = width < 0;
		if (orientation.isVertical()) {
			// Vertically
			if (negativeWidth) {
				// This will be the width of the old RectangleArea
				width = r.width + width - borderWidth;
			}
			if (width > r.width - 2) {
				throw new IllegalArgumentException("Width " + width + " in vertical splitting is too big");
			}
			if (width < 1) {
				width = width + borderWidth - r.width;
				throw new IllegalArgumentException("Width " + width + " in vertical splitting is too big");
			}
			int newStartX = r.x + width + borderWidth;
			RectangleArea newRec;
			if (reverseAreas) {
				newRec = new RectangleArea(r.x, r.y, width, r.height);
				r.setBounds(newStartX, r.y, r.width - width - borderWidth, r.height);
			} else {
				newRec = new RectangleArea(newStartX, r.y, r.width - width - borderWidth, r.height);
				r.setSize(width, r.height);
			}
			return addRectangleArea(newRec);
		} else {
			// Horizontally
			if (negativeWidth) {
				// This will be the width of the old RectangleArea
				width = r.height + width - borderWidth;
			}
			if (width > r.height - 2) {
				throw new IllegalArgumentException("Width " + width + " in horizontal splitting is too big");
			}
			if (width < 1) {
				width = width + borderWidth - r.height;
				throw new IllegalArgumentException("Width " + width + " in horizontal splitting is too big");
			}
			int newStartY = r.y + width + borderWidth;
			RectangleArea newRec;
			// Though argument is called width, it is height if a rectangle
			// is split vertically
			if (reverseAreas) {
				newRec = new RectangleArea(r.x, r.y, r.width, width);
				r.setBounds(r.x, newStartY, r.width, r.height - width - borderWidth);
			} else {
				newRec = new RectangleArea(r.x, newStartY, r.width, r.height - width - borderWidth);
				r.setSize(r.width, width);
			}
			return addRectangleArea(newRec);
		}
		// Add empty edges array for new rectangle
	}
	public RectangleArea cutRectangleFromSide(RectangleArea rectangleToCut, CardinalDirection side, int depth) {
		if (depth < 1) {
			throw new IllegalArgumentException("Depth must be 1 or greater; it is now " + depth);
		}
		if (side == CardinalDirection.N) {
			return splitRectangle(rectangleToCut, Orientation.HORIZONTAL, depth, true);
		} else if (side == CardinalDirection.E) {
			return splitRectangle(rectangleToCut, Orientation.VERTICAL, -depth, false);
		} else if (side == CardinalDirection.S) {
			return splitRectangle(rectangleToCut, Orientation.HORIZONTAL, -depth, false);
		} else if (side == CardinalDirection.W) {
			return splitRectangle(rectangleToCut, Orientation.VERTICAL, depth, true);
		} else {
			throw new Error("Unknown direction " + side);
		}
	}
	/**
	 * Changes {@link RectangleSystem#borderWidth} of this RectangleSystem and
	 * expands all RectangleAreas by that amount of cells to still be neighbors
	 * with their neighbors.
	 * 
	 * @param depth
	 *            Difference between the old borderWidth and the desired
	 *            borderWidth.
	 */
	public void expandRectanglesToBorder(int depth) {
		if (borderWidth < depth * 2) {
			throw new Error("border width " + borderWidth + " is too thin for expanding each rectangle by " + depth);
		}
		for (Rectangle r : content) {
			r.x -= depth;
			r.y -= depth;
			r.width += depth * 2;
			r.height += depth * 2;
		}
		borderWidth -= depth * 2;
	}
	@Override
	public Iterator<RectangleArea> iterator() {
		return content.iterator();
	}
	public RectangleArea findRectangleWithMostNeigbors() {
		RectangleArea answer = null;
		int maxNumberOfEdges = 0;
		for (RectangleArea r : graph.vertexSet()) {
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
	 * Returns an iterable object that allows iteration over all neighbors of a
	 * certain RectangleArea in a certain order.
	 * 
	 * @param r
	 * @see NeighboursIterable
	 */
	public NeighboursIterable getNeighboursIterable(RectangleArea r) {
		return new NeighboursIterable(this, r);
	}

	public class NeighboursIterable implements Iterable<RectangleArea> {
		private ArrayList<RectangleArea> orderedRectangles = new ArrayList<RectangleArea>();
		private boolean orderSet = false;
		private final RectangleSystem rs;
		private final RectangleArea centralRectangle;
		private RectangleArea startingNeighbour;
		private double centerX;
		private double centerY;

		NeighboursIterable(RectangleSystem rs, RectangleArea centralRectangle) {
			this.rs = rs;
			this.centralRectangle = centralRectangle;
			this.centerX = centralRectangle.getCenterX();
			this.centerY = centralRectangle.getCenterY();
		}
		@Override
		public Iterator<RectangleArea> iterator() {
			if (orderSet == false) {
				throw new Error("You must first set this Iterable's type of order with a set* method");
			}
			return orderedRectangles.iterator();
		}
		/**
		 * Prepares the iterable to iterate over all rectangles in clockwise
		 * order. Before calling this method, you must set a starting rectangle
		 * either implicitly with
		 * {@link NeighboursIterable#setStartingNeigbour(RectangleArea)} or
		 * explicitly with
		 * {@link NeighboursIterable#setRandomStartingNeighbour()}.
		 * 
		 * @return This same object, so you can use a chain of invocations to
		 *         its methods right inside for (:) loop heading.
		 */
		public NeighboursIterable setClockwiseOrder() {
			orderedRectangles = getRectanglesSortedClockwise();
			return this;
		}
		public NeighboursIterable setCounterClockwiseOrder() {
			ArrayList<RectangleArea> rectangles = getRectanglesSortedClockwise();
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
			orderedRectangles.remove(orderedRectangles.indexOf(startingNeighbour));
			Collections.shuffle(orderedRectangles);
			return this;
		}
		/**
		 * Returns a list of rectangles sorted clockwise, but doesn't consider
		 * the {@link NeighboursIterable#startingNeighbour} rectangle. Instead
		 * the first rectangle is the one with a center point closest to 0 angle
		 * in a polar coordinate system whose 0 ray is collinear to world's
		 * Cartesian system's x-axis.
		 */
		private ArrayList<RectangleArea> getRectanglesSortedClockwise() {
			ArrayList<AngleRectanglePair> rectangles = new ArrayList<AngleRectanglePair>();
			int startingNeighbourIndex = -1;
			for (RectangleArea r : rs.getNeighbors(centralRectangle)) {
				// For each rectangle save its center's angle in a polar
				// coordinate system.
				// We'll need the angle solely for sorting.
				if (r == startingNeighbour) {
					startingNeighbourIndex = rectangles.size();
				}
				rectangles.add(new AngleRectanglePair(Math.atan2(r.getCenterY() - centerY, r.getCenterX() - centerX), r));
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
			ArrayList<RectangleArea> answer = new ArrayList<RectangleArea>();
			for (int i = startingNeighbourIndex, l = rectangles.size(); i < l; i++) {
				answer.add(rectangles.get(i).rectangle);
			}
			for (int i = 0; i < startingNeighbourIndex; i++) {
				answer.add(rectangles.get(i).rectangle);
			}
			return answer;
		}
		public NeighboursIterable setStartingNeigbour(RectangleArea neighbour) {
			startingNeighbour = neighbour;
			return this;
		}
		public NeighboursIterable setRandomStartingNeighbour() {
			startingNeighbour = Utils.getRandomElement(rs.getNeighbors(centralRectangle));
			return this;
		}

		class AngleRectanglePair {
			private final double angle;
			private final RectangleArea rectangle;

			AngleRectanglePair(double angle, RectangleArea rectangle) {
				this.angle = angle;
				this.rectangle = rectangle;
			}
		}
	}
}