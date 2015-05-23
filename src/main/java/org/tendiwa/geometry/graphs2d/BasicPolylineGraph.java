package org.tendiwa.geometry.graphs2d;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polyline;
import org.tendiwa.geometry.Polyline_Wr;
import org.tendiwa.geometry.Segment2D;

import java.util.Set;

public class BasicPolylineGraph extends Polyline_Wr implements PolylineGraph2D {
	private final Graph2D graph;

	public BasicPolylineGraph(Polyline polyline) {
		super(polyline);
		this.graph = Graph2D.createGraph(polyline, polyline.toSegments());
	}

	@Override
	public final int degreeOf(Point2D vertex) {
		return graph.degreeOf(vertex);
	}


	@Override
	public final Segment2D getEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.getEdge(sourceVertex, targetVertex);
	}

	@Override
	public final boolean containsEdge(Point2D sourceVertex, Point2D targetVertex) {
		return graph.containsEdge(sourceVertex, targetVertex);
	}

	@Override
	public final boolean containsEdge(Segment2D segment2D) {
		return graph.containsEdge(segment2D);
	}

	@Override
	public final boolean containsVertex(Point2D point2D) {
		return graph.containsVertex(point2D);
	}

	@Override
	public final Set<Segment2D> edgeSet() {
		return graph.edgeSet();
	}

	@Override
	public final Set<Segment2D> edgesOf(Point2D vertex) {
		return graph.edgesOf(vertex);
	}

	@Override
	public final Set<Point2D> vertexSet() {
		return graph.vertexSet();
	}

	@Override
	public final Point2D getEdgeSource(Segment2D segment2D) {
		return graph.getEdgeSource(segment2D);
	}

	@Override
	public final Point2D getEdgeTarget(Segment2D segment2D) {
		return graph.getEdgeTarget(segment2D);
	}
}
