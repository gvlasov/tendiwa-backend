package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.*;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.geometry.RayIntersection;

import java.awt.Color;
import java.util.*;

import static org.tendiwa.geometry.Vectors2D.perpDotProduct;

// TODO: Split this class into more classes.
public class SuseikaStraightSkeleton implements StraightSkeleton {

	private final ListOfActiveVertices lav;
	private SkeletonEvent watchEvent;
	//	TestCanvas canvas = new TestCanvas(1, 200, 400);
	DrawableInto canvas = TestCanvas.canvas;
	private final PriorityQueue<SkeletonEvent> queue;
	public final Multimap<Point2D, Point2D> arcs = HashMultimap.create();
	static final double EPSILON = 1e-10;
	private final HashMap<Node, Node> splitNodePairs = new HashMap<>();
	private final MovementRegistry registry;
	private final RegistryOfSplitEventsOnEdges splitEventsRegistry;

	public SuseikaStraightSkeleton(MinimalCycle<Point2D, Segment2D> cycle) {
		// Transform clockwise list to a counter-clockwise list.
		this(Lists.reverse(cycle.vertexList()), true);
	}

	public SuseikaStraightSkeleton(List<Point2D> vertices) {
		this(vertices, false);
	}

	private SuseikaStraightSkeleton(List<Point2D> vertices, boolean trustCounterClockwise) {
//		vertices = Lists.reverse(vertices);
		Node.canvas = canvas;
		OppositeEdgeStartMovement.canvas = canvas;
		if (!trustCounterClockwise && !JTSUtils.isYDownCCW(vertices)) {
			vertices = Lists.reverse(vertices);
		}
//		outputPoints(vertices);
		this.lav = new ListOfActiveVertices(vertices, canvas);


		int size = lav.edges.size();
		this.queue = new PriorityQueue<>(size);
		// [Obdrzalek 1998, paragraph 2.2, algorithm step 1c]
		int i = 0;
		registry = new MovementRegistry(lav.nodes);
		splitEventsRegistry = new RegistryOfSplitEventsOnEdges(lav.nodes);
		for (
			Node node = lav.nodes.getFirst();
			i < size;
			i++, node = node.next
			) {
//			canvas.draw(node.bisector.segment, DrawingSegment2D.withColor(Color.green));
			SkeletonEvent e = computeNearerBisectorsIntersection(node);
			if (e != null) {
				queue.add(e);
			}
		}
		assert !queue.isEmpty();
		while (!queue.isEmpty()) {
			// Convex 2a
			SkeletonEvent point = queue.poll();
			if (point.event == EventType.EDGE) {
				// Convex 2b
				if (point.va.isProcessed() || point.vb.isProcessed()) {
					if (!(point.va.isProcessed() && point.vb.isProcessed())) {
						Node node = point.va.isProcessed() ? point.vb : point.va;
						SkeletonEvent e = computeNearerBisectorsIntersection(node);
						if (e != null) {
							queue.add(e);
						}
					}
					continue;
				}
				assert point.va.next == point.vb : point.va.next.vertex + " " + point.vb.vertex;
				// Convex 2c
				if (point.va.previous.previous == point.vb) {
					connectLast3SegmentsForEdgeEvent(point);
					continue;
				}
				if (point.va.next.next == point.va) {
					eliminate2NodeLav(point);
					continue;
				}
				// Convex 2d
				outputArc(point.va.vertex, point);
				outputArc(point.vb.vertex, point);

				// Convex 2e
				Node node = new Node(
					point.va.previous.currentEdge,
					point.vb.currentEdge,
					point
				);
				node.connectWithPrevious(point.va.previous);
				point.vb.next.connectWithPrevious(node);
				node.computeReflexAndBisector();

				point.va.setProcessed();
				point.vb.setProcessed();
				if (!hasPairOf(point.va)) {
					// Move starts only to edge event nodes.
					registry.getByOriginalEdge(point.va.currentEdge).moveTo(node);
				}
				if (!hasPairOf(point.vb)) {
					registry.getByOriginalEdge(point.vb.currentEdge).moveTo(node);
				}
				if (hasPairOf(point.vb)) {
					Node newEnd = pairOf(point.vb);
					if (!newEnd.isProcessed()) {
						registry.getByOriginalEdge(point.va.currentEdge).moveTo(newEnd);
					}
					registry.getByOriginalEdge(newEnd.currentEdge).moveTo(node);
				}
				if (hasPairOf(point.va)) {
					Node newEnd = pairOf(point.va);
					if (!newEnd.isProcessed()) {
						registry.getByOriginalEdge(point.vb.currentEdge).moveTo(newEnd);
					}
					registry.getByOriginalEdge(newEnd.currentEdge).moveTo(node);
				}
//				draw(node, Color.RED);

				// Convex 2f
				SkeletonEvent e = computeNearerBisectorsIntersection(node);
				if (e != null) {
					queue.add(e);
				}
			} else {
				assert point.event == EventType.SPLIT;
				if (point.va.isProcessed()) {
					continue;
				}
				// Non-convex 2c
				if (point.va.previous.previous.previous == point.va) {
					connectLast3EdgesForSplitEvent(point);
					continue;
				}
				if (point.va.next.next == point.va) {
					eliminate2NodeLav(point);
					continue;
				}
				// Non-convex 2D
				outputArc(point.va.vertex, point);
				if (point.oppositeEdgeStart.isProcessed()) {
					canvas.draw(point.oppositeEdgeStart.bisector.segment, DrawingSegment2D.withColorThin(Color.orange));
					canvas.draw(point.oppositeEdgeStart.currentEdge, DrawingSegment2D.withColorThin(Color.magenta));
					canvas.draw(point, DrawingPoint2D.withColorAndSize(Color.green, 3));
					canvas.draw(point.oppositeEdgeStart.vertex, DrawingPoint2D.withColorAndSize(Color.green, 2));
					assert false;
				}
				// Non-convex 2e

				Node node1 = new Node(
					point.va.previous.currentEdge,
					point.oppositeEdgeEnd.previous.currentEdge,
					point
				);
				Node node2 = new Node(
					point.oppositeEdgeStart.currentEdge,
					point.va.currentEdge,
					point
				);

				Node lav1NextNode;
				if (point.va.isInTheSameLav(point.oppositeEdgeEnd)) {
					lav1NextNode = point.oppositeEdgeEnd;
				} else {
					lav1NextNode = splitEventsRegistry.getNodeFromLeft(point.oppositeEdgeEnd.previous.currentEdge, node1);
				}
				Node lav2PreviousNode;
				if (point.va.isInTheSameLav(point.oppositeEdgeStart)) {
					lav2PreviousNode = point.oppositeEdgeStart;
				} else {
					lav2PreviousNode = splitEventsRegistry.getNodeFromRight(point.oppositeEdgeStart.currentEdge, node2);
				}

				node1.connectWithPrevious(point.va.previous);
				lav1NextNode.connectWithPrevious(node1);
				ImmutableList.copyOf(node1);
//				draw(node1, Color.green);

				node2.connectWithPrevious(lav2PreviousNode);
				point.va.next.connectWithPrevious(node2);
				ImmutableList.copyOf(node2);
//				draw(node2, Color.blue);

				point.va.setProcessed();
				pairSplitNodes(node1, node2);

				splitEventsRegistry.addSplitNode(
					point.oppositeEdgeStartMovement.getStart().currentEdge,
					node1,
					RegistryOfSplitEventsOnEdges.Orientation.LEFT
				);
				splitEventsRegistry.addSplitNode(
					point.oppositeEdgeStartMovement.getStart().currentEdge,
					node2,
					RegistryOfSplitEventsOnEdges.Orientation.RIGHT
				);

				// Non-convex 2f
				integrateNewSplitNode(node1, point, false);
				integrateNewSplitNode(node2, point, true);
			}
		}
		assert !arcs.isEmpty();
//		if (canvas instanceof TestCanvas) {
//			((TestCanvas) canvas).close();
//		}
	}

