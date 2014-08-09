package org.tendiwa.graphs;

import org.jgrapht.EdgeFactory;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

public final class PlanarGraphs {
	private static final EdgeFactory<Point2D, Segment2D> EDGE_FACTORY = Segment2D::new;

	private PlanarGraphs() {
		throw new UnsupportedOperationException("This class should not be instantiated");
	}

	public static EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return EDGE_FACTORY;
	}
}
