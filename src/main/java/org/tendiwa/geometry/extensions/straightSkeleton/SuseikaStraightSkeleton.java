package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.StraightSkeleton;

import java.util.*;

// TODO: Split this class into more classes.
public class SuseikaStraightSkeleton implements StraightSkeleton {

	private final InitialListOfActiveVertices initialLav;
	private final PriorityQueue<SkeletonEvent> queue;
	private final Multimap<Point2D, Point2D> arcs = HashMultimap.create();
	final Debug debug = new Debug();

	public SuseikaStraightSkeleton(List<Point2D> vertices) {
		this(vertices, false);
	}

	private SuseikaStraightSkeleton(List<Point2D> vertices, boolean trustCounterClockwise) {
//		Utils.printListOfPoints(vertices);

		this.initialLav = new InitialListOfActiveVertices(vertices, trustCounterClockwise);
		this.queue = new PriorityQueue<>(initialLav.size());

		// [Obdrzalek 1998, paragraph 2.2, algorithm step 1c]
		initialLav.nodes.forEach(this::queueEventFromNode);
		assert !queue.isEmpty();

		while (!queue.isEmpty()) {
			// Convex 2a
			queue.poll().handle(this);
		}
		assert !arcs.isEmpty();
	}


	/**
	 * Makes a {@link Node} produce a {@link org.tendiwa.geometry.extensions.straightSkeleton.SkeletonEvent}
	 * and adds that event to the event queue if it is not null. It it is null, this method does nothing.
	 *
	 * @param node
	 * 	A node that produces an event.
	 */
	void queueEventFromNode(Node node) {
		SkeletonEvent e = node.computeNearerBisectorsIntersection();
		if (e != null) {
			queue.add(e);
		}
	}

	void connectLast3SegmentsOfLav(EdgeEvent event) {
		Node centerNode = new CenterNode(event.point);
		outputArc(event.leftParent().vertex, event.point);
		outputArc(event.rightParent().vertex, event.point);
		outputArc(event.leftParent().previous().vertex, event.point);
		debug.draw3NodeLavArcs(event);

		event.leftParent().growAdjacentFaces(centerNode);
		event.rightParent().growAdjacentFaces(centerNode);
		event.leftParent().previous().growAdjacentFaces(centerNode);

		event.leftParent().setProcessed();
		event.rightParent().setProcessed();
		event.leftParent().previous().setProcessed();

		assert event.leftParent().previous() == event.rightParent().next();
	}

	/**
	 * Such lavs can form after a split event
	 */
	void eliminate2NodeLav(Node node1, Node node2) {
		// TODO: Move this method to the Node class
		assert node1.next() == node2 && node2.next() == node1;
		outputArc(node1.vertex, node2.vertex);
		debug.draw2NodeLavArc(node1, node2);
		node1.growAdjacentFaces(node2);
		node1.setProcessed();
		node2.setProcessed();
		if (node1.hasPair()) {
			node1 = node1.getPair();
		}
		if (node2.hasPair()) {
			node2 = node2.getPair();
		}
	}

	void outputArc(Point2D start, Point2D end) {
		assert start != null;
		assert end != null;
		arcs.put(start, end);
		debug.testForNoIntersection(arcs, start, end);
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
	public ImmutableSet<Polygon> cap(double depth) {
		return new ShrinkedFront(faces(), depth).polygons();
	}

	@Override
	public Set<Polygon> faces() {
		Set<Polygon> answer = new LinkedHashSet<>();
		initialLav.nodes.stream()
			.map(node -> node.face().toPolygon())
			.forEach(answer::add);
		return answer;
	}

	void queueEvent(SplitEvent splitEvent) {
		assert splitEvent != null;
		queue.add(splitEvent);
	}
}
