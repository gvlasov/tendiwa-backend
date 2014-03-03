package org.tendiwa.settlements;

import org.jgrapht.EdgeFactory;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

import java.util.*;

/**
 * [Kelly section 4.3.1]
 * <p/>
 * A part of a city bounded by a fundamental basis cycle (one of those in <i>minimal cycle basis</i> from [Kelly section
 * 4.3.1, figure 41].
 */
public class CityCell {
private final RoadGraph secondaryRoadNetwork;
private final SimpleGraph<Point2D, Line2D> graph;

/**
 * @param vertices
 * 	Vertices forming a single cycle.
 */
CityCell(List<Point2D> vertices) {

	Collection<Line2D> edges = new ArrayList<>(vertices.size());
	for (int i = 0, l = vertices.size() - 1; i < l; i++) {
		edges.add(new Line2D(vertices.get(i), vertices.get(i + 1)));
	}
	graph = new SimpleGraph<>(new EdgeFactory<Point2D, Line2D>() {
		@Override
		public Line2D createEdge(Point2D sourceVertex, Point2D targetVertex) {
			return new Line2D(sourceVertex, targetVertex);
		}
	});
	for (Point2D vertex : vertices) {
		graph.addVertex(vertex);
	}
	for (Line2D edge : edges) {
		graph.addEdge(edge.start, edge.end, edge);
	}

	assert new ConnectivityInspector<>(graph).isGraphConnected();
	for (Point2D vertex : graph.vertexSet()) {
		assert graph.degreeOf(vertex) == 2;
	}
	secondaryRoadNetwork = buildSecondaryRoadNetwork();
}

private RoadGraph buildSecondaryRoadNetwork() {
	Collection<Point2D> vertices = new HashSet<>(graph.vertexSet());
	Collection<Line2D> edges = new HashSet<>(graph.edgeSet());
	for (Line2D road : longestRoads()) {
		calculateDeviatedMidPoint(road);
	}
}

private Point2D calculateDeviatedMidPoint(Line2D road) {
	return new Point2D(
		road.start.x + (road.end.x - road.start.x) / 2,
		road.start.y + (road.end.y - road.start.y) / 2
	);
}

/**
 * [Kelly figure 42]
 * <p/>
 * Returns several of the longest roads.
 *
 * @return Several of the longest roads.
 */
private Collection<Line2D> longestRoads() {
	List<Line2D> edges = new ArrayList<>(graph.edgeSet());
	Collections.sort(edges, new Comparator<Line2D>() {
		@Override
		public int compare(Line2D o1, Line2D o2) {
			return (int) Math.signum(o2.length() - o1.length());
		}
	});
	return edges.subList(0, 2);
}
}
