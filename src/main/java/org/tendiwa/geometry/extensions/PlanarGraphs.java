package org.tendiwa.geometry.extensions;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

public final class PlanarGraphs {
	private PlanarGraphs() {

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
}
