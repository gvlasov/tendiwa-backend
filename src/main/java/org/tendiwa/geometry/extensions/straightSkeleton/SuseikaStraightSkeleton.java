package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.geometry.RayIntersection;

import java.awt.Color;
import java.util.*;
import java.util.stream.Collectors;

import static org.tendiwa.geometry.Vectors2D.perpDotProduct;

// TODO: Split this class into more classes.
public class SuseikaStraightSkeleton implements StraightSkeleton {

	private final InitialListOfActiveVertices initialLav;
	//	TestCanvas canvas = new TestCanvas(1, 200, 400);
	DrawableInto canvas = TestCanvas.canvas;
	private final PriorityQueue<SkeletonEvent> queue;
	public final Multimap<Point2D, Point2D> arcs = HashMultimap.create();
	static final double EPSILON = 1e-10;
	private final HashMap<Node, Node> splitNodePairs = new HashMap<>();
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
		SplitEvent.canvas = canvas;
		if (!trustCounterClockwise && !JTSUtils.isYDownCCW(vertices)) {
			vertices = Lists.reverse(vertices);
		}
//		outputPoints(vertices);
		this.initialLav = new InitialListOfActiveVertices(vertices, canvas);


		this.queue = new PriorityQueue<>(initialLav.size());
		// [Obdrzalek 1998, paragraph 2.2, algorithm step 1c]
		splitEventsRegistry = new RegistryOfSplitEventsOnEdges(initialLav.nodes);
		queueInitialEvents();
		assert !queue.isEmpty();

