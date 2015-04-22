package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.VertexPositionAdapter;

public class Point2DVertexPositionAdapter {

	private static VertexPositionAdapter<Point2D> vertexPositionAdapter = new VertexPositionAdapter<Point2D>() {
		@Override
		public double getX(Point2D vertex) {
			return vertex.x();
		}

		@Override
		public double getY(Point2D vertex) {
			return vertex.y();
		}
	};

	private Point2DVertexPositionAdapter() {
		throw new UnsupportedOperationException("Shouldn't be instantiated");
	}

	public static VertexPositionAdapter<Point2D> get() {
		return vertexPositionAdapter;
	}
}
