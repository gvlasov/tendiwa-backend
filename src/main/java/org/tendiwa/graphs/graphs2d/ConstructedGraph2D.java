package org.tendiwa.graphs.graphs2d;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.graphs2d.Graph2D;

import java.util.Set;

public abstract class ConstructedGraph2D implements Graph2D {
	private final UndirectedGraph<Point2D, Segment2D> graph;

	protected ConstructedGraph2D() {
		this.graph = new SimpleGraph<>(BasicSegment2D::new);
	}

	protected ConstructedGraph2D(Graph2D graph) {
		this.graph = graph.toJgrapht();
	}

	protected final Segment2D addEdge(
		Point2D sourceVertex,
		Point2D targetVertex
	) {
		return graph.addEdge(sourceVertex, targetVertex);
	}

	protected final void addSegmentAsEdge(Segment2D segment) {
		addEdge(segment.start(), segment.end(), segment);
	}

	protected final void addGraph(Graph2D graph) {
		graph.vertexSet().forEach(this::addVertex);
		graph.edgeSet().forEach(this::addSegmentAsEdge);
	}

	protected final void addPolygon(Polygon polygon) {
		polygon.forEach(this::addVertex);
		polygon.toSegments().forEach(this::addSegmentAsEdge);
	}

	protected final void addPolyline(Polyline polyline) {
		polyline.forEach(this::addVertex);
		polyline.toSegments().forEach(this::addSegmentAsEdge);
	}

	protected final boolean addEdge(
		Point2D sourceVertex,
		Point2D targetVertex,
		Segment2D edge
	) {
		return graph.addEdge(sourceVertex, targetVertex, edge);
	}

	protected final boolean addVertex(Point2D vertex) {
		return graph.addVertex(vertex);
	}

	protected final Segment2D removeEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.removeEdge(sourceVertex, targetVertex);
	}

	protected final boolean removeEdge(Segment2D edge) {
		return graph.removeEdge(edge);
	}

	protected boolean removeVertex(Point2D vertex) {
		return graph.removeVertex(vertex);
	}

	@Override
	public Set<Point2D> vertexSet() {
		return graph.vertexSet();
	}

	@Override
	public Set<Segment2D> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public boolean containsVertex(Point2D vertex) {
		return graph.containsVertex(vertex);
	}

	@Override
	public boolean containsEdge(Segment2D edge) {
		return graph.containsEdge(edge);
	}

	@Override
	public int degreeOf(Point2D vertex) {
		return graph.degreeOf(vertex);
	}

	@Override
	public Set<Segment2D> edgesOf(Point2D vertex) {
		return graph.edgesOf(vertex);
	}

	@Override
	public Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public Point2D getEdgeSource(Segment2D edge) {
		return graph.getEdgeSource(edge);
	}

	@Override
	public Point2D getEdgeTarget(Segment2D edge) {
		return graph.getEdgeTarget(edge);
	}

	protected void removeEdgeAndOrphanedVertices(Segment2D edge) {
		assert containsEdge(edge);
		removeEdge(edge);
		if (degreeOf(edge.start()) == 0) {
			removeVertex(edge.start());
		}
		if (degreeOf(edge.end()) == 0) {
			removeVertex(edge.end());
		}
	}
}
