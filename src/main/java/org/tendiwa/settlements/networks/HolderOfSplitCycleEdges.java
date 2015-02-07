package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableMap;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.core.meta.Range;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.PlanarGraphs;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds chains of edges that are result of splitting edges of {@link RoadsPlanarGraphModel#originalRoadGraph}.
 * <p>
 * We can't mutate {@link org.tendiwa.settlements.networks.RoadsPlanarGraphModel#originalRoadGraph}
 * itself because that breaks {@link org.tendiwa.graphs.MinimalCycle#iterator()}.
 * <p>
 * This class exists so {@link RoadsPlanarGraphModel#getFullRoadGraph()} can return a graph with edges on cycle edges
 * that are not part of the initial {@link RoadsPlanarGraphModel#originalRoadGraph}.
 * <p>
 * One of the reasons why we need this class is that when we split a road of
 * {@link NetworkWithinCycle}, that road may be shared with another
 * {@link NetworkWithinCycle} of the same {@link RoadsPlanarGraphModel}.
 * <p>
 * <b>Original edge</b> is an edge that hasn't been split from another edge.
 */
final class HolderOfSplitCycleEdges {
	/**
	 * After edges has been split with {@link #splitEdge(org.tendiwa.geometry.Segment2D,
	 * org.tendiwa.geometry.Point2D)},
	 * this map may end up containing non-existing edges as keys, because we can split and edge and then split it
	 * again. Those edges should not be removed, because they are needed by
	 * {@link #isEdgeSplit(org.tendiwa.geometry.Segment2D)}.
	 */
	private final Map<Segment2D, UndirectedGraph<Point2D, Segment2D>> subedgesToGraphs = new HashMap<>();
	/**
	 * A map from point to the original edges split by those points.
	 */
	private final Map<Point2D, Segment2D> splitPointsToOriginalEdges = new HashMap<>();

	/**
	 * Remembers that {@code edgeToSplit} is split into a graph of sub-edges.
	 *
	 * @param edgeToSplit
	 * 	An edge of {@link RoadsPlanarGraphModel#originalRoadGraph} (those are called "original edges" within this
	 * 	class, or a sub-edge.
	 * @param point
	 * 	A point to split the edge with.
	 */
	void splitEdge(Segment2D edgeToSplit, Point2D point) {
		UndirectedGraph<Point2D, Segment2D> graph = subedgesToGraphs.get(edgeToSplit);
		boolean isEdgeOriginal = graph == null;
		if (isEdgeOriginal) {
			initGraphForOriginalEdge(edgeToSplit, point);
			splitPointsToOriginalEdges.put(point, edgeToSplit);
			return;
		}
		if (graph.containsVertex(point)) {
//			TODO: Maybe here we should only check this. If this assert works, then we sure should.
//			assert point.equals(edgeToSplit.start) || point.equals(edgeToSplit.end);
			return;
		}
		Segment2D parentEdge = findSubEdgeThatContainsPoint(graph, point);
		graph.removeEdge(parentEdge);
		graph.addVertex(point);
		Segment2D originalEdge = findOriginalEdge(edgeToSplit);
		addOneOfSplitEdges(parentEdge.start, point, graph);
		addOneOfSplitEdges(point, parentEdge.end, graph);
		splitPointsToOriginalEdges.put(point, originalEdge);
		assert !graph.containsEdge(edgeToSplit);
	}

	/**
	 * If {@code edge} is split edge, returns its original edge, otherwise returns {@code edge}.
	 *
	 * @param edge
	 * @return
	 */
	Segment2D findOriginalEdge(Segment2D edge) {
		Segment2D answer = splitPointsToOriginalEdges.get(edge.start);
		if (answer == null) {
			answer = splitPointsToOriginalEdges.get(edge.end);
		}
		return answer == null ? edge : answer;
	}

	/**
	 * @return A new {@link com.google.common.collect.ImmutableMap} that maps original edges to their split parts
	 */
	ImmutableMap<Point2D, Segment2D> getMapFromSplitToOriginalSegments() {
		return ImmutableMap.copyOf(splitPointsToOriginalEdges);
	}

	private void addOneOfSplitEdges(
		Point2D subedgeStart,
		Point2D subedgeEnd,
		UndirectedGraph<Point2D, Segment2D> graph
	) {
		Segment2D addedEdge = graph.addEdge(subedgeStart, subedgeEnd);
		subedgesToGraphs.put(addedEdge, graph);
	}

	private void initGraphForOriginalEdge(Segment2D edgeToSplit, Point2D point) {
		UndirectedGraph<Point2D, Segment2D> graph;
		graph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
		subedgesToGraphs.put(edgeToSplit, graph);
		// TODO: Why do we need this here?
		subedgesToGraphs.put(edgeToSplit.reverse(), graph);
		graph.addVertex(edgeToSplit.start);
		graph.addVertex(edgeToSplit.end);
		graph.addVertex(point);
		assert graph.edgeSet().isEmpty();
		addEdgeWithSubgraph(edgeToSplit.start, point, graph);
		addEdgeWithSubgraph(point, edgeToSplit.end, graph);
	}

	private void addEdgeWithSubgraph(Point2D start, Point2D end, UndirectedGraph<Point2D, Segment2D> graph) {
		Segment2D addedEdge = graph.addEdge(start, end);
		subedgesToGraphs.put(addedEdge, graph);
//		subedgesToGraphs.put(addedEdge.reverse(), graph);
	}


	private Segment2D findSubEdgeThatContainsPoint(UndirectedGraph<Point2D, Segment2D> graph, Point2D point) {
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
		assert subedgesToGraphs.containsKey(edge);
		return subedgesToGraphs.get(edge);
	}

	boolean isEdgeSplit(Segment2D edge) {
		// Asserting that if an edge is said to be split, then it should be contained in the graph stored under that
		// edge.
		return subedgesToGraphs.containsKey(edge);
	}

	/**
	 * Finds the actual edge that is result of splitting {@code originalEdge}, or {@code originalEdge} itself, that
	 * holds {@code point}.
	 *
	 * @param originalEdge
	 * 	Original edge containing an actual edge.
	 * @param point
	 * 	A point on the actual edge.
	 */
	public Segment2D findActualEdge(Segment2D originalEdge, Point2D point) {
		assert Recs2D.boundingBox(originalEdge).contains(point);
		if (isEdgeSplit(originalEdge)) {
			for (Segment2D splitEdge : getGraph(originalEdge).edgeSet()) {
				assert !splitEdge.start.equals(point);
				assert !splitEdge.end.equals(point);
				if (Recs2D.boundingBox(splitEdge).contains(point)) {
					return splitEdge;
				}
			}
			throw new RuntimeException("Could not find actual edge");
		} else {
			return originalEdge;
		}
	}
}
