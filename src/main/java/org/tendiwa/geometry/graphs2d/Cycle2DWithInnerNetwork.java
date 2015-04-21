package org.tendiwa.geometry.graphs2d;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

interface Cycle2DWithInnerNetwork extends Cycle2D {
	UndirectedGraph<Point2D, Segment2D>  innerNetwork();
	default UndirectedGraph<Point2D, Segment2D>  fullGraph() {
		return new UnitedGraph2D(graph(), innerNetwork());
	}
}
