package org.tendiwa.graphs.graphs2d;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.BasicSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;

public class BasicMutableGraph2D extends SimpleGraph<Point2D, Segment2D> implements MutableGraph2D {

	public BasicMutableGraph2D() {
		super(BasicSegment2D::new);
	}

	/**
	 * Copies vertices and edges of another graph into this graph.
	 */
	public BasicMutableGraph2D(Graph2D graph) {
		this();
		graph.vertexSet().forEach(this::addVertex);
		graph.edgeSet().forEach(this::addSegmentAsEdge);
	}

	public BasicMutableGraph2D(UndirectedGraph<Point2D, Segment2D> graph) {
		this();
		graph.vertexSet().forEach(this::addVertex);
		graph.edgeSet().forEach(this::addSegmentAsEdge);
	}
}
