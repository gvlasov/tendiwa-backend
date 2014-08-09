package org.tendiwa.geometry.extensions.twakStraightSkeleton;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.straightSkeleton.PolygonShrinker;
import org.tendiwa.graphs.PlanarGraphs;

import java.util.List;
import java.util.Map;

public class TwakPolygonShrinker extends PolygonShrinker {

	public TwakPolygonShrinker(
		UndirectedGraph<Point2D, Segment2D> graph,
		List<Segment2D> edges,
		double depth
	) {
		this.edges = edges;
		UndirectedGraph<Point2D, Segment2D> fullGraph = fillNewGraphWithArcsAndEdges(
			graph,
			edges
		);
		Map<Segment2D, Iterable<Segment2D>> edgesToFaces = mapOriginalEdgesToFaces(fullGraph, edges);
		shrunkPolygonsSegments = findShrunkPolygonsSegments(
			depth,
			edgesToFaces
		);
	}

	public UndirectedGraph<Point2D, Segment2D> fillNewGraphWithArcsAndEdges(
		UndirectedGraph<Point2D, Segment2D> arcsGraph,
		List<Segment2D> edges
	) {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());

		for (Segment2D arc : arcsGraph.edgeSet()) {
			graph.addVertex(arc.start);
			graph.addVertex(arc.end);
			graph.addEdge(arc.start, arc.end);
		}
		for (Segment2D edge : edges) {
			graph.addVertex(edge.start);
			graph.addVertex(edge.end);
			graph.addEdge(edge.start, edge.end, new Segment2D(edge.start, edge.end));
		}
		return graph;
	}

}