	private void connectLast3EdgesForSplitEvent(SkeletonEvent point) {
		outputArc(point.va.vertex, point);
		point.va.setProcessed();
		point.vb.setProcessed();
		point.va.previous.setProcessed();
		outputArc(point.vb.vertex, point);
		assert point.va.previous == point.vb.next;
		outputArc(point.va.previous.vertex, point);
	}

	private void eliminate2NodeLav(SkeletonEvent point) {
		assert point.vb != null;
		// Eliminating LAVs consisting only of 2 nodes
		outputArc(point.va.vertex, point.vb.vertex);
		point.va.setProcessed();
		point.vb.setProcessed();
	}

	private void connectLast3SegmentsForEdgeEvent(SkeletonEvent point) {
		outputArc(point.va.vertex, point);
		point.va.setProcessed();
		point.vb.setProcessed();
		point.va.previous.setProcessed();
		outputArc(point.vb.vertex, point);
		assert point.va.previous == point.vb.next : point.va.previous.vertex + " " + point.vb.next.vertex;
		outputArc(point.va.previous.vertex, point);
	}

	private void outputPoints(List<Point2D> vertices) {
		for (Point2D point : Lists.reverse(vertices)) {
			System.out.println("new Point2D(" + point.x + ", " + point.y + "),");
		}
	}


