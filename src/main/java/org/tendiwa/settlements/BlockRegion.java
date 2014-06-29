package org.tendiwa.settlements;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Vector2D;

import java.awt.Color;
import java.util.*;

/**
 *
 */
public class BlockRegion extends EnclosedBlock {
	private static final double EPSILON = 1e-10;
	private final Map<Point2D, Point2D> roadPoints;
	private final TObjectDoubleMap<Node> roadLengths;
	private final Random random;
	public static TestCanvas canvas;

	/**
	 * Constructor for an original block to be divided into lesser blocks. {@link #roadPoints} is filled with all
	 * roads from {@code outline}.
	 *
	 * @param outline
	 * 	Vertices of a circular road graph that will form a circular list.
	 * @param random
	 */
	public BlockRegion(List<Point2D> outline, Random random) {
		super(outline);
		assert !outline
			.stream()
			.anyMatch(p -> p.y > 10000 || p.x > 10000);
		assert outline.size() > 2;
		this.random = new Random(random.nextInt());
		this.roadPoints = createRoadPoints();
		this.roadLengths = computeRoadLengths();
	}

	private Map<Point2D, Point2D> createRoadPoints() {
		Map<Point2D, Point2D> answer = new HashMap<>();
		Node node = startNode;
		do {
			answer.put(node.point, node.next.point);
			node = node.next;
		} while (node != startNode);
		return answer;
	}

	/**
	 * Constructor for sub-blocks divided from the original block.
	 *
	 * @param createdNode
	 * 	One of nodes of a circular list for this sub-block.
	 * @param roadPoints
	 * 	Points of nodes where the original roads start and end.
	 * @param random
	 */
	BlockRegion(Node createdNode, Map<Point2D, Point2D> roadPoints, Random random) {
		super(createdNode);
		this.random = new Random(random.nextInt());
		this.roadPoints = roadPoints;
		this.roadLengths = computeRoadLengths();
	}

	private TObjectDoubleMap<Node> computeRoadLengths() {
		TObjectDoubleMap<Node> roadLengths = new TObjectDoubleHashMap<>();
		Node node = this.startNode;
		do {
			computeRoadLength(node, roadLengths);
			node = node.next;
		} while (node != this.startNode);
		return roadLengths;
	}


	/**
	 * Saves distance from a node to the next node.
	 *
	 * @param node
	 * 	A node.
	 * @param roadLengths
	 * 	A map with road lengths.
	 */
	private void computeRoadLength(Node node, TObjectDoubleMap<Node> roadLengths) {
		assert node.next != null;
		double roadLength = node.point.distanceTo(node.next.point);
		assert roadLength > 3;
		roadLengths.put(node, roadLength);
	}

	private double getRoadLength(Node start) {
		assert start != null;
		assert roadLengths.containsKey(start);
		return roadLengths.get(start);
	}

	/**
	 * [Kelly Figure 54]
	 * <p>
	 * Consecutively splits a block into lesser blocks until blocks are small enough.
	 *
	 * @param lotWidth
	 * @param lotDepth
	 * @param lotDeviance
	 * @return
	 */
	public Set<BlockRegion> subdivideLots(double lotWidth, double lotDepth, double lotDeviance) {
		Queue<BlockRegion> queue = new LinkedList<>();
		Set<BlockRegion> output = new HashSet<>();
		queue.add(this);
		double splitSize;
		while (!queue.isEmpty()) {
			BlockRegion region = queue.poll();
			LongestRoadAndNonRoadPair longest = region.getLongestEdges();
			Node longestEdgeStart = longest.road;
			if (longestEdgeStart == null || region.getRoadLength(longestEdgeStart) < lotWidth) {
				longestEdgeStart = longest.nonRoad;
				if (longestEdgeStart == null || region.getRoadLength(longestEdgeStart) < lotDepth) {
					output.add(region);
					continue;
				} else {
					splitSize = lotDepth;
				}
			} else {
				splitSize = lotWidth;
			}
			Point2D splitStart = getSplitStart(longestEdgeStart, splitSize, lotDeviance);
			Point2D splitEnd = splitStart.add(
				Vector2D.fromStartToEnd(
					longestEdgeStart.point, longestEdgeStart.next.point
				).cross()
			);
			Set<BlockRegion> newRegions = splitRegion(region, splitStart, splitEnd);
//			assert !newRegions.isEmpty();
			for (BlockRegion newRegion : newRegions) {
				canvas.draw(newRegion, DrawingEnclosedBlock.withColor(Color.red));
			}

			queue.addAll(newRegions);
		}
		return output;
	}

