package tendiwa.geometry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import tendiwa.core.meta.Chance;
import tendiwa.core.meta.Utils;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * <p> RectangleSystem is one of the most basic yet powerful concepts of terrain generation. Basically, it is a graph
 * where vertices are rectangles. There is an edge between two vertices if these rectangles touch each other's sides.
 * RectangleSystem provides methods </p> <p>RectangleSystem differs from RectangleSequence in that System always
 * remembers what rectangles are neighbours and what rectangles are outer (don't have any neighbors from some
 * side).</p>
 */
public class RectangleSystem extends RectangleSequence {
private static final Comparator<EnhancedRectangle> COMPARATOR_HORIZONTAL = new RectangleComparator(Orientation.HORIZONTAL);
private static final Comparator<EnhancedRectangle> COMPARATOR_VERTICAL = new RectangleComparator(Orientation.VERTICAL);
public static Comparator<EnhancedRectangle> horizontalRectangleComparator = new Comparator<EnhancedRectangle>() {
	@Override
	public int compare(EnhancedRectangle r1, EnhancedRectangle r2) {
		if (r1.x > r2.x) {
			return 1;
		}
		if (r1.x < r2.x) {
			return -1;
		}
		return 0;
	}
};
public static Comparator<EnhancedRectangle> verticalRectangleComparator = new Comparator<EnhancedRectangle>() {
	@Override
	public int compare(EnhancedRectangle r1, EnhancedRectangle r2) {
		if (r1.y > r2.y) {
			return 1;
		}
		if (r1.y < r2.y) {
			return -1;
		}
		return 0;
	}
};
private final Map<Orientation, TreeSet<EnhancedRectangle>> sortedRectangles;
/**
 * Amount of cells between two neighbor rectangles. RectangleSystem must obey this rule: to successfully done it,
 * neighbor rectangles must be %borderWidth% cells away from each other.
 */
protected int borderWidth;
/**
 * The main part of a RectangleSystem — a graph that depicts connections between neighbor EnhancedRectangles.
 */
protected SimpleGraph<EnhancedRectangle, Neighborship> graph;
private OuterSegments outerSegments = new OuterSegments();

public RectangleSystem(int borderWidth) {
	super();
	this.borderWidth = borderWidth;
	graph = new SimpleGraph<>(Neighborship.class);
	sortedRectangles = new HashMap<>();
	sortedRectangles.put(Orientation.HORIZONTAL, new TreeSet<EnhancedRectangle>(COMPARATOR_HORIZONTAL));
	sortedRectangles.put(Orientation.VERTICAL, new TreeSet<EnhancedRectangle>(COMPARATOR_VERTICAL));
}

public Graph<EnhancedRectangle, Neighborship> getGraph() {
	return graph;
}

public ImmutableMap<EnhancedRectangle, Collection<CardinalDirection>> getOuterSides() {
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
public EnhancedRectangle findRectangleByCell(int x, int y) {
	for (EnhancedRectangle r : graph.vertexSet()) {
		if (r.contains(x, y)) {
			return r;
		}
	}
	throw new RuntimeException(
		"There is no rectangle that contains point {" + x + ":" + y + "}");
}

/**
 * Returns a view to all EnhancedRectangle existing in this RectangleSystem.
 *
 * @return An unmodifiable collection of all the EnhancedRectangles.
 */
public List<EnhancedRectangle> rectangleList() {
	return Collections.unmodifiableList(content);
}

/**
 * Finds out from which {@link CardinalDirection} is a rectangle located relatively to another EnhancedRectangle.
 *
 * @param rectangle
 * 	One rectangle
 * @param neighbor
 * 	Another rectangle
 * @return Side from which neighbor is located relatively to a rectangle.
 */
CardinalDirection getNeighborSide(EnhancedRectangle rectangle, EnhancedRectangle neighbor) {
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
	throw new RuntimeException(
		"Cannot find direction of neighbor rectangle " + neighbor + " for rectangle " + rectangle);
}

/**
 * Returns a set of {@link Segment}s that this system forms with its outer rectangle.
 *
 * @param r
 * 	A rectangle to find free segments of.
 * @param side
 * 	A side of that rectangle.
 * @return A set of all such segments (a zero-length one if a rectangle is not outer)
 */
public ImmutableSet<Segment> getSegmentsFreeFromNeighbors(EnhancedRectangle r, CardinalDirection side) {
	ArrayList<EnhancedRectangle> rectanglesFromThatSide = new ArrayList<>(getRectanglesCloseToSide(r, side));
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
			r.x,
			r.y,
			r.width,
			Orientation.HORIZONTAL
		));
	} else if (side == CardinalDirection.E) {
		segments.add(new Segment(
			r.x + r.width - 1,
			r.y,
			r.height,
			Orientation.VERTICAL
		));
	} else if (side == Directions.S) {
		segments.add(new Segment(
			r.x,
			r.y + r.height - 1,
			r.width,
			Orientation.HORIZONTAL));
	} else {
		// if (direction == DirectionOldSide.W)
		segments.add(new Segment(r.x, r.y, r.height, Orientation.VERTICAL));
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
		// eliminated, and there are still rectangles from that direction.
		// If number of segments here reaches 0, it means that this
		// direction of EnhancedRectangle has no neighbor-free segments. Now we
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

public ImmutableSet<RectangleSidePiece> getSidePiecesFreeFromNeighbours(EnhancedRectangle r, CardinalDirection side) {
	ImmutableSet<Segment> segmentsFreeFromNeighbors = getSegmentsFreeFromNeighbors(r, side);
	Builder<RectangleSidePiece> answer = ImmutableSet.builder();
	for (Segment segment : segmentsFreeFromNeighbors) {
		// TODO: When Segment will be immutablized, create a constructor
		// that uses an existing segment.
		answer.add(new RectangleSidePiece(
			side,
			segment.x,
			segment.y,
			segment.length));
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
 * @see RectangleSystem#getNeighborsFromSide(EnhancedRectangle, CardinalDirection)
 */
Set<EnhancedRectangle> getRectanglesCloseToSide(EnhancedRectangle r, CardinalDirection side) {
	Set<EnhancedRectangle> rectanglesFromThatSide = new HashSet<>();
	// TODO: Add somewhere examples of such rectangles as in comment below.
		/*
		 * Not only neighbors can shorten free segments, but also the rectangles
		 * that touch this rectangle only with their border can shorten free
		 * segments too. That's why we check all the rectangles, and not only
		 * the neighbors.
		 */
	if (side == CardinalDirection.N) {
		for (EnhancedRectangle neighbor : content) {
		        /*
		         * The part starting from Utils.integersRangeIntersection in
				 * each case checks if a neighbor rectangle touches _side_ (not
				 * border!) of the rectangle r with its direction _or_ border.
				 */
			if (neighbor.y + neighbor.height + borderWidth == r.y && Utils
				.integersRangeIntersection(
					neighbor.x - borderWidth,
					neighbor.x + neighbor.width - 1 + borderWidth,
					r.x,
					r.x + r.width - 1) > 0) {
				rectanglesFromThatSide.add(neighbor);
			}
		}

	} else if (side == CardinalDirection.E) {
		for (EnhancedRectangle neighbor : content) {
			if (neighbor.x == r.x + r.width + borderWidth && Utils
				.integersRangeIntersection(
					neighbor.y - borderWidth,
					neighbor.y + neighbor.height - 1 + borderWidth,
					r.y,
					r.y + r.height - 1) > 0) {
				rectanglesFromThatSide.add(neighbor);
			}
		}
	} else if (side == CardinalDirection.S) {
		for (EnhancedRectangle neighbor : content) {
			if (neighbor.y == r.y + r.height + borderWidth && Utils
				.integersRangeIntersection(
					neighbor.x - borderWidth,
					neighbor.x + neighbor.width - 1 + borderWidth,
					r.x,
					r.x + r.width - 1) > 0) {
				rectanglesFromThatSide.add(neighbor);
			}
		}
	} else {
		// if (direction == SideTest.W)
		for (EnhancedRectangle neighbor : content) {
			if (neighbor.x + neighbor.width + borderWidth == r.x && Utils
				.integersRangeIntersection(
					neighbor.y - borderWidth,
					neighbor.y + neighbor.height - 1 + borderWidth,
					r.y,
					r.y + r.height - 1) > 0) {
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
 * @see RectangleSystem#getRectanglesCloseToSide(EnhancedRectangle, CardinalDirection)
 * @see RectangleSystem#getNeighborsFromSide(EnhancedRectangle, CardinalDirection)
 */
public Set<EnhancedRectangle> getRectanglesCloseToSideOrBorder(EnhancedRectangle r, CardinalDirection side) {
	Set<EnhancedRectangle> rectanglesFromThatSide = new HashSet<>();
	// TODO: Add somewhere examples of such rectangles as in comment below.
	    /*
	     * Not only neighbors can shorten free segments, but also the rectangles
		 * that touch this rectangle only with their border can
		 * too. That's why we check all the rectangles, and not only
		 * the neighbors.
		 */
	if (side == CardinalDirection.N) {
		for (EnhancedRectangle neighbor : content) {
		        /*
		         * The part starting from Utils.integersRangeIntersection in
				 * each case checks if a neighbor rectangle touches _side_ (not
				 * border!) of the rectangle r with its direction _or_ border.
				 */
			if (neighbor.y + neighbor.height + borderWidth == r.y && Utils
				.integersRangeIntersection(
					neighbor.x - borderWidth,
					neighbor.x + neighbor.width - 1 + borderWidth,
					r.x - borderWidth,
					r.x + r.width - 1 + borderWidth) > 0) {
				rectanglesFromThatSide.add(neighbor);
			}
		}

	} else if (side == CardinalDirection.E) {
		for (EnhancedRectangle neighbor : content) {
			if (
				neighbor.x == r.x + r.width + borderWidth
					&& Utils.integersRangeIntersection(
					neighbor.y - borderWidth,
					neighbor.y + neighbor.height - 1 + borderWidth,
					r.y - borderWidth,
					r.y + r.height - 1 + borderWidth
				) > 0
				) {
				rectanglesFromThatSide.add(neighbor);
			}
		}
	} else if (side == CardinalDirection.S) {
		for (EnhancedRectangle neighbor : content) {
			if (neighbor.y == r.y + r.height + borderWidth && Utils
				.integersRangeIntersection(
					neighbor.x - borderWidth,
					neighbor.x + neighbor.width - 1 + borderWidth,
					r.x - borderWidth,
					r.x + r.width - 1 + borderWidth) > 0) {
				rectanglesFromThatSide.add(neighbor);
			}
		}
	} else {
		// if (direction == SideTest.W)
		for (EnhancedRectangle neighbor : content) {
			if (neighbor.x + neighbor.width + borderWidth == r.x && Utils
				.integersRangeIntersection(
					neighbor.y - borderWidth,
					neighbor.y + neighbor.height - 1 + borderWidth,
					r.y - borderWidth,
					r.y + r.height - 1 + borderWidth) > 0) {
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
public Set<EnhancedRectangle> outerRectanglesSet() {
	return outerSegments.getOuterRectangles();
}

/**
 * Returns a set of EnhancedRectangles that touch a EnhancedRectangle from a particular direction.
 *
 * @param r
 * 	A EnhancedRectangle to seek neighbors of.
 * @param side
 * 	From which direction to seek for neighbors.
 * @return All neighbors from that direction.
 */
public Set<EnhancedRectangle> getNeighborsFromSide(EnhancedRectangle r, CardinalDirection side) {
	Set<EnhancedRectangle> neighborsFromThatSide = new HashSet<>();
	for (Neighborship e : graph.edgesOf(r)) {
		if (graph.getEdgeSource(e) == r && getNeighborSide(
			r,
			graph.getEdgeTarget(e)) == side) {
			neighborsFromThatSide.add(graph.getEdgeTarget(e));
		} else if (graph.getEdgeTarget(e) == r && getNeighborSide(
			r,
			graph.getEdgeSource(e)) == side) {
			neighborsFromThatSide.add(graph.getEdgeSource(e));
		}
	}
	return neighborsFromThatSide;
}

/**
 * Searches for neighbors in graph.
 *
 * @param r
 * 	A rectangle from this rectangle system.
 * @return A set of all neighbors of that rectangle.
 */
public Set<EnhancedRectangle> getNeighbors(EnhancedRectangle r) {
	Set<EnhancedRectangle> neighbors = new HashSet<>();
	for (Neighborship e : graph.edgesOf(r)) {
		if (graph.getEdgeSource(e) == r) {
			neighbors.add(graph.getEdgeTarget(e));
		} else if (graph.getEdgeTarget(e) == r) {
			neighbors.add(graph.getEdgeSource(e));
		}
	}
	return neighbors;
}

/**
 * Returns a segment inside EnhancedRectangle r1 by which r1 touches r2.
 *
 * @param r1
 * 	A rectangle from this rectangle system.
 * @param r2
 * 	Another rectangle from this rectangle system.
 * @return A segment that lies inside r1 close to its borders, and is located in front of r2.
 */
Segment getAdjacencySegment(EnhancedRectangle r1, EnhancedRectangle r2) {
	if (!areRectanglesNear(r1, r2)) {
		throw new IllegalArgumentException(
			"Both rectangles must be near each other: " + r1 + " " + r2);
	}
	CardinalDirection side = getNeighborSide(r1, r2);
	switch (side) {
		case N:
			return new Segment(Math.max(r1.x, r2.x), r1.y, Math.min(
				r1.x + r1.width - r2.x,
				r2.x + r2.width - r1.x), Orientation.HORIZONTAL);
		case E:
			return new Segment(
				r1.x + r1.width - 1,
				Math.max(r1.y, r2.y),
				Math.min(r1.y + r1.height - r2.y, r2.y + r2.height - r2.y),
				Orientation.VERTICAL);
		case S:
			return new Segment(
				Math.max(r1.x, r2.x),
				r1.y + r1.height - 1,
				Math.min(r1.x + r1.width - r2.x, r2.x + r2.width - r1.x),
				Orientation.HORIZONTAL);
		case W:
		default:
			return new Segment(r1.x, Math.max(r1.y, r2.y), Math.min(
				r1.y + r1.height - r2.y,
				r2.y + r2.height - r1.y), Orientation.VERTICAL);
	}
}

@Override
public EnhancedRectangle addRectangle(EnhancedRectangle r) {
	super.addRectangle(r);
	graph.addVertex(r);
	buildEdgesWith(r);
	return r;
}

private void buildEdgesWith(EnhancedRectangle r) {
	for (Orientation orientation : Orientation.values()) {
		TreeSet<EnhancedRectangle> treeSet = sortedRectangles.get(orientation);
		ArrayList<Map<EnhancedRectangle, CardinalDirection>> neighbors = findNeighborsInSortedSet(r, orientation);
		for (Map.Entry<EnhancedRectangle, CardinalDirection> e : neighbors.get(0).entrySet()) {
			addEdgeBetween(r, e.getKey(), e.getValue(), Neighborship.NeighborshipType.NEIGHBORSHIP);
		}
		for (Map.Entry<EnhancedRectangle, CardinalDirection> e : neighbors.get(1).entrySet()) {
			addEdgeBetween(r, e.getKey(), e.getValue(), Neighborship.NeighborshipType.UNION);
		}
		// Final value of type is 2
//		System.out.println("Neighbours of "+shortDef(r)+" "+orientation+" are "+neighbors.keySet().stream().map(e -> shortDef(e)).collect(Collectors.toList()));
		treeSet.add(r);
	}
}

private String shortDef(EnhancedRectangle r) {
	CardinalDirection dir1 = r.y == 0 ? CardinalDirection.N : CardinalDirection.S;
	CardinalDirection dir2 = r.x == 0 ? CardinalDirection.W : CardinalDirection.E;
	return "[" + dir1 + " " + dir2 + " " + r.width + " " + r.height + "]";
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
private void addEdgeBetween(EnhancedRectangle r1, EnhancedRectangle r2, CardinalDirection sourceRecOccupiedSide, Neighborship.NeighborshipType type) {
	graph.addEdge(r1, r2, new Neighborship(sourceRecOccupiedSide, type));
}

/**
 * Comuptes and returns both "near" and "united" neighbors of a newly added rectangle.
 *
 * @param r
 * 	The original rectangle — the one we search for neighbors of.
 * @param orientation
 * 	Neighbors from which side of original rectangle are being found. If rectangles are supposed to touch N and S sides,
 * 	then orientation is VERTICAL, else it is HORIZONTAL.
 * @return List of two maps from a neighbor rectangle to a direction it is from original rectangle. First index of the
 *         list contains neighbors of type Near, second index contains neighbors of type United.
 */
private ArrayList<Map<EnhancedRectangle, CardinalDirection>> findNeighborsInSortedSet(EnhancedRectangle r, Orientation orientation) {
	TreeSet<EnhancedRectangle> treeSet = sortedRectangles.get(orientation);
	ArrayList<Map<EnhancedRectangle, CardinalDirection>> answer = new ArrayList<>();
	Map<EnhancedRectangle, CardinalDirection> nears = new HashMap<>();
	Map<EnhancedRectangle, CardinalDirection> uniteds = new HashMap<>();
	answer.add(nears);
	answer.add(uniteds);
	int distance = 0;
	CardinalDirection decreasingSide = orientation.isHorizontal() ? CardinalDirection.W : CardinalDirection.N;
	CardinalDirection increasingSide = orientation.isHorizontal() ? CardinalDirection.E : CardinalDirection.S;
	for (CardinalDirection dir : new CardinalDirection[]{decreasingSide, increasingSide}) {
		EnhancedRectangle bufferRectangle;
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
					// Rectangles will have Neighborship of type Near
					nears.put(bufferRectangle, dir);
				} else if (distance == 0) {
					// Rectangles will have Neighborship of type United
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

private boolean areProbableNeighborsByOrientation(EnhancedRectangle r1, EnhancedRectangle r2, Orientation orientation) {
	if (!areRectanglesNear(r1, r2)) {
		return false;
	} else if (r1.overlapsByDynamicRange(r2, orientation.reverted())) {
		return false;
	}
	return true;
}

private Orientation getOrientationOfTouching(EnhancedRectangle r1, EnhancedRectangle r2) {
	assert areRectanglesNear(r1, r2);
	return getNeighborSide(r1, r2).getOrientation();
}

/**
 * Excludes a rectangle from this system. Neighbor rectangles of the excluded rectangle will become outer.
 *
 * @param r
 * 	A EnhancedRectangle that exists in this RectangleSystem.
 */
@Override
public void excludeRectangle(EnhancedRectangle r) {
	super.excludeRectangle(r);
	for (EnhancedRectangle neighbor : getNeighbors(r)) {
		assert graph.containsEdge(neighbor, r);
		outerSegments.uncomputeSide(neighbor, getNeighborSide(neighbor, r));
	}
	outerSegments.uncomputeRectangle(r);
	graph.removeVertex(r);
}

/**
 * Checks if a rectangle is one of the outer rectangles.
 *
 * @param r
 * 	A rectangle from this rectangle system.
 * @return true if {@code r} is outer, false if it is inner.
 * @see OuterSegments
 */
public boolean isRectangleOuter(EnhancedRectangle r) {
	return outerSegments.getOuterSidesOf(r).size() > 0;
}

/**
 * Checks if two {@link EnhancedRectangle}s have exactly {@code borderWidth} cells between their closest sides. Such
 * rectangles are considered "neighbors" in this individual RectangleSystem. e
 *
 * @param r1
 * 	A rectangle from this rectangle system.
 * @param r2
 * 	Another rectangle from this rectangle system.
 * @return
 */
public boolean areRectanglesNear(Rectangle r1, Rectangle r2) {
	if (r1.x + r1.width + borderWidth == r2.x || r2.x + r2.width + borderWidth == r1.x) {
		// Rectangles share a vertical line
		int a1 = r1.y;
		int a2 = r1.y + r1.height - 1;
		int b1 = r2.y;
		int b2 = r2.y + r2.height - 1;
		int intersection = Utils.integersRangeIntersection(a1, a2, b1, b2);
		return intersection >= 1;
	} else if (r1.y + r1.height + borderWidth == r2.y || r2.y + r2.height + borderWidth == r1.y) {
		// Rectangles share a horizontal line
		int a1 = r1.x;
		int a2 = r1.x + r1.width - 1;
		int b1 = r2.x;
		int b2 = r2.x + r2.width - 1;
		int intersection = Utils.integersRangeIntersection(a1, a2, b1, b2);
		return intersection >= 1;
	} else {
		// Rectangles definitely don't share horizontal or vertical lines
		return false;
	}
}

/**
 * Checks if a EnhancedRectangle exists in this RectangleSystem.
 *
 * @param r
 * 	A rectangle.
 * @return True if {@code r} exists in this rectangle system, false otherwise.
 */
public boolean hasRectangle(EnhancedRectangle r) {
	return content.contains(r);
}

/**
 * Make this EnhancedRectangle's graph into a directed tree. The shape of a tree will be determined randomly, but a root
 * vertex can be set.
 */
public void convertGraphToDirectedTree() {
	// Randomly select an existing vertex to be a root vertex of a tree
	EnhancedRectangle rootVertex = null;
	for (EnhancedRectangle r : graph.vertexSet()) {
		rootVertex = r;
		break;
	}
	    /*
	     * Another graph that will contain the same vertices that this.graph,
		 * but with single tree-form edges.
		 */
	SimpleGraph<EnhancedRectangle, Neighborship> graph2 = new SimpleGraph<>(Neighborship.class);
	graph2.addVertex(rootVertex);
	// Vertex set of the new graph
	Set<EnhancedRectangle> newVertexSet = graph2.vertexSet();
	for (int i = 0, l = graph.vertexSet().size(); i < l; i++) {
		loop:
		for (EnhancedRectangle r : newVertexSet) {// Select a random
			for (Neighborship e : graph.edgesOf(r)) {
				EnhancedRectangle r2 = graph.getEdgeTarget(e);
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
 * <p> Finds all double edges and makes them single. There is no way to predict which vertex will be the sources or the
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
 * Randomly removes outer EnhancedRectangles. Iterates over all outer rectangles with a {@code chance} percent chance to
 * remove each of them.
 *
 * @param depth
 * 	How many runs to make.
 * @param chance
 * 	A chance to remove.
 */
public void nibbleSystem(int depth, int chance) {
	for (int k = 0; k < depth; k++) {
		Set<EnhancedRectangle> removedRectangles = new HashSet<>();
		for (EnhancedRectangle r : outerRectanglesSet()) {
			if (Chance.roll(chance)) {
				removedRectangles.add(r);
			}
		}
		for (EnhancedRectangle r : removedRectangles) {
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
	Set<EnhancedRectangle> copy = new HashSet<>(content);
	for (EnhancedRectangle r : copy) {
		if (r.isInCircle(x, y, radius)) {
			excludeRectangle(r);
			answer.add(r);
		}
	}
	return answer;
}

/**
 * <p> Splits rectangle into two rectangles, one of them being the initial rectangle, and another one a new rectangle.
 * Rectangle under current number will be the left one (if dir == DirectionToBERemoved.V) or the top one (if dir ==
 * DirectionToBERemoved.H). </p> <p> If width < 0, then a rectangle width width/height = -width from right side/bottom
 * will be cut off, and under current number still stay right/bottom rectangle, but the old one will still be the
 * left/top one, and the returned one will be the right/bottom one. </p>
 *
 * @param r
 * 	A rectangle from this RectangleSystem.
 * @param orientation
 * 	Horizontally or vertically.
 * @param widthOrHeight
 * 	How much to cut.
 * @return A new rectangle that was created by splitting the old one.
 */
public EnhancedRectangle splitRectangle(EnhancedRectangle r, Orientation orientation, int widthOrHeight, boolean reverseAreas) {
	// TODO: Optimize size() calls
	if (widthOrHeight == 0) {
		throw new IllegalArgumentException("Argument width can't be 0");
	}
	if (!hasRectangle(r)) {
		throw new IllegalArgumentException("EnhancedRectangle " + r + " doesn't exist in this RectangleSystem");
	}
	boolean negativeWidth = widthOrHeight < 0;
	if (orientation.isVertical()) {
		// Vertically
		if (negativeWidth) {
			// This will be the width of the old EnhancedRectangle
			widthOrHeight = r.width + widthOrHeight - borderWidth;
		}
		if (widthOrHeight > r.width) {
			throw new IllegalArgumentException(
				"Width " + widthOrHeight + " in vertical splitting is too big");
		}
		if (widthOrHeight < 1) {
			widthOrHeight = widthOrHeight + borderWidth - r.width;
			throw new IllegalArgumentException(
				"Width " + widthOrHeight + " in vertical splitting is too big");
		}
		int newStartX = r.x + widthOrHeight + borderWidth;
		EnhancedRectangle newRec;
		if (reverseAreas) {
			newRec = new EnhancedRectangle(r.x, r.y, widthOrHeight, r.height);
			resizeRectangle(r,
				newStartX,
				r.y,
				r.width - widthOrHeight - borderWidth,
				r.height);
		} else {
			newRec = new EnhancedRectangle(
				newStartX,
				r.y,
				r.width - widthOrHeight - borderWidth,
				r.height);
			resizeRectangle(r, widthOrHeight, r.height);
		}
		return addRectangle(newRec);
	} else {
		// Horizontally
		if (negativeWidth) {
			// This will be the width of the old EnhancedRectangle
			widthOrHeight = r.height + widthOrHeight - borderWidth;
		}
		if (widthOrHeight > r.height) {
			throw new IllegalArgumentException(
				"Width " + widthOrHeight + " in horizontal splitting is too big");
		}
		if (widthOrHeight < 1) {
			widthOrHeight = widthOrHeight + borderWidth - r.height;
			throw new IllegalArgumentException(
				"Width " + widthOrHeight + " in horizontal splitting is too big");
		}
		int newStartY = r.y + widthOrHeight + borderWidth;
		EnhancedRectangle newRec;
		// Though argument is called width, it is height if a rectangle
		// is split vertically
		if (reverseAreas) {
			newRec = new EnhancedRectangle(r.x, r.y, r.width, widthOrHeight);
			resizeRectangle(r,
				r.x,
				newStartY,
				r.width,
				r.height - widthOrHeight - borderWidth);
		} else {
			newRec = new EnhancedRectangle(
				r.x,
				newStartY,
				r.width,
				r.height - widthOrHeight - borderWidth);
			resizeRectangle(r, r.width, widthOrHeight);
		}
		return addRectangle(newRec);
	}
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
private void resizeRectangle(EnhancedRectangle r, int newWidth, int newHeight) {
	excludeRectangle(r);
	r.width = newWidth;
	r.height = newHeight;
	addRectangle(r);
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
private void resizeRectangle(EnhancedRectangle r, int newX, int newY, int newWidth, int newHeight) {
	excludeRectangle(r);
	r.x = newX;
	r.y = newY;
	r.width = newWidth;
	r.height = newHeight;
	addRectangle(r);
}

public EnhancedRectangle cutRectangleFromSide(EnhancedRectangle rectangleToCut, CardinalDirection side, int depth) {
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

/**
 * Changes {@link RectangleSystem#borderWidth} of this RectangleSystem and expands all EnhancedRectangles by that amount
 * of cells to still be neighbors with their neighbors.
 *
 * @param depth
 * 	Difference between the old borderWidth and the desired borderWidth.
 */
public void expandRectanglesToBorder(int depth) {
	if (borderWidth < depth * 2) {
		throw new Error(
			"border width " + borderWidth + " is too thin for expanding each rectangle by " + depth);
	}
	for (Rectangle r : content) {
		r.x -= depth;
		r.y -= depth;
		r.width += depth * 2;
		r.height += depth * 2;
	}
	borderWidth -= depth * 2;
}

public EnhancedRectangle findRectangleWithMostNeigbors() {
	EnhancedRectangle answer = null;
	int maxNumberOfEdges = 0;
	for (EnhancedRectangle r : graph.vertexSet()) {
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
 * Returns an iterable object that allows iteration over all neighbors of a certain EnhancedRectangle in a certain
 * order.
 *
 * @param r
 * 	A rectangle whose neighbors you need to iterate over.
 * @see NeighboursIterable
 */
public NeighboursIterable getNeighboursIterable(EnhancedRectangle r) {
	return new NeighboursIterable(this, r);
}

public Collection<Segment> getOuterSegmentsOf(EnhancedRectangle r, CardinalDirection side) {
	return outerSegments.getOuterSegmentsOf(r, side);
}

private static class RectangleComparator implements Comparator<EnhancedRectangle> {

	private final Orientation orientation;

	RectangleComparator(Orientation orientation) {

		this.orientation = orientation;
	}

	@Override
	public int compare(EnhancedRectangle o1, EnhancedRectangle o2) {
		int staticCoord1 = o1.getMinStaticCoord(orientation);
		int staticCoord2 = o2.getMinStaticCoord(orientation);
		if (staticCoord1 == staticCoord2) {
			int dDynamicCoord = o1.getMinDynamicCoord(orientation) - o2.getMinDynamicCoord(orientation);
			assert dDynamicCoord != 0 || o1 == o2;
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
	final Map<EnhancedRectangle, Map<CardinalDirection, ImmutableSet<Segment>>> recsToSegments = new HashMap<>();
	private Map<EnhancedRectangle, Set<CardinalDirection>> allOuterSides;

	Collection<Segment> getOuterSegmentsOf(EnhancedRectangle r, CardinalDirection side) {
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

	void uncomputeSide(EnhancedRectangle r, CardinalDirection side) {
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
	public Map<EnhancedRectangle, Set<CardinalDirection>> outerSidesOfRectangles() {
		ImmutableMap.Builder<EnhancedRectangle, Set<CardinalDirection>> builder = ImmutableMap.builder();
		for (Map.Entry<EnhancedRectangle, Map<CardinalDirection, ImmutableSet<Segment>>> entry : recsToSegments.entrySet()) {
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
	private Collection<Segment> computeOuterSegmentsOf(EnhancedRectangle r, CardinalDirection side) {
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

	private Collection<CardinalDirection> getOuterSidesOf(EnhancedRectangle r) {
		Builder<CardinalDirection> builder = ImmutableSet.builder();
		for (CardinalDirection dir : CardinalDirection.values()) {
			Collection<Segment> segments = getOuterSegmentsOf(r, dir);
			if (!segments.isEmpty()) {
				builder.add(dir);
			}
		}
		return builder.build();
	}

	private Set<EnhancedRectangle> getOuterRectangles() {
		computeForAllRectangles();
		return recsToSegments.keySet();
	}

	private void computeForAllRectangles() {
		for (EnhancedRectangle r : content) {
			if (!recsToSegments.containsKey(r)) {
				for (CardinalDirection side : CardinalDirection.values()) {
					computeOuterSegmentsOf(r, side);
				}
			}
		}
	}

	public ImmutableMap<EnhancedRectangle, Collection<CardinalDirection>> getAllOuterSides() {
		computeForAllRectangles();
		ImmutableMap.Builder<EnhancedRectangle, Collection<CardinalDirection>> builder = ImmutableMap.builder();
		for (Map.Entry<EnhancedRectangle, Map<CardinalDirection, ImmutableSet<Segment>>> entry : recsToSegments.entrySet()) {
			if (!entry.getValue().isEmpty()) {
				builder.put(entry.getKey(), entry.getValue().keySet());
			}
		}
		return builder.build();
	}

	public void uncomputeRectangle(EnhancedRectangle r) {
		recsToSegments.remove(r);
	}
}

public class NeighboursIterable implements Iterable<EnhancedRectangle> {
	private final RectangleSystem rs;
	private final EnhancedRectangle centralRectangle;
	private ArrayList<EnhancedRectangle> orderedRectangles = new ArrayList<>();
	private boolean orderSet = false;
	private EnhancedRectangle startingNeighbour;
	private double centerX;
	private double centerY;

	NeighboursIterable(RectangleSystem rs, EnhancedRectangle centralRectangle) {
		this.rs = rs;
		this.centralRectangle = centralRectangle;
		this.centerX = centralRectangle.getCenterX();
		this.centerY = centralRectangle.getCenterY();
	}

	@Override
	public Iterator<EnhancedRectangle> iterator() {
		if (!orderSet) {
			throw new Error(
				"You must first set this Iterable's type of order with a set* method");
		}
		return orderedRectangles.iterator();
	}

	/**
	 * Prepares the iterable to iterate over all rectangles in clockwise order. Before calling this method, you must set a
	 * starting rectangle either implicitly with {@link NeighboursIterable#setStartingNeigbour(EnhancedRectangle)} or
	 * explicitly with {@link NeighboursIterable#setRandomStartingNeighbour()}.
	 *
	 * @return This same object, so you can use a chain of invocations to its methods right inside for (:) loop heading.
	 */
	public NeighboursIterable setClockwiseOrder() {
		orderedRectangles = getRectanglesSortedClockwise();
		return this;
	}

	public NeighboursIterable setCounterClockwiseOrder() {
		ArrayList<EnhancedRectangle> rectangles = getRectanglesSortedClockwise();
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
	 * Returns a list of rectangles sorted clockwise, but doesn't consider the {@link NeighboursIterable#startingNeighbour}
	 * rectangle. Instead the first rectangle is the one with a center point closest to 0 angle in a polar coordinate
	 * system whose 0 ray is collinear to world's Cartesian system's x-axis.
	 */
	private ArrayList<EnhancedRectangle> getRectanglesSortedClockwise() {
		ArrayList<AngleRectanglePair> rectangles = new ArrayList<>();
		int startingNeighbourIndex = -1;
		for (EnhancedRectangle r : rs.getNeighbors(centralRectangle)) {
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
		ArrayList<EnhancedRectangle> answer = new ArrayList<>();
		for (int i = startingNeighbourIndex, l = rectangles.size(); i < l; i++) {
			answer.add(rectangles.get(i).rectangle);
		}
		for (int i = 0; i < startingNeighbourIndex; i++) {
			answer.add(rectangles.get(i).rectangle);
		}
		return answer;
	}

	public NeighboursIterable setStartingNeigbour(EnhancedRectangle neighbour) {
		startingNeighbour = neighbour;
		return this;
	}

	public NeighboursIterable setRandomStartingNeighbour() {
		startingNeighbour = Utils.getRandomElement(rs.getNeighbors(centralRectangle));
		return this;
	}

	class AngleRectanglePair {
		private final double angle;
		private final EnhancedRectangle rectangle;

		AngleRectanglePair(double angle, EnhancedRectangle rectangle) {
			this.angle = angle;
			this.rectangle = rectangle;
		}
	}
}
}