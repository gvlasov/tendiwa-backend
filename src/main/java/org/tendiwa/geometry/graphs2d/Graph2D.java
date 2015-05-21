package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;
import org.tendiwa.settlements.utils.streetsDetector.ConnectivityComponent;

import java.util.Collection;
import java.util.Set;

import static org.tendiwa.collections.Collectors.toImmutableSet;

public interface Graph2D extends UndirectedGraph<Point2D, Segment2D> {

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

	default boolean hasOnlyEdge(Segment2D edge) {
		return containsEdge(edge) && edgeSet().size() == 1;
	}

	default MinimumCycleBasis minimumCycleBasis() {
		return new MinimumCycleBasis(
			this,
			Point2DVertexPositionAdapter.get()
		);
	}

	default boolean isPlanar() {
		return ShamosHoeyAlgorithm.areIntersected(edgeSet());
	}

	@Deprecated
	@Override
	default Segment2D addEdge(Point2D sourceVertex, Point2D targetVertex) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Segment2D segment2D) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean addVertex(Point2D point2D) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean removeAllEdges(Collection<? extends Segment2D> edges) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default Set<Segment2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean removeAllVertices(Collection<? extends Point2D> vertices) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean removeEdge(Segment2D segment2D) {
		throw new UnsupportedOperationException();
	}

	@Deprecated
	@Override
	default boolean removeVertex(Point2D point2D) {
		throw new UnsupportedOperationException();
	}
}
