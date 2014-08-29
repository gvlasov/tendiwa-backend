package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;

public class SameLineGraphEdgesPerturbations {
	private static Comparator<Segment2D> HORIZONTAL_COMPARATOR = (a, b) -> {
		assert a.start.y == a.end.y && b.start.y == b.end.y;
		double d = a.start.y - b.start.y;
		if (d < 0) {
			return -1;
		}
		if (d > 0) {
			return 1;
		}
		return 0;
	};
	private static Comparator<Segment2D> VERTICAL_COMPARATOR = (a, b) -> {
		assert a.start.x == a.end.x && b.start.x == b.end.x;
		double d = a.start.x - b.start.x;
		if (d < 0) {
			return -1;
		}
		if (d > 0) {
			return 1;
		}
		return 0;
	};

	/**
	 * Checks if some of graph's edges are segments of the same line, and perturbs vertices and edges of this graph
	 * so it contains no such segments.
	 * <p>
	 * This class is designed to work with graphs that represent simple polygons. You can use it with other classes
	 * of graphs, but that probably won't be useful.
	 * <p>
	 * NOTE: For graphs in which there is a very small distance between a vertex and an edge non-adjacent to that
	 * vertex, this algorithm may mutate graph in such a way that it will intersect itself.
	 *
	 * @param graph
	 * 	A planar graph to be mutated.
	 */
	public static void perturbIfHasSameLineEdges(UndirectedGraph<Point2D, Segment2D> graph, double magnitude) {
		ArrayList<Segment2D> verticalEdges = new ArrayList<>(graph.edgeSet().size());
		ArrayList<Segment2D> horizontalEdges = new ArrayList<>(graph.edgeSet().size());
		for (Segment2D edge : graph.edgeSet()) {
			if (edge.start.x == edge.end.x) {
				verticalEdges.add(edge);
			} else if (edge.start.y == edge.end.y) {
				horizontalEdges.add(edge);
			}
		}
		verticalEdges.sort(VERTICAL_COMPARATOR);
		horizontalEdges.sort(HORIZONTAL_COMPARATOR);
		/*
		 The algorithm is the following:
		 For each axis-parallel edge in a list of edges sorted by static coordinate,
		 perturb its start if the next edge in list has the same static coordinate (i.e., lies on the same line).
		 That way if we have N same line axis-parallel edges (placed consecutively in an array because it is sorted),
		 N-1 of those will be perturbed, except for the last one (because there is no next edge for the last one).
		 Perturbing the last one is not necessary because bu perturbing other ones the last one becomes non-parallel
		 to each of them.
		  */
		int size = verticalEdges.size() - 1;
		for (int i = 0; i < size; i++) {
			Point2D vertex = verticalEdges.get(i).start; // .end would be fine too
			if (vertex.x == verticalEdges.get(i + 1).start.x) {
				perturbVertexAndItsEdges(vertex, graph, magnitude);
			}
		}
		size = horizontalEdges.size() - 1;
		for (int i = 0; i < size; i++) {
			Point2D vertex = horizontalEdges.get(i).start; // .end would be fine too
			if (vertex.y == horizontalEdges.get(i + 1).start.y) {
				if (!graph.containsVertex(vertex)) {
					// Same edge could already be perturbed in a loop over vertical edges.
					continue;
				}
				perturbVertexAndItsEdges(vertex, graph, magnitude);
			}
		}
	}

	private static void perturbVertexAndItsEdges(
		Point2D vertex,
		UndirectedGraph<Point2D, Segment2D> graph,
		double magnitude
	) {
		Set<Segment2D> edges = ImmutableSet.copyOf(graph.edgesOf(vertex));
		assert edges.size() == 2;
		// We move by both axes so both vertical and
		// horizontal edges will become not on the same line
		// with those with which they were on the same line.
		Point2D newVertex = vertex.moveBy(magnitude, magnitude);
		graph.addVertex(newVertex);
		for (Segment2D edge : edges) {
			boolean removed = graph.removeEdge(edge);
			assert removed;
			// It should be .end, not .start, because in perturbIfHasSameLineEdges we used
			// vertex = edges.get(i).start
			if (edge.start == vertex) {
				graph.addEdge(newVertex, edge.end);
			} else {
				assert edge.end == vertex;
				graph.addEdge(newVertex, edge.start);
			}
		}
		assert graph.degreeOf(vertex) == 0 : graph.degreeOf(vertex);
		graph.removeVertex(vertex);
	}
}
