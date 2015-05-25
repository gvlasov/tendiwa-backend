package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.collections.Collectors;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.PlanarGraphs;

import java.util.*;

import static org.tendiwa.collections.Collectors.toLinkedHashSet;

public final class SuseikaStraightSkeleton implements StraightSkeleton {
	private final InitialListOfActiveVertices initialLav;
	private final PriorityQueue<SkeletonEvent> queue;
	private final Multimap<Point2D, Point2D> arcs = HashMultimap.create();
	final Debug debug = new Debug();
	private static int skeletonNumber = 0;
	private final Polygon polygon;
	private int hash = skeletonNumber++;

	public SuseikaStraightSkeleton(Polygon polygon) {
		this(polygon, false);
	}

	@Override
	public int hashCode() {
		return hash;
	}

	private SuseikaStraightSkeleton(Polygon polygon, boolean trustCounterClockwise) {
		this.polygon = polygon;
//		Utils.printListOfPoints(vertices);

		this.initialLav = new InitialListOfActiveVertices(polygon, trustCounterClockwise);
		this.queue = new PriorityQueue<>(initialLav.size());

		// [Obdrzalek 1998, paragraph 2.2, algorithm step 1c]
		initialLav.nodes.forEach(this::queueEventFromNode);
		assert !queue.isEmpty();

		while (!queue.isEmpty()) {
			// Convex 2a
			SkeletonEvent event = queue.poll();
			event.handle(this);
//			debug.drawEventHeight(event);
			assert Boolean.TRUE;
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

	void outputArc(Point2D start, Point2D end) {
		assert start != null;
		assert end != null;
		arcs.put(start, end);
		debug.testForNoIntersection(arcs, start, end);
	}

	@Override
	public UndirectedGraph<Point2D, Segment2D> graph() {
		UndirectedGraph<Point2D, Segment2D> graph = PlanarGraphs.createGraph();
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
		if (depth <= -Vectors2D.EPSILON) {
			throw new IllegalArgumentException("Cap depth can't be negative");
		}
		if (depth <= Vectors2D.EPSILON) {
			return ImmutableSet.of(polygon);
		} else {
			return new ShrinkedFront(faces(), depth).polygons();
		}
	}

	@Override
	public Set<StraightSkeletonFace> faces() {
		return initialLav.nodes.stream()
			.map(node -> node.face().toPolygon())
			.collect(toLinkedHashSet());
	}

	void queueEvent(SplitEvent splitEvent) {
		assert splitEvent != null;
		queue.add(splitEvent);
	}
}
