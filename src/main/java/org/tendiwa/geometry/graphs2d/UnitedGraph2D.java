package org.tendiwa.geometry.graphs2d;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.AbstractBaseGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

final class UnitedGraph2D extends AbstractBaseGraph<Point2D, Segment2D> implements Graph2D {

	public UnitedGraph2D(
		UndirectedGraph<Point2D, Segment2D> oneGraph,
		UndirectedGraph<Point2D, Segment2D> anotherGraph
	) {
		super(oneGraph.getEdgeFactory(), false, false);
		oneGraph.vertexSet().forEach(this::addVertex);
		anotherGraph.vertexSet().forEach(this::addVertex);
		oneGraph.edgeSet().forEach(e -> this.addEdge(e.start(), e.end(), e));
		anotherGraph.edgeSet().forEach(e -> this.addEdge(e.start(), e.end(), e));
	}
}
