package org.tendiwa.geometry.graphs2d;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

public interface Mesh2D {
	UndirectedGraph<Point2D, Segment2D> graph();
}
