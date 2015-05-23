package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import com.sun.istack.internal.NotNull;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;
import org.tendiwa.settlements.utils.streetsDetector.ConnectivityComponent;

import java.util.Iterator;
import java.util.Set;

import static org.tendiwa.collections.Collectors.toImmutableSet;

public interface Graph2D extends BoundedShape {

	static Graph2D createGraph(Iterable<Point2D> vertices, Iterable<Segment2D> segments) {
		MutableGraph2D graph = new BasicMutableGraph2D();
		vertices.forEach(graph::addVertex);
		segments.forEach(graph::addSegmentAsEdge);
		return graph;
	}

	static Graph2DUnionCollector toGraph2DUnion() {
		return new Graph2DUnionCollector();
	}

	default Graph2D without(UndirectedGraph<Point2D, Segment2D> graph) {
		MutableGraph2D answer = new BasicMutableGraph2D();
		edgeSet().stream()
			.filter(edge -> !graph.containsEdge(edge))
			.forEach(segment -> {
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

	Set<Point2D> vertexSet();

	Set<Segment2D> edgeSet();

	boolean containsVertex(Point2D vertex);

	boolean containsEdge(Segment2D edge);

	int degreeOf(Point2D vertex);

	Set<Segment2D> edgesOf(Point2D vertex);

	Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex);

	boolean containsEdge(Point2D sourceVertex, Point2D targetVertex);

	Point2D getEdgeSource(Segment2D e);

	Point2D getEdgeTarget(Segment2D e);

	default UndirectedGraph<Point2D, Segment2D> toJgrapht() {
		MutableGraph2D graph = new BasicMutableGraph2D();
		vertexSet().forEach(graph::addVertex);
		edgeSet().forEach(graph::addSegmentAsEdge);
		return graph;
	}


	default Graph2D intersection(Graph2D graph) {
		MutableGraph2D answer = new BasicMutableGraph2D();
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
		UndirectedGraph<Point2D, Segment2D> jgraphtGraph = toJgrapht();
		return new ConnectivityInspector<>(jgraphtGraph).connectedSets()
			.stream()
			.map(set -> new ConnectivityComponent<>(jgraphtGraph, set))
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

	default boolean hasOnlyEdge(Segment2D edge) {
		return containsEdge(edge) && edgeSet().size() == 1;
	}

	default MinimumCycleBasis minimumCycleBasis() {
		return new MinimumCycleBasis(this);
	}

	default boolean isPlanar() {
		return ShamosHoeyAlgorithm.areIntersected(edgeSet());
	}

	@Override
	@NotNull
	default Iterator<Point2D> iterator() {
		return vertexSet().iterator();
	}
}
