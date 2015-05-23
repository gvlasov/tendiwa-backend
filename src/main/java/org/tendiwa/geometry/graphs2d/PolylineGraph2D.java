package org.tendiwa.geometry.graphs2d;

import com.sun.istack.internal.NotNull;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polyline;

import java.util.Iterator;

public interface PolylineGraph2D extends Polyline, Graph2D {

	@Override
	default int degreeOf(Point2D vertex) {
		return vertex.equals(start()) || vertex.equals(end()) ? 1 : 2;
	}

	@NotNull
	@Override
	Iterator<Point2D> iterator();
}