	private void pairSplitNodes(Node node1, Node node2) {
		assert !splitNodePairs.containsKey(node1);
		assert !splitNodePairs.containsKey(node2);
		assert node1 != node2;
		assert node1.vertex.equals(node2.vertex);
		splitNodePairs.put(node1, node2);
		splitNodePairs.put(node2, node1);
	}

	private static boolean isAround(Point2D point, int x, int y) {
		return point.distanceTo(new Point2D(x, y)) < 4;
	}

	private boolean hasPairOf(Node node) {
		return splitNodePairs.containsKey(node);
	}

	/**
	 * @param node
	 * @param point
	 * @param isRightNode
	 * 	Is {@code node} the V_2 from [Obdrzalek 1998, Figure 6]
	 */
	private void integrateNewSplitNode(Node node, SkeletonEvent point, boolean isRightNode) {
		if (node.next.next == node) {
			// Eliminating lavs of 2 edges (those lavs can form after a split event).
			outputArc(node.vertex, node.next.vertex);
			node.setProcessed();
			node.next.setProcessed();
			if (hasPairOf(node.next)) {
				registry.getByOriginalEdge(node.currentEdge).moveTo(pairOf(node.next));
			}
			if (hasPairOf(node)) {
				registry.getByOriginalEdge(node.next.currentEdge).moveTo(pairOf(node));
			}
		} else {
			node.computeReflexAndBisector();
			if (isRightNode) {
				// Because that node contains the original current edge
				registry.getByOriginalEdge(point.va.currentEdge).moveTo(node);
			} else {
				registry.getByOriginalEdge(point.va.currentEdge).moveTo(node);
			}
			SkeletonEvent e = computeNearerBisectorsIntersection(node);
			if (e != null) {
				queue.add(e);
			}
			if (node.vertex.distanceTo(new Point2D(68, 90)) < 4 && !isRightNode) {
				watchEvent = e;
			}
		}
	}

	private Node pairOf(Node node) {
		assert splitNodePairs.containsKey(node);
		return splitNodePairs.get(node);
	}


	private void draw(Point2D point) {
		canvas.draw(point, DrawingPoint2D.withColorAndSize(Color.black, 4));
	}

	private void draw(Node node, Color color) {
		Iterator<Node> iter = node.iterator();
		Node previous = iter.next();
		while (iter.hasNext()) {
			Node next = iter.next();
			canvas.draw(
				new Segment2D(
					previous.vertex,
					next.vertex
				),
				DrawingSegment2D.withColor(color)
			);
			previous = next;
		}

		canvas.draw(
			new Segment2D(
				previous.vertex,
				node.vertex
			),
			DrawingSegment2D.withColor(color)
		);
	}

	private void draw(Segment2D segment) {
		canvas.draw(segment, DrawingSegment2D.withColorThin(Color.green));
	}

	private void outputArc(Point2D start, Point2D end) {
		assert start != null;
		assert end != null;
		arcs.put(start, end);
		canvas.draw(new Segment2D(start, end), DrawingSegment2D.withColorThin(Color.CYAN));
	}