		while (!queue.isEmpty()) {
			// Convex 2a
			SkeletonEvent point = queue.poll();
			// TODO: move handling methods to the corresponding classes?
			if (point instanceof SplitEvent) {
				handleSplitEvent((SplitEvent) point);
			} else {
				handleEdgeEvent((EdgeEvent) point);
			}
			assert Boolean.TRUE;
		}
		assert !arcs.isEmpty();
//		if (canvas instanceof TestCanvas) {
//			((TestCanvas) canvas).close();
//		}
	}

	private void queueInitialEvents() {
		for (OriginalEdgeStart node : initialLav.nodes) {
			SkeletonEvent e = computeNearerBisectorsIntersection(node);
			if (e != null) {
				queue.add(e);
			}
		}
	}

	private void handleSplitEvent(SplitEvent point) {
		if (point.parent().isProcessed()) {
			return;
		}
		// Non-convex 2c
		if (point.parent().previous().previous().previous() == point.parent()) {
//			connectLast3SegmentsOfLav(point);
			assert false;
			return;
		}
		if (point.parent().next().next() == point.parent()) {
			eliminate2NodeLav(point.parent(), point.parent().next().next());
			return;
		}
		// Non-convex 2D
		outputArc(point.parent().vertex, point);
		canvas.draw(new Segment2D(point.parent().vertex, point), DrawingSegment2D.withColorThin(Color.red));
		if (point.getOppositeEdgeStartMovementHead().isProcessed()) {
//			canvas.draw(point.getOppositeEdgeStartMovementHead().bisector.segment, DrawingSegment2D.withColorThin(Color.orange));
			canvas.draw(point.getOppositeEdgeStartMovementHead().currentEdge, DrawingSegment2D.withColorThin(Color.magenta));
			canvas.draw(point.getOppositeEdgeStartMovementHead().vertex, DrawingPoint2D.withColorAndSize(Color.green, 2));
			assert false;
		}
		// Non-convex 2e

		// Split event produces two nodes at the same point, and those two nodes have distinct LAVs.
		LeftSplitNode leftNode = new LeftSplitNode(
			point,
			point.parent().previousEdgeStart,
//			point.getOppositeEdgeEndMovementHead().previous().currentEdgeStart
			point.oppositeEdgeStart()
		);
		RightSplitNode rightNode = new RightSplitNode(
			point,
			point.getOppositeEdgeEndMovementHead().previousEdgeStart,
			point.parent().currentEdgeStart
		);
		leftNode.setPair(rightNode);
		rightNode.setPair(leftNode);

		Node leftLavNextNode;
		boolean inTheSameLav = point.parent().isInTheSameLav(point.getOppositeEdgeEndMovementHead());
		if (inTheSameLav) {
			leftLavNextNode = point.getOppositeEdgeEndMovementHead();
		} else {
			leftLavNextNode = splitEventsRegistry.getNodeFromLeft(
				point.oppositeEdgeStart(),
				leftNode
			);
		}
		Node rightLavPreviousNode;
		if (point.parent().isInTheSameLav(point.getOppositeEdgeStartMovementHead())) {
			rightLavPreviousNode = point.getOppositeEdgeStartMovementHead();
		} else {
			rightLavPreviousNode = splitEventsRegistry.getNodeFromRight(
				point.oppositeEdgeStart(),
				rightNode
			);
		}

		leftNode.setPreviousInLav(point.parent().previous());
		leftLavNextNode.setPreviousInLav(leftNode);

		// TODO: Why do we need this?
//		try {
//			ImmutableList.copyOf(leftNode);
//		} catch (RuntimeException e) {
//			assert false;
//		}
//				draw(node1, Color.green);

		rightNode.setPreviousInLav(rightLavPreviousNode);
		point.parent().next().setPreviousInLav(rightNode);

		// TODO: Why do we need this?
//		ImmutableList.copyOf(rightNode);
//				draw(node2, Color.blue);

		point.parent().setProcessed();
		pairSplitNodes(leftNode, rightNode);

		splitEventsRegistry.addSplitNode(
			point.oppositeEdgeStart(),
			leftNode
		);
		splitEventsRegistry.addSplitNode(
			point.oppositeEdgeStart(),
			rightNode
		);

		point.parent().growRightFace(rightNode);
		point.parent().growLeftFace(leftNode);

		// Non-convex 2
		integrateNewSplitNode(leftNode, point, false);
		integrateNewSplitNode(rightNode, point, true);
		assert Boolean.TRUE;
	}

	private void handleEdgeEvent(EdgeEvent point) {
		// Convex 2b
		if (point.leftParent().isProcessed() || point.rightParent().isProcessed()) {
			if (!(point.leftParent().isProcessed() && point.rightParent().isProcessed())) {
				Node node = point.leftParent().isProcessed() ? point.rightParent() : point.leftParent();
				SkeletonEvent e = computeNearerBisectorsIntersection(node);
				if (e != null) {
					queue.add(e);
				}
			}
			return;
		}
		if ( point.leftParent().next() != point.rightParent()) {
			assert false;
		}
		// Convex 2c
		if (point.leftParent().previous().previous() == point.rightParent()) {
			connectLast3SegmentsOfLav(point);
			return;
		}
		if (point.leftParent().isInLavOf2Nodes()) {
			eliminate2NodeLav(point.leftParent(), point.rightParent());
			return;
		}
		// Convex 2d
		outputArc(point.leftParent().vertex, point);
		outputArc(point.rightParent().vertex, point);
		canvas.draw(new Segment2D(point.leftParent().vertex, point), DrawingSegment2D.withColorThin(Color.yellow));
		canvas.draw(new Segment2D(point.rightParent().vertex, point), DrawingSegment2D.withColorThin(Color.yellow));

		// Convex 2e
		Node node = new ShrinkedNode(
			point,
			point.leftParent().previousEdgeStart,
			point.rightParent().currentEdgeStart
		);

//		if (shouldHeadJump(point)) {
//			if (hasPair(point.leftParent())) {
//				point.leftParent().previousEdgeStart.face.growEndHalfface(node);
//				point.rightParent().previousEdgeStart.face.growEndHalfface(node);
//				point.rightParent().currentEdgeStart.face.growStartHalfface(node);
//
//				Node pair = pairOf(point.leftParent());
//				point.rightParent().previousEdgeStart.face.growEndHalfface(pair);
//			} else {
//				assert hasPair(point.rightParent());
//				point.rightParent().currentEdgeStart.face.growStartHalfface(node);
//				point.leftParent().previousEdgeStart.face.growEndHalfface(node);
//				point.leftParent().currentEdgeStart.face.growStartHalfface(node);
//
//				Node pair = pairOf(point.rightParent());
//				point.leftParent().currentEdgeStart.face.growStartHalfface(pair);
//
//			}
//		} else {
		point.leftParent().growAdjacentFaces(node);
		point.rightParent().growAdjacentFaces(node);
//		}

		node.setPreviousInLav(point.leftParent().previous());
		point.rightParent().next().setPreviousInLav(node);
		node.computeReflexAndBisector();

		point.leftParent().setProcessed();
		point.rightParent().setProcessed();

		// Convex 2f
		SkeletonEvent e = computeNearerBisectorsIntersection(node);
		if (e != null) {
			queue.add(e);
		}
		assert Boolean.TRUE;
	}

	private void connectLast3SegmentsOfLav(EdgeEvent point) {
		Node centerNode = new CenterNode(point);
		outputArc(point.leftParent().vertex, point);
		canvas.draw(new Segment2D(point.leftParent().vertex, point), DrawingSegment2D.withColorThin(Color.cyan));
		outputArc(point.rightParent().vertex, point);
		canvas.draw(new Segment2D(point.rightParent().vertex, point), DrawingSegment2D.withColorThin(Color.cyan));
		outputArc(point.leftParent().previous().vertex, point);
		canvas.draw(new Segment2D(point.leftParent().previous().vertex, point), DrawingSegment2D.withColorThin(Color.cyan));

		point.leftParent().growAdjacentFaces(centerNode);
		point.rightParent().growAdjacentFaces(centerNode);
		point.leftParent().previous().growAdjacentFaces(centerNode);

		point.leftParent().setProcessed();
		point.rightParent().setProcessed();
		point.leftParent().previous().setProcessed();

		assert point.leftParent().previous() == point.rightParent().next();
	}

