package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;
import org.tendiwa.settlements.utils.streetsDetector.ConnectivityComponent;

import static org.tendiwa.collections.Collectors.toImmutableSet;

public interface Graph2D extends UndirectedGraph<Point2D, Segment2D> {

	static Graph2D unite(UndirectedGraph<Point2D, Segment2D> graph, Graph2D graph2D) {
		return null;
	}

	static Graph2D createGraph(Iterable<Point2D> vertices, Iterable<Segment2D> segments) {
		MutableGraph2D graph = new MutableGraph2D();
		vertices.forEach(graph::addVertex);
		segments.forEach(graph::addSegmentAsEdge);
		return graph;
	}

	static Graph2DUnionCollector toGraph2DUnion() {
		return new Graph2DUnionCollector();
	}

	default Graph2D without(UndirectedGraph<Point2D, Segment2D> graph) {
		MutableGraph2D answer = new MutableGraph2D();
		edgeSet().stream()
			.filter(edge -> !graph.containsEdge(edge))
			.forEach((segment) -> {
				answer.addVertex(segment.start());
				answer.addVertex(segment.end());
				answer.addSegmentAsEdge(segment);
			});
		vertexSet()
			.stream()
			.filter(v -> degreeOf(v) == 0)
			.forEach(answer::addVertex);
		return answer;
	}

	default Graph2D intersection(Graph2D graph) {
		MutableGraph2D answer = new MutableGraph2D();
		vertexSet()
			.stream()
			.filter(graph::containsVertex)
			.forEach(answer::addVertex);
		edgeSet()
			.stream()
			.filter(graph::containsEdge)
			.forEach(answer::addSegmentAsEdge);
		return answer;
	}

	default ImmutableSet<ConnectivityComponent<Point2D, Segment2D>> connectivityComponents() {
		return new ConnectivityInspector<>(this).connectedSets()
			.stream()
			.map(set -> new ConnectivityComponent<>(this, set))
			.collect(toImmutableSet());
	}
	default Point2D findNeighborOnSegment(Point2D hub, Segment2D segment) {
		if (!segment.oneOfEndsIs(hub)) {
			throw new IllegalArgumentException("Hub point should be one of the segment's ends");
		}
		Point2D answer = null;
		for (Segment2D edge : edgesOf(hub)) {
			Point2D anotherEnd = edge.anotherEnd(hub);
			if (segment.contains(anotherEnd)) {
				if (answer != null) {
					throw new GeometryException("2 neighbors of hub point are on segment");
				}
				answer = anotherEnd;
			}
		}
		return answer;
	}
}