	private LongestRoadAndNonRoadPair getLongestEdges() {
		return new LongestRoadAndNonRoadPair();
	}

	/**
	 * [Kelly Figure 58]
	 * <p>
	 * Splits a single polygon into multiple polygons with a splitting line.
	 *
	 * @param region
	 * 	A polygon to split.
	 * @param a
	 * 	One end of a splitting line.
	 * @param b
	 * 	Another end of a splitting line.
	 * @return A set of polygons split by a line from the given polygon.
	 */
	private Set<BlockRegion> splitRegion(BlockRegion region, Point2D a, Point2D b) {
		Set<BlockRegion> output = new HashSet<>();
		Vector2D ab = Vector2D.fromStartToEnd(a, b);
		TObjectDoubleMap<Node> positions = computeEdgeStartPositions(region, a, b);
		Set<Node> visited = new HashSet<>();
		Node node = region.startNode;
		List<Node> createdNodes = new LinkedList<>();
		do {
			Node nextNode = node.next; // Need to buffer the next node because next node is changed
			if (positions.get(node) > 0 && positions.get(node.next) <= 0
				|| positions.get(node) <= 0 && positions.get(node.next) > 0) {
				Vector2D cd = Vector2D.fromStartToEnd(node.point, node.next.point);
				double denom = ab.getX() * cd.getY() - ab.getY() * cd.getX();
				if (Math.abs(denom) < EPSILON) {
					throw new GeometryException("Trying to find an intersection of parallel lines");
				}
				Vector2D ca = Vector2D.fromStartToEnd(node.point, a);
				double r = (ca.getY() * cd.getX() - ca.getX() * cd.getY()) / denom;
				double s = (ca.getY() * ab.getX() - ca.getX() * ab.getY()) / denom;
				Node intersectionEdge;
				if (Math.abs(positions.get(node)) < EPSILON) {
					intersectionEdge = node;
				} else if (Math.abs(positions.get(node.next)) < EPSILON) {
					intersectionEdge = node.next;
				} else {
					Point2D newNodePosition = node.point.add(cd.multiply(s));
					intersectionEdge = node.insert(newNodePosition);

					computeRoadLength(node, roadLengths);
					computeRoadLength(intersectionEdge, roadLengths);
					if (isRoad(node, nextNode)) {
						roadPoints.put(node.point, newNodePosition);
						roadPoints.put(intersectionEdge.point, nextNode.point);
					}
				}
				positions.put(intersectionEdge, r);
				createdNodes.add(intersectionEdge);
			}
			node = nextNode;
		} while (node != region.startNode);
		createdNodes.sort((o1, o2) -> (int) Math.signum(positions.get(o1) - positions.get(o2)));
		assert createdNodes.size() % 2 == 0;
		for (int i = 0; i < createdNodes.size(); i += 2) {
			bridge(createdNodes.get(i), createdNodes.get(i + 1));
		}
		for (Node createdNode : createdNodes) {
			boolean skipDuplicate = visitRegionEdges(createdNode, visited);
			if (!skipDuplicate) {
				output.add(new BlockRegion(createdNode, roadPoints, new Random(random.nextInt())));
			}
		}
		return output;

	}

	private static int chainLength(Node node) {
		Node start = node;
		Node current = node;
		int size = 0;
		do {
			size++;
			current = node.next;
		} while (current != start);
		return size;
	}


	/**
	 * Fills visited set with descendants of a node until it reaches an already visited descendant.
	 *
	 * @param startNode
	 * 	A node.
	 * @param visited
	 * 	A set of nodes that were visited.
	 * @return true if it reached a node that has already been visited, false otherwise.
	 */
	private boolean visitRegionEdges(Node startNode, Set<Node> visited) {
		Node node = startNode;
		do {
			if (visited.contains(node)) {
				return true;
			}
			visited.add(node);
			node = node.next;
		} while (node != startNode);
		return false;
	}

