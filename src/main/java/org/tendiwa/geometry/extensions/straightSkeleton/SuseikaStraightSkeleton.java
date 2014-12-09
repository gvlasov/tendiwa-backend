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
	private final NodeFlowRegistry nodeFlowRegistry;
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
		NodeFlow.canvas = canvas;
		SkeletonEvent.canvas = canvas;
		if (!trustCounterClockwise && !JTSUtils.isYDownCCW(vertices)) {
			vertices = Lists.reverse(vertices);
		}
//		outputPoints(vertices);
		this.initialLav = new InitialListOfActiveVertices(vertices, canvas);


		this.queue = new PriorityQueue<>(initialLav.size());
		// [Obdrzalek 1998, paragraph 2.2, algorithm step 1c]
		nodeFlowRegistry = new NodeFlowRegistry(initialLav.nodes);
		splitEventsRegistry = new RegistryOfSplitEventsOnEdges(initialLav.nodes, nodeFlowRegistry);
		queueInitialEvents(initialLav.size());
		assert !queue.isEmpty();

		while (!queue.isEmpty()) {
			// Convex 2a
			SkeletonEvent point = queue.poll();
			if (point.isSplitEvent()) {
				handleSplitEvent(point);
			} else {
				handleEdgeEvent(point);
			}
		}
		assert !arcs.isEmpty();