	private SkeletonEvent computeNearerBisectorsIntersection(Node node) {
		// Non-convex 1c
		RayIntersection next = null;
		try {
			next = node.bisector.intersectionWith(node.next.bisector);
		} catch (GeometryException e) {
			canvas.draw(node.vertex, DrawingPoint2D.withColorAndSize(Color.red, 4));
			canvas.draw(node.next.vertex, DrawingPoint2D.withColorAndSize(Color.black, 4));
			canvas.draw(node.bisector.segment, DrawingSegment2D.withColorDirected(Color.blue));
			canvas.draw(node.next.bisector.segment, DrawingSegment2D.withColorDirected(Color.green));
			throw new RuntimeException(e);
		}
		RayIntersection previous = null;
		try {
			previous = node.bisector.intersectionWith(node.previous.bisector);
		} catch (GeometryException e) {
			canvas.draw(node.vertex, DrawingPoint2D.withColorAndSize(Color.red, 4));
			canvas.draw(node.previous.vertex, DrawingPoint2D.withColorAndSize(Color.black, 4));
			canvas.draw(node.bisector.segment, DrawingSegment2D.withColorDirected(Color.blue));
			canvas.draw(node.previousEdge, DrawingSegment2D.withColorDirected(Color.blue));
			canvas.draw(node.currentEdge, DrawingSegment2D.withColorDirected(Color.blue));
			canvas.draw(node.previous.bisector.segment, DrawingSegment2D.withColorDirected(Color.green));
			canvas.draw(node.previous.previousEdge, DrawingSegment2D.withColorDirected(Color.green));
			canvas.draw(node.previous.currentEdge, DrawingSegment2D.withColorDirected(Color.green));
			throw new RuntimeException(e);
		}
		Point2D nearer = null;
		Node originalEdgeStart = null;
		Node va = null;
		Node vb = null;
		if (next.r > 0 || previous.r > 0) {
			if (previous.r < 0 && next.r > 0 || next.r > 0 && next.r <= previous.r) {
				if (node.next.bisector.intersectionWith(node.bisector).r > 0 && next.r > 0) {
					nearer = next.getLinesIntersectionPoint();
					originalEdgeStart = node;
					va = node;
					vb = node.next;
				}
			} else if (next.r < 0 && previous.r > 0 || previous.r > 0 && previous.r <= next.r) {
				if (node.previous.bisector.intersectionWith(node.bisector).r > 0 && previous.r > 0) {
					nearer = previous.getLinesIntersectionPoint();
					originalEdgeStart = node.previous;
					va = node.previous;
					vb = node;
				}
			}
		}
//		else if (next.r > 0) {
//			nearer = next.getLinesIntersectionPoint();
//			originalEdgeStart = node;
//			va = node;
//			vb = node.next;
//		} else if (previous.r > 0) {
//			nearer = previous.getLinesIntersectionPoint();
//			originalEdgeStart = node.previous;
//			va = node.previous;
//			vb = node;
//		}
		if (node.isReflex) {
			SkeletonEvent splitPoint = findSplitEvent(node);
			if (
				nearer == null
					|| splitPoint != null
					&& node.vertex.distanceTo(splitPoint) < node.vertex.distanceTo(nearer)
				) {
				return splitPoint;
			}
		}
		assert nearer == null || va != null && vb != null;
		assert va == null && vb == null || va.next == vb;
		return nearer == null ? null :
			new SkeletonEvent(
				nearer.x,
				nearer.y,
				registry.getByOriginalEdge(originalEdgeStart.currentEdge),
				registry.getByOriginalEdge(originalEdgeStart.next.currentEdge),
				va,
				vb,
				EventType.EDGE,
				canvas
			);
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, figure 4]
	 * <p>
	 * Computes the point where a split event occurs.
	 *
	 * @param reflexNode
	 * 	A node from which a reflex event emanates.
	 * @return The point where split event occurs, or null if there is no split event emanated from {@code reflexNode}.
	 */
	private SkeletonEvent findSplitEvent(Node reflexNode) {
		assert reflexNode.isReflex;
		Point2D splitPoint = null;
		Node originalEdgeStart = null;
		for (Node node : reflexNode) {
			if (node == reflexNode || node == reflexNode.previous || node == reflexNode.next) {
				continue;
			}
			if (
				new RayIntersection(
					reflexNode.bisector.segment,
					node.currentEdge
				).r <= EPSILON
				) {
				continue;
			}
			if (
				new RayIntersection(
					reflexNode.currentEdge.reverse(),
					node.currentEdge
				).r <= 1
				) {
				continue;
			}
			if (
				new RayIntersection(
					reflexNode.previousEdge,
					node.currentEdge
				).r <= 1
				) {
				continue;
			}
			Point2D point = computeSplitPoint(reflexNode, node.currentEdge);
			if (isPointInAreaBetweenEdgeAndItsBisectors(point, node)) {
				if (
					splitPoint == null
						|| reflexNode.vertex.distanceTo(splitPoint) > reflexNode.vertex.distanceTo(point)
					) {
					splitPoint = point;
					originalEdgeStart = node;
				}
			}
		}
		return splitPoint == null ? null :
			new SkeletonEvent(
				splitPoint.x,
				splitPoint.y,
				registry.getByOriginalEdge(originalEdgeStart.currentEdge),
				registry.getByOriginalEdge(originalEdgeStart.next.currentEdge),
				reflexNode,
				null,
				EventType.SPLIT,
				canvas
			);
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, Figure 4]
	 * <p>
	 * Computes point B_i.
	 *
	 * @param currentNode
	 * 	A reflex node that creates a split event.
	 * @param oppositeEdge
	 * 	The tested line segment.
	 * @return Intersection between the bisector at {@code currentNode} and the axis of the angle between one of the
	 * edges starting at {@code currentNode} and the tested line segment {@code oppositeEdge}.
	 */
	private Point2D computeSplitPoint(Node currentNode, Segment2D oppositeEdge) {
		assert currentNode.isReflex;
		Point2D bisectorStart;
		if (currentNode.previousEdge.isParallel(oppositeEdge)) {
			bisectorStart = new RayIntersection(currentNode.currentEdge, oppositeEdge).getLinesIntersectionPoint();
		} else {
			bisectorStart = new RayIntersection(currentNode.previousEdge, oppositeEdge).getLinesIntersectionPoint();
		}
		Bisector bisector = new Bisector(
			new Segment2D(
				currentNode.vertex,
				bisectorStart
			),
			new Segment2D(
				bisectorStart,
				new RayIntersection(
					currentNode.bisector.segment,
					oppositeEdge
				).getLinesIntersectionPoint()
			),
			bisectorStart,
			false
		);
		return bisector.intersectionWith(currentNode.bisector).getLinesIntersectionPoint();
	}