	private void bridge(Node start, Node end) {
		if (end.next == start) {
			Node buf = start;
			start = end;
			end = buf;
		}
		Node startNext = start.next;
		Node nextCopy = new Node(end.point);
		Node startCopy = new Node(start.point);
		start.setNext(nextCopy);
		nextCopy.setNext(end.next);
		end.setNext(startCopy);
		startCopy.setNext(startNext);
		computeRoadLength(start, roadLengths);
		computeRoadLength(end, roadLengths);
		computeRoadLength(start.next, roadLengths);
		computeRoadLength(end.next, roadLengths);
	}

	/**
	 * For each node, computes its start's position relative to the segment ab. Position < 0 means that start's on
	 * the left, >0 means on the right, 0 means it's exactly on the line ab.
	 *
	 * @param region
	 * 	A block that contains all nodes for whose starts are positions computed.
	 * @param a
	 * 	Start of the dividing line.
	 * @param b
	 * 	End of the dividing line.
	 * @return A map from block's nodes to positions of those nodes relative to ab.
	 */
	private TObjectDoubleMap<Node> computeEdgeStartPositions(BlockRegion region, Point2D a, Point2D b) {
		Vector2D ab = Vector2D.fromStartToEnd(a, b);
		double lsq = a.distanceTo(b);
		lsq = lsq * lsq;
		TObjectDoubleMap<Node> leftOrRightPositions = new TObjectDoubleHashMap<>();
		Node node = region.startNode;
		do {
			Vector2D ac = Vector2D.fromStartToEnd(a, node.point);
			leftOrRightPositions.put(node, (-ac.getY() * ab.getX() + ac.getX() * ab.getY()) / lsq);
			node = node.next;
		} while (node != region.startNode);
		return leftOrRightPositions;
	}

	/**
	 * [Kelly Figure 56]
	 * <p>
	 * Computes a beginning point of a line splitting the polygon.
	 *
	 * @param longestEdgeStart
	 * 	An edge from which the line originates.
	 * @param splitSize
	 * 	Target size of a split piece of {@code longestEdgeStart}.
	 * @param lotDeviance
	 * 	Deviance of split size.
	 * @return A point on edge {@code longestEdgeStart} from which the split line originates.
	 */
	private Point2D getSplitStart(Node longestEdgeStart, double splitSize, double lotDeviance) {
		assert getRoadLength(longestEdgeStart) == longestEdgeStart.point.distanceTo(longestEdgeStart.next.point);
		double factor = Math.round(getRoadLength(longestEdgeStart) / splitSize);
		assert factor > 0;
		double fraction = 1 / factor;
		double midPosition = Math.round(factor / 2) * fraction;
		assert midPosition < 1;

		Vector2D longestEdgeVector = Vector2D.fromStartToEnd(
			longestEdgeStart.point, longestEdgeStart.next.point
		);
		double multiplier = midPosition + (lotDeviance * (random.nextDouble() - 0.5) * fraction);
		assert multiplier > 0 && multiplier < 1;
		return longestEdgeStart.point.add(longestEdgeVector.multiply(multiplier));
	}

	/**
	 * Longest road and non-road are computes as a pair because it can be done in a single loop over all nodes,
	 * in order to not run the loop twice.
	 */
	private class LongestRoadAndNonRoadPair {
		/**
		 * Longest road. May be null if there are only non-roads (doesn't happen actually,
		 * but is asserted by the algorithm).
		 */
		private final Node road;
		/**
		 * Longest non road. May be null if there are only road (this one does happen).
		 *
		 * @see #road
		 */
		private final Node nonRoad;

		LongestRoadAndNonRoadPair() {
			Node current = startNode;
			Node longestRoad = null;
			Node longestNonRoad = null;
			double maxRoadLength = Double.MIN_VALUE;
			double maxNonRoadLength = Double.MIN_VALUE;
			do {
				double length = current.point.distanceTo(current.next.point);
				if (isRoad(current, current.next)) {
					if (length > maxRoadLength) {
						maxRoadLength = length;
						longestRoad = current;
					}
				} else {
					if (length > maxNonRoadLength) {
						maxNonRoadLength = length;
						longestNonRoad = current;
					}
				}
				current = current.next;
			} while (current != startNode);
			assert !(longestRoad == null && longestNonRoad == null);
			this.road = longestRoad;
			this.nonRoad = longestNonRoad;
		}
	}

	private boolean isRoad(Node current, Node next) {
		return roadPoints.containsKey(current.point) && roadPoints.get(current.point).equals(next.point);
	}

}
