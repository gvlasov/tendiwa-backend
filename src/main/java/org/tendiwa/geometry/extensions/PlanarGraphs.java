package org.tendiwa.geometry.extensions;

import org.jgrapht.EdgeFactory;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimumCycleBasis;

public final class PlanarGraphs {
	private static final EdgeFactory<Point2D, Segment2D> EDGE_FACTORY = Segment2D::new;

	private PlanarGraphs() {
		throw new UnsupportedOperationException();
	}

	public static UndirectedGraph<Point2D, Segment2D> copyGraph(
		UndirectedGraph<Point2D, Segment2D> graph
	) {
		UndirectedGraph<Point2D, Segment2D> blockBoundsNetwork = new SimpleGraph<>(graph.getEdgeFactory());
		graph.vertexSet().forEach(blockBoundsNetwork::addVertex);
		for (Segment2D edge : graph.edgeSet()) {
			blockBoundsNetwork.addEdge(edge.start, edge.end, edge);
		}
		return blockBoundsNetwork;
	}

	public static MinimumCycleBasis<Point2D, Segment2D> minimumCycleBasis(UndirectedGraph<Point2D, Segment2D> graph) {
		return new MinimumCycleBasis<>(graph, Point2DVertexPositionAdapter.get());
	}

	public static EdgeFactory<Point2D, Segment2D> getEdgeFactory() {
		return EDGE_FACTORY;
	}

	public static UndirectedGraph<Point2D, Segment2D> createGraph() {
		return new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
	}

	/**
	 * Removes an edge from graph, and also removes any vertices that are orphaned (left with degree == 0) as a result
	 * of removing the edge.
	 *
	 * @param graph
	 * 	A graph to modify.
	 * @param edge
	 * 	An edge to remove.
	 */
	public static void removeEdgeAndOrphanedVertices(UndirectedGraph<Point2D, Segment2D> graph, Segment2D edge) {
		assert graph.containsEdge(edge);
		graph.removeEdge(edge);
		if (graph.degreeOf(edge.start) == 0) {
			graph.removeVertex(edge.start);
		}
		if (graph.degreeOf(edge.end) == 0) {
			graph.removeVertex(edge.end);
		}
	}

	public UndirectedGraph<Point2D, Segment2D> graphOfSegments(Iterable<Segment2D> segments) {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
		for (Segment2D segment : segments) {
			graph.addVertex(segment.start);
			graph.addVertex(segment.end);
			graph.addEdge(segment.start, segment.end, segment);
		}
		return graph;
	}
}