	private Point2D computeAnotherIntersectionPoint(Node currentNode, Segment2D oppositeEdge) {
//		Point2D anotherBisectorStart = new RayIntersection(
//			currentNode.currentEdge,
//			oppositeEdge
//		).getLinesIntersectionPoint();
//		Point2D anotherOppositeEdgeVector = oppositeEdge.start.subtract(oppositeEdge.end).normalize();
//		Point2D anotherOneVector = currentNode.vertex.subtract(anotherBisectorStart).normalize();
//		Segment2D anotherBisector = new Segment2D(
//			anotherBisectorStart,
//			anotherBisectorStart.add(anotherOneVector).add(anotherOppositeEdgeVector)
//		);
//		return new RayIntersection(
//			currentNode.bisector.segment,
//			anotherBisector
//		).getLinesIntersectionPoint();
		Segment2D previousEdge = new Segment2D(currentNode.next.vertex, currentNode.vertex);
		Point2D bisectorStart = new RayIntersection(previousEdge, oppositeEdge).getLinesIntersectionPoint();
		Bisector bisector = new Bisector(
			new Segment2D(
				previousEdge.end,
				bisectorStart
			),
			new Segment2D(
				bisectorStart,
				new RayIntersection(
					currentNode.bisector.segment,
					oppositeEdge
				).getLinesIntersectionPoint()
			),
			bisectorStart,
			false
		);
		return bisector.intersectionWith(currentNode.bisector).getLinesIntersectionPoint();
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, Figure 4]
	 * <p>
	 * Checks if a point (namely point B coming from a reflex vertex) is located in an area bounded by an edge and
	 * bisectors coming from start and end nodes of this edge.
	 *
	 * @param point
	 * 	The point to test.
	 * @param currentNode
	 * 	A node at which starts the area-forming edge.
	 * @return true if the point is located within the area marked by an edge and edge's bisectors, false otherwise.
	 */
	private static boolean isPointInAreaBetweenEdgeAndItsBisectors(Point2D point, Node currentNode) {
		Bisector currentBisector = currentNode.bisector;
		Bisector nextBisector = currentNode.next.bisector;
		Point2D a = currentBisector.segment.end;
		Point2D b = currentNode.currentEdge.start;
		Point2D c = currentNode.currentEdge.end;
		Point2D d = nextBisector.segment.end;
		return isPointNonConvex(a, point, b) && isPointNonConvex(b, point, c) && isPointNonConvex(c, point, d);
	}

	/**
	 * Given 3 counter-clockwise points of a polygon, check if the middle one is convex or reflex.
	 *
	 * @param previous
	 * 	Beginning of vector 1.
	 * @param point
	 * 	End of vector 1 and beginning of vector 2.
	 * @param next
	 * 	End of vector 2.
	 * @return true if {@code point} is non-convex, false if it is convex.
	 */
	private static boolean isPointNonConvex(Point2D previous, Point2D point, Point2D next) {
		return perpDotProduct(
			new double[]{point.x - previous.x, point.y - previous.y},
			new double[]{next.x - point.x, next.y - point.y}
		) >= 0;
	}

	@Override
	public UndirectedGraph<Point2D, Segment2D> graph() {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);
		for (Map.Entry<Point2D, Collection<Point2D>> startToEnds : arcs.asMap().entrySet()) {
			Point2D start = startToEnds.getKey();
			graph.addVertex(start);
			for (Point2D end : startToEnds.getValue()) {
				graph.addVertex(end);
				graph.addEdge(start, end);
			}
		}
		return graph;
	}

	@Override
	public List<Segment2D> originalEdges() {
		return lav.edges;
	}

	@Override
	public UndirectedGraph<Point2D, Segment2D> cap(double depth) {
		return new PolygonShrinker(arcs, lav.edges, depth).asGraph();
	}
}