//	private void growFaceEndWithPairOfSegments(Node start, Node center, Node end) {
//		if (start.currentEdgeStart.face.whereStartMoved() == start) {
//			if (hasPair(end)) {
//				end = pairOf(end);
//			}
//			// If by moving counter-clockwise we close an edge
//			Face face = start.currentEdgeStart.face;
//			face.growStartHalfface(center);
//			face.growStartHalfface(end);
//		} else {
//			if (hasPair(start)) {
//				start = pairOf(start);
//			}
//			Face face = end.previousEdgeStart.face;
//			face.growEndHalfface(center);
//			face.growEndHalfface(start);
//		}
//	}

	private void eliminate2NodeLav(Node node1, Node node2) {
		// TODO: This branch doesn't seem to occur
		assert node1.next() == node2 && node2.next() == node1;
		outputArc(node1.vertex, node2.vertex);
		canvas.draw(new Segment2D(node1.vertex, node2.vertex), DrawingSegment2D.withColorThin(Color.magenta));
		node1.setProcessed();
		node2.setProcessed();
		if (node1.hasPair()) {
			node1 = node1.getPair();
		}
		if (node2.hasPair()) {
			node2 = node2.getPair();
		}
		node1.growAdjacentFaces(node2);
	}

	private void pairSplitNodes(Node node1, Node node2) {
		assert !splitNodePairs.containsKey(node1);
		assert !splitNodePairs.containsKey(node2);
		assert node1 != node2;
		assert node1.vertex.equals(node2.vertex);
		splitNodePairs.put(node1, node2);
		splitNodePairs.put(node2, node1);
	}

	/**
	 * @param node
	 * @param point
	 * @param isRightNode
	 * 	Is {@code node} the V_2 from [Obdrzalek 1998, Figure 6]
	 */
	private void integrateNewSplitNode(Node node, SkeletonEvent point, boolean isRightNode) {
		if (node.isInLavOf2Nodes()) {
			// Such lavs can form after a split event
			eliminate2NodeLav(node, node.next());
		} else {
			node.computeReflexAndBisector();
			SkeletonEvent e = computeNearerBisectorsIntersection(node);
			if (e != null) {
				queue.add(e);
			}
		}
	}


	private void outputArc(Point2D start, Point2D end) {
		assert start != null;
		assert end != null;
		arcs.put(start, end);
		if (
			ShamosHoeyAlgorithm.areIntersected(
				arcs.entries().stream().map(e -> new Segment2D(e.getKey(), e.getValue())).collect(Collectors.toList()))
			) {
			TestCanvas.canvas.draw(new Segment2D(start, end), DrawingSegment2D.withColorThin(Color.white));
			assert false;
		}
	}

	private SkeletonEvent computeNearerBisectorsIntersection(Node node) {
		// Non-convex 1c
		RayIntersection next = node.bisector.intersectionWith(node.next().bisector);
		EdgeEvent sameLineIntersection = trySameLineIntersection(next, node, node.next());
		if (sameLineIntersection != null) {
			return sameLineIntersection;
		}

		RayIntersection previous = node.bisector.intersectionWith(node.previous().bisector);
		sameLineIntersection = trySameLineIntersection(previous, node, node.previous());
		if (sameLineIntersection != null) {
			return sameLineIntersection;
		}

		Point2D nearer = null;
		Node originalEdgeStart = null;
		Node va = null;
		Node vb = null;
		if (next.r > 0 || previous.r > 0) {
			if (previous.r < 0 && next.r > 0 || next.r > 0 && next.r <= previous.r) {
				if (node.next().bisector.intersectionWith(node.bisector).r > 0 && next.r > 0) {
					nearer = next.getLinesIntersectionPoint();
					originalEdgeStart = node;
					va = node;
					vb = node.next();
				}
			} else if (next.r < 0 && previous.r > 0 || previous.r > 0 && previous.r <= next.r) {
				if (node.previous().bisector.intersectionWith(node.bisector).r > 0 && previous.r > 0) {
					nearer = previous.getLinesIntersectionPoint();
					originalEdgeStart = node.previous();
					va = node.previous();
					vb = node;
				}
			}
		}
//		else if (next.r > 0) {
//			nearer = next.getLinesIntersectionPoint();
//			originalEdgeStart = node;
//			leftParent = node;
//			rightParent = node.next;
//		} else if (previous.r > 0) {
//			nearer = previous.getLinesIntersectionPoint();
//			originalEdgeStart = node.previous;
//			leftParent = node.previous;
//			rightParent = node;
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
		assert va == null && vb == null || va.next() == vb;
		if (nearer == null) {
			return null;
		}
		return new EdgeEvent(nearer.x, nearer.y, va, vb);
	}

	private EdgeEvent trySameLineIntersection(RayIntersection intersection, Node current, Node target) {
		if (Double.isInfinite(intersection.r)) {
			return new EdgeEvent(
				(target.vertex.x + current.vertex.x) / 2,
				(target.next().vertex.y + current.vertex.y) / 2,
				current,
				target
			);
		}
		return null;
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
			if (node == reflexNode || node == reflexNode.previous() || node == reflexNode.next()) {
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
					reflexNode.previousEdge(),
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
		if (splitPoint == null) {
			return null;
		}
		if (originalEdgeStart == null) {
			assert false;
		}
		// TODO: If this is always true, we can get rid of casting
		assert originalEdgeStart instanceof OriginalEdgeStart;
		SplitEvent skeletonEvent = new SplitEvent(
			splitPoint.x,
			splitPoint.y,
			reflexNode,
			// TODO: Get rid of casting
			(OriginalEdgeStart) originalEdgeStart
		);
		return skeletonEvent;
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
		if (currentNode.previousEdge().isParallel(oppositeEdge)) {
			bisectorStart = new RayIntersection(currentNode.currentEdge, oppositeEdge).getLinesIntersectionPoint();
		} else {
			bisectorStart = new RayIntersection(currentNode.previousEdge(), oppositeEdge).getLinesIntersectionPoint();
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
		Segment2D previousEdge = new Segment2D(currentNode.next().vertex, currentNode.vertex);
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
		Bisector nextBisector = currentNode.next().bisector;
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
		return initialLav.edges;
	}

	@Override
	public UndirectedGraph<Point2D, Segment2D> cap(double depth) {
		return new PolygonShrinker(arcs, initialLav.edges, depth).asGraph();
	}
}
