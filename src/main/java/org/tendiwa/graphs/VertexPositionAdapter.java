package org.tendiwa.graphs;

import org.tendiwa.geometry.BasicPoint2D;
import org.tendiwa.geometry.Point2D;

public interface VertexPositionAdapter<V> {
	public double getX(V vertex);

	public double getY(V vertex);

	public default Point2D positionOf(V vertex) {
		return new BasicPoint2D(getX(vertex), getY(vertex));
	}
}
