package org.tendiwa.geometry.graphs2d;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;

public interface Cycle2D extends Polygon, Graph2D {

	@Override
	default int degreeOf(Point2D vertex) {
		if (containsVertex(vertex)) {
			return 2;
		} else {
			throw new IllegalArgumentException("Vertex " + vertex + " is not present in this graph");
		}
	}
}
