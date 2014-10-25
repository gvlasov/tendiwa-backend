package org.tendiwa.settlements;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.meta.Range;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vectors2D;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds chains of edges that are result of splitting edges of {@link RoadsPlanarGraphModel#lowLevelRoadGraph}.
 * We can't mutate {@link org.tendiwa.settlements.NetworkWithinCycle#lowLevelRoadGraph}
 * itself because that breaks {@link org.tendiwa.graphs.MinimalCycle#iterator()}.
 * <p>
 * This class exists so {@link RoadsPlanarGraphModel#getFullRoadGraph()} can return a graph with edges on cycle edges
 * that are
 * not part of the initial {@link RoadsPlanarGraphModel#lowLevelRoadGraph}.
 */
final class HolderOfSplitCycleEdges {
	private final Map<Segment2D, UndirectedGraph<Point2D, Segment2D>> edgesToSubEdgeGraphs = new HashMap<>();

	/**
	 * Remembers that {@code edgeToSplit} is split into a graph of sub-edges.
	 *
	 * @param edgeToSplit
	 * 	An edge of {@link RoadsPlanarGraphModel#lowLevelRoadGraph}, or a sub-edge.
	 * @param point
	 * 	A point to split the edge with.
	 */
	void splitEdge(Segment2D edgeToSplit, Point2D point) {
		UndirectedGraph<Point2D, Segment2D> graph = edgesToSubEdgeGraphs.get(edgeToSplit);
		if (graph == null) {
			graph = new SimpleGraph<>(org.tendiwa.geometry.extensions.PlanarGraphs.getEdgeFactory());
			edgesToSubEdgeGraphs.put(edgeToSplit, graph);
			edgesToSubEdgeGraphs.put(edgeToSplit.reverse(), graph);
			assert graph.edgeSet().isEmpty();
			graph.addVertex(edgeToSplit.start);
			graph.addVertex(edgeToSplit.end);
			graph.addVertex(point);
			Segment2D addedEdge = graph.addEdge(edgeToSplit.start, point);
			edgesToSubEdgeGraphs.put(addedEdge, graph);
//			edgesToSubEdgeGraphs.put(addedEdge.reverse(), graph);
			addedEdge = graph.addEdge(point, edgeToSplit.end);
			edgesToSubEdgeGraphs.put(addedEdge, graph);
//			edgesToSubEdgeGraphs.put(addedEdge.reverse(), graph);
			return;
		}
		if (graph.containsVertex(point)) {
			return;
		}
		Segment2D subEdge = findSubEdgeThatContains(graph, point);
		graph.removeEdge(subEdge);
		graph.addVertex(point);
		Segment2D addedEdge = graph.addEdge(subEdge.start, point);
		edgesToSubEdgeGraphs.put(addedEdge, graph);
//		edgesToSubEdgeGraphs.put(addedEdge.reverse(), graph);
		addedEdge = graph.addEdge(point, subEdge.end);
		edgesToSubEdgeGraphs.put(addedEdge, graph);
//		edgesToSubEdgeGraphs.put(addedEdge.reverse(), graph);
	}


	private Segment2D findSubEdgeThatContains(UndirectedGraph<Point2D, Segment2D> graph, Point2D point) {
		for (Segment2D subEdge : graph.edgeSet()) {
			double dx = Math.abs(subEdge.dx());
			double dy = Math.abs(subEdge.dy());
			if (dx > dy) {
				assert dx > Vectors2D.EPSILON;
				double min = Math.min(subEdge.start.x, subEdge.end.x);
				double max = Math.max(subEdge.start.x, subEdge.end.x);
				if (Range.contains(min, max, point.x)) {
					return subEdge;
				}
			} else {
				assert dy > Vectors2D.EPSILON;
				double min = Math.min(subEdge.start.y, subEdge.end.y);
				double max = Math.max(subEdge.start.y, subEdge.end.y);
				if (Range.contains(min, max, point.y)) {
					return subEdge;
				}
			}
		}
		throw new GeometryException("Sub-edge containing point " + point + " not found");
	}

	UndirectedGraph<Point2D, Segment2D> getGraph(Segment2D edge) {
		assert edgesToSubEdgeGraphs.containsKey(edge);
		return edgesToSubEdgeGraphs.get(edge);
	}

	boolean isEdgeSplit(Segment2D edge) {
		return edgesToSubEdgeGraphs.containsKey(edge);
	}
}
