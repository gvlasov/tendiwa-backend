package org.tendiwa.graphs.graphs2d;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.PlanarGraphs;

import java.util.Collection;
import java.util.Set;

public class Graph2D implements UndirectedGraph<Point2D, Segment2D> {
	private final UndirectedGraph<Point2D, Segment2D> graph;

	public void addSegmentAsEdge(Segment2D segment) {
		boolean added = addEdge(segment.start, segment.end, segment);
		if (!added) {
			throw new IllegalArgumentException("Segment " + segment + " is already contained in this graph");
		}
	}

	public void integrateCutSegment(CutSegment2D cutSegment) {
		Segment2D originalSegment = cutSegment.originalSegment();
		boolean removed = graph.removeEdge(
			cutSegment.originalSegment()
		);
		if (!removed) {
			throw new IllegalArgumentException(
				"Can't integrate shredded segment: there wasn't any original segment " +
					originalSegment + " in the graph"
			);
		}
		cutSegment.stream()
			.map(s -> s.end)
			.forEach(graph::addVertex);
		cutSegment.forEach(this::addSegmentAsEdge);
		assert !cutSegment.hasBeenCut() || !graph.containsEdge(originalSegment);
	}

	public boolean hasOnlyEdge(Segment2D edge) {
		return graph.containsEdge(edge) && graph.edgeSet().size() == 1;
	}

	public Point2D findNeighborOnSegment(Point2D hub, Segment2D segment) {
		if (!segment.isOneOfEnds(hub)) {
			throw new IllegalArgumentException("Hub point should be one of the segment's ends");
		}
		Point2D answer = null;
		for (Segment2D edge : graph.edgesOf(hub)) {
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

	@Override
	public int degreeOf(Point2D vertex) {
		return graph.degreeOf(vertex);
	}

	@Override
	public Set<Segment2D> getAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return graph.getAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return graph.getEdgeFactory();
	}

	@Override
	@Deprecated
	public Segment2D addEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.addEdge(sourceVertex, targetVertex);
	}

	@Override
	@Deprecated
	public boolean addEdge(Point2D sourceVertex, Point2D targetVertex, Segment2D segment2D) {
		return graph.addEdge(sourceVertex, targetVertex, segment2D);
	}

	@Override
	public boolean addVertex(Point2D point2D) {
		return graph.addVertex(point2D);
	}

	@Override
	public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Segment2D segment2D) {
		return graph.containsEdge(segment2D);
	}

	@Override
	public boolean containsVertex(Point2D point2D) {
		return graph.containsVertex(point2D);
	}

	@Override
	public Set<Segment2D> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public Set<Segment2D> edgesOf(Point2D vertex) {
		return graph.edgesOf(vertex);
	}

	@Override
	public boolean removeAllEdges(Collection<? extends Segment2D> edges) {
		return graph.removeAllEdges(edges);
	}

	@Override
	public Set<Segment2D> removeAllEdges(Point2D sourceVertex, Point2D targetVertex) {
		return graph.removeAllEdges(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeAllVertices(Collection<? extends Point2D> vertices) {
		return graph.removeAllVertices(vertices);
	}

	@Override
	public Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.removeEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean removeEdge(Segment2D segment2D) {
		return graph.removeEdge(segment2D);
	}

	@Override
	public boolean removeVertex(Point2D point2D) {
		return graph.removeVertex(point2D);
	}

	@Override
	public Set<Point2D> vertexSet() {
		return graph.vertexSet();
	}

	@Override
	public Point2D getEdgeSource(Segment2D segment2D) {
		return graph.getEdgeSource(segment2D);
	}

	@Override
	public Point2D getEdgeTarget(Segment2D segment2D) {
		return graph.getEdgeTarget(segment2D);
	}

	@Override
	public double getEdgeWeight(Segment2D segment2D) {
		return graph.getEdgeWeight(segment2D);
	}

	public Graph2D() {
		this.graph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
	}

}
