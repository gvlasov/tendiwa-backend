package org.tendiwa.geometry.extensions;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

public final class PlanarGraphs {
	private static final EdgeFactory<Point2D, Segment2D> EDGE_FACTORY = Segment2D::new;

	private PlanarGraphs() {
		throw new UnsupportedOperationException();
	}

	public static UndirectedGraph<Point2D, Segment2D> copyRelevantNetwork(UndirectedGraph<Point2D,
		Segment2D> relevantNetwork) {
		UndirectedGraph<Point2D, Segment2D> blockBoundsNetwork = new SimpleGraph<>(relevantNetwork.getEdgeFactory());
		for (Point2D vertex : relevantNetwork.vertexSet()) {
			blockBoundsNetwork.addVertex(vertex);
		}
		for (Segment2D edge : relevantNetwork.edgeSet()) {
			blockBoundsNetwork.addEdge(edge.start, edge.end, edge);
		}
		return blockBoundsNetwork;
	}

	public static EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return EDGE_FACTORY;
	}
}
