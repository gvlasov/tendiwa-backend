package org.tendiwa.geometry;

import org.tendiwa.geometry.graphs2d.Cycle2D;
import org.tendiwa.geometry.graphs2d.Graph2D;

import java.util.Set;

public final class PolygonGraph extends Polygon_Wr implements Cycle2D {

	private final Graph2D graph;

	public PolygonGraph(Polygon polygon) {
		super(polygon);
		this.graph = Graph2D.createGraph(polygon, polygon.toSegments());
	}

	@Override
	public int degreeOf(Point2D vertex) {
		return graph.degreeOf(vertex);
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
}
