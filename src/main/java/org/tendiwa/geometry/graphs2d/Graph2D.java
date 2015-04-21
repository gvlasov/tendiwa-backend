package org.tendiwa.geometry.graphs2d;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

interface Graph2D extends UndirectedGraph<Point2D, Segment2D> {

	static Graph2D unite(UndirectedGraph<Point2D, Segment2D> graph, Graph2D graph2D) {
		return null;
	}
}
