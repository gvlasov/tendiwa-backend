package org.tendiwa.geometry.graphs2d;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polyline;

public interface PolylineGraph2D extends Polyline, Graph2D {
	@Override
	default int degreeOf(Point2D vertex) {
		return vertex.equals(start()) || vertex.equals(end()) ? 1 : 2;
	}
}