//		if (canvas instanceof TestCanvas) {
//			((TestCanvas) canvas).close();
//		}
	}

	private void queueInitialEvents(int size) {
		int i = 0;
		// TODO: We don't need a counter here, change to foreach
		for (
			Node node = initialLav.nodes.get(0);
			i < size;
			i++, node = node.next()
			) {
//			canvas.draw(node.bisector.segment, DrawingSegment2D.withColor(Color.green));
			SkeletonEvent e = computeNearerBisectorsIntersection(node);
			if (e != null) {
				queue.add(e);
			}
		}
	}

	private void handleSplitEvent(SkeletonEvent point) {
		assert point.isSplitEvent();
		if (point.leftParent().isProcessed()) {
			return;
		}
		// Non-convex 2c
		if (point.leftParent().previous().previous().previous() == point.leftParent()) {
			connectLast3SegmentsOfLav(point);
			return;
		}
		if (point.leftParent().next().next() == point.leftParent()) {
			eliminate2NodeLav(point.leftParent(), point.rightParent());
			return;
		}
		// Non-convex 2D
		outputArc(point.leftParent().vertex, point);
		if (point.getOppositeEdgeStartMovementHead().isProcessed()) {
//			canvas.draw(point.getOppositeEdgeStartMovementHead().bisector.segment, DrawingSegment2D.withColorThin(Color.orange));
			canvas.draw(point.getOppositeEdgeStartMovementHead().currentEdge, DrawingSegment2D.withColorThin(Color.magenta));
			canvas.draw(point.getOppositeEdgeStartMovementHead().vertex, DrawingPoint2D.withColorAndSize(Color.green, 2));
			assert false;
		}
		// Non-convex 2e

		// Split event produces two nodes at the same point, and those two nodes have distinct LAVs.
		Node leftNode = new Node(
			point,
			point.leftParent().previousEdgeStart,
			point.getOppositeEdgeEndMovementHead().previous().currentEdgeStart
		);
		Node rightNode = new Node(
			point,
			point.getOppositeEdgeEndMovementHead().previousEdgeStart,
			point.leftParent().currentEdgeStart
		);

		Node leftLavNextNode;
		boolean inTheSameLav = point.leftParent().isInTheSameLav(point.getOppositeEdgeEndMovementHead());
		if (inTheSameLav) {
			leftLavNextNode = point.getOppositeEdgeEndMovementHead();
		} else {
			leftLavNextNode = splitEventsRegistry.getNodeFromLeft(
				point.oppositeEdgeStartMovement.getTail(),
				leftNode
			);
		}
		Node rightLavPreviousNode;
		if (point.leftParent().isInTheSameLav(point.getOppositeEdgeStartMovementHead())) {
			rightLavPreviousNode = point.getOppositeEdgeStartMovementHead();
		} else {
			rightLavPreviousNode = splitEventsRegistry.getNodeFromRight(point.getOppositeEdgeStartMovementHead(), rightNode);
		}

		leftNode.setPreviousInLav(point.leftParent().previous());
		leftLavNextNode.setPreviousInLav(leftNode);

		// TODO: Why do we need this?
//		try {
//			ImmutableList.copyOf(leftNode);
//		} catch (RuntimeException e) {
//			assert false;
//		}
//				draw(node1, Color.green);

		rightNode.setPreviousInLav(rightLavPreviousNode);
		point.leftParent().next().setPreviousInLav(rightNode);

		// TODO: Why do we need this?
//		ImmutableList.copyOf(rightNode);
//				draw(node2, Color.blue);

		point.leftParent().setProcessed();
		pairSplitNodes(leftNode, rightNode);

		splitEventsRegistry.addSplitNode(
			point.oppositeEdgeStartMovement.getTail(),
			leftNode,
			RegistryOfSplitEventsOnEdges.Orientation.LEFT
		);
		splitEventsRegistry.addSplitNode(
			point.oppositeEdgeStartMovement.getTail(),
			rightNode,
			RegistryOfSplitEventsOnEdges.Orientation.RIGHT
		);

		nodeFlowRegistry.split(point.leftParent(), leftNode, rightNode);
		// Non-convex 2
		integrateNewSplitNode(leftNode, point, false);
		integrateNewSplitNode(rightNode, point, true);
	}

	private void handleEdgeEvent(SkeletonEvent point) {
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
		assert point.leftParent().next() == point.rightParent() : point.leftParent().next().vertex + " " + point
			.rightParent()
			.vertex;
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

		// Convex 2e
		Node node = new Node(
			point,
			point.leftParent().previousEdgeStart,
			point.rightParent().currentEdgeStart
		);
		node.setPreviousInLav(point.leftParent().previous());
		point.rightParent().next().setPreviousInLav(node);
		node.computeReflexAndBisector();

		point.leftParent().setProcessed();
		point.rightParent().setProcessed();
		boolean hasLeftPair = hasPairOf(point.leftParent());
		boolean hasRightPair = hasPairOf(point.rightParent());
		if (areFlowTailsNeighborsInInitialLav(point)) {
			nodeFlowRegistry.move(point.leftParent(), node);
			nodeFlowRegistry.move(point.rightParent(), node);
		} else {
			if (!hasLeftPair) {
				// Move beginnings only to edge event nodes.
				nodeFlowRegistry.move(point.rightParent(), node);
			}
			if (!hasRightPair) {
				nodeFlowRegistry.move(point.leftParent(), node);
			}
			if (hasRightPair) {
				Node parentPair = pairOf(point.rightParent());
//				if (!parentPair.isProcessed()) {
//					nodeFlowRegistry.split(point.leftParent(), node, parentPair);
//				}
				nodeFlowRegistry.move(point.leftParent(), parentPair);
			}
			if (hasLeftPair) {
				Node parentPair = pairOf(point.leftParent());
//				if (!parentPair.isProcessed()) {
//					nodeFlowRegistry.split(node, parentPair, parentPair);
//				}
				nodeFlowRegistry.move(point.rightParent(), parentPair);
			}
		}

		// Convex 2f
		SkeletonEvent e = computeNearerBisectorsIntersection(node);
		if (e != null) {
			queue.add(e);
		}
	}

	private boolean areFlowTailsNeighborsInInitialLav(SkeletonEvent point) {
		return point.leftParent().currentEdgeStart == point.rightParent().currentEdgeStart
			|| point.rightParent().currentEdgeStart == point.leftParent().previousEdgeStart;
	}

	private void connectLast3SegmentsOfLav(SkeletonEvent point) {
		outputArc(point.leftParent().vertex, point);
		point.leftParent().setProcessed();
		point.rightParent().setProcessed();
		point.leftParent().previous().setProcessed();
		outputArc(point.rightParent().vertex, point);
		assert point.leftParent().previous() == point.rightParent().next()
			: point.leftParent().previous().vertex + " " + point.rightParent().next().vertex;
		outputArc(point.leftParent().previous().vertex, point);
		movePairlessPointsToPairPoint(point);
	}

	private void movePairlessPointsToPairPoint(SkeletonEvent point) {
		tryCombination(point, point.leftParent(), point.rightParent(), point.leftParent().previous());
		tryCombination(point, point.rightParent(), point.leftParent().previous(), point.leftParent());
		tryCombination(point, point.leftParent().previous(), point.leftParent(), point.rightParent());
	}

	private void tryCombination(SkeletonEvent point, Node target, Node neighbor1, Node neighbor2) {
		Node counterClockwiseNode = findCounterClockwiseMostNode(
			point,
			target,
			neighbor1,
			neighbor2
		);
		if (hasPairOf(counterClockwiseNode) && !pairOf(counterClockwiseNode).isProcessed()) {
			nodeFlowRegistry.move(target, pairOf(counterClockwiseNode));
//			movementRegistry.getByOriginalEdge(target.currentEdge).changeHead(pairOf(counterClockwiseNode));
		}
	}

	private Node findCounterClockwiseMostNode(Point2D source, Node target, Node neighbor1, Node neighbor2) {
		double originalAngle = target.vertex.angleTo(source);
		double angle1 = computeAndRotateByOriginalAngle(source, neighbor1.vertex, originalAngle);
		double angle2 = computeAndRotateByOriginalAngle(source, neighbor2.vertex, originalAngle);
		/*
		Now we are rotated in such a way that vector [target;source] has angle 0 radian, and the vector
		counter-clockwise from [source;target] (swapping intended) is the one that has the least angle greater than 0.
		 */
		assert angle1 > 0;
		assert angle2 > 0;
		assert angle1 != angle2;
		return angle1 > angle2 ? neighbor2 : neighbor1;
	}

	private double computeAndRotateByOriginalAngle(Point2D source, Point2D neighbor1, double originalAngle) {
		double angle = (source.angleTo(neighbor1) - originalAngle) % Math.PI * 2;
		while (angle < 0) {
			angle += Math.PI * 2;
		}
		return angle;
	}

	private void eliminate2NodeLav(Node node1, Node node2) {
		assert node1.next() == node2 && node2.next() == node1;
		// Eliminating LAVs consisting only of 2 nodes
		outputArc(node1.vertex, node2.vertex);
		node1.setProcessed();
		node2.setProcessed();
	}

	private void pairSplitNodes(Node node1, Node node2) {
		assert !splitNodePairs.containsKey(node1);
		assert !splitNodePairs.containsKey(node2);
		assert node1 != node2;
		assert node1.vertex.equals(node2.vertex);
		splitNodePairs.put(node1, node2);
		splitNodePairs.put(node2, node1);
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
		if (node.isInLavOf2Nodes()) {
			// Such lavs can form after a split event
			eliminate2NodeLav(node, node.next());
			jumpIfNeighborHasPair(node.next(), node);
			jumpIfNeighborHasPair(node, node.next());
		} else {
			node.computeReflexAndBisector();
			// TODO: Functionality moved to NodeFlow.split, this commented out code must be removed
//			if (isRightNode) {
//				// Because that node contains the original current edge
////				movementRegistry.getByOriginalEdge(point.leftParent.currentEdge).changeHead(node);
//				nodeFlowRegistry.move(point.leftParent, node);
//			} else {
////				movementRegistry.getByOriginalEdge(point.leftParent.currentEdge).changeHead(node);
//				nodeFlowRegistry.move(point.leftParent, node);
//			}
			SkeletonEvent e = computeNearerBisectorsIntersection(node);
			if (e != null) {
				queue.add(e);
			}
		}
	}

	private void jumpIfNeighborHasPair(Node nodeToMove, Node neighbor) {
		if (hasPairOf(neighbor)) {
			nodeFlowRegistry.move(nodeToMove, pairOf(neighbor));
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
		if (ShamosHoeyAlgorithm.areIntersected(arcs.entries().stream().map(e -> new Segment2D(e.getKey(), e.getValue
			())).collect(Collectors.toList()))) {
			TestCanvas.canvas.draw(new Segment2D(start, end), DrawingSegment2D.withColorThin(Color.white));
//			assert false;
		}
		canvas.draw(new Segment2D(start, end), DrawingSegment2D.withColorThin(Color.yellow));
	}

	private SkeletonEvent computeNearerBisectorsIntersection(Node node) {
		// Non-convex 1c
		RayIntersection next = null;
		try {
			next = node.bisector.intersectionWith(node.next().bisector);
		} catch (GeometryException e) {
			canvas.draw(node.vertex, DrawingPoint2D.withColorAndSize(Color.red, 4));
			canvas.draw(node.next().vertex, DrawingPoint2D.withColorAndSize(Color.black, 4));
			canvas.draw(node.bisector.segment, DrawingSegment2D.withColorDirected(Color.blue, 1));
			canvas.draw(node.next().bisector.segment, DrawingSegment2D.withColorDirected(Color.green, 1));
			throw new RuntimeException(e);
		}
		RayIntersection previous = null;
		try {
			previous = node.bisector.intersectionWith(node.previous().bisector);
		} catch (GeometryException e) {
			canvas.draw(node.vertex, DrawingPoint2D.withColorAndSize(Color.red, 4));
			canvas.draw(node.previous().vertex, DrawingPoint2D.withColorAndSize(Color.black, 4));
			canvas.draw(node.bisector.segment, DrawingSegment2D.withColorDirected(Color.blue, 1));
			canvas.draw(node.previousEdge(), DrawingSegment2D.withColorDirected(Color.blue, 1));
			canvas.draw(node.currentEdge, DrawingSegment2D.withColorDirected(Color.blue, 1));
			canvas.draw(node.previous().bisector.segment, DrawingSegment2D.withColorDirected(Color.green, 1));
			canvas.draw(node.previous().previousEdge(), DrawingSegment2D.withColorDirected(Color.green, 1));
			canvas.draw(node.previous().currentEdge, DrawingSegment2D.withColorDirected(Color.green, 1));
			throw new RuntimeException(e);
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
//		try {
//			assert initialLav.nodes.contains(originalEdgeStart) : originalEdgeStart.vertex;
//			assert initialLav.nodes.contains(originalEdgeStart.next) : originalEdgeStart.next.vertex;
//		} catch (AssertionError e) {
//
//			TestCanvas.canvas.draw(originalEdgeStart.vertex, DrawingPoint2D.withColorAndSize(Color.yellow, 3));
//			TestCanvas.canvas.draw(originalEdgeStart.next.vertex, DrawingPoint2D.withColorAndSize(Color.magenta, 3));
//			TestCanvas.canvas.draw(node.bisector.segment, DrawingSegment2D.withColorDirected(Color.yellow));
//			throw new RuntimeException();
//		}

		SkeletonEvent skeletonEvent = new SkeletonEvent(
			nearer.x,
			nearer.y,
			nodeFlowRegistry.getChainByHead(originalEdgeStart),
			nodeFlowRegistry.getChainByHead(originalEdgeStart.next()),
			va,
			vb
		);
//		if (!initialLav.nodes.contains(node)) {
//			canvas.draw(new Segment2D(node.vertex, nearer), DrawingSegment2D.withColorThin(Color.yellow));
//		}
		nodeFlowRegistry.getChainByHead(originalEdgeStart).addStartObserver(skeletonEvent);
		nodeFlowRegistry.getChainByHead(originalEdgeStart.next()).addEndObserver(skeletonEvent);
		return skeletonEvent;
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
		SkeletonEvent skeletonEvent = new SkeletonEvent(
			splitPoint.x,
			splitPoint.y,
			nodeFlowRegistry.getChainByHead(originalEdgeStart),
			nodeFlowRegistry.getChainByHead(originalEdgeStart.next()),
			reflexNode,
			null
		);
		nodeFlowRegistry.getChainByHead(originalEdgeStart).addStartObserver(skeletonEvent);
		nodeFlowRegistry.getChainByHead(originalEdgeStart.next()).addEndObserver(skeletonEvent);
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
