package org.tendiwa.settlements.cityBounds;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.*;

import java.util.Set;

final class LadderyEdgesOptimizer {

	/**
	 * Replaces pairs of orthogonal neighbor edges of length 1 with a single diagonal edge of length sqrt(2) if all
	 * the cells in a {@link org.tendiwa.geometry.Rectangle} hulling that pair of edges are in {@code shape}.
	 * <p>
	 * This optimization changes bounds of {@code ladderyCycleGraph} only in such a way,
	 * that its new inner cells are completely inside {@code shape}.
	 *
	 * @param ladderyCycleGraph
	 * 	A graph with all its vertices of degree 2 (i.e. a graph consisting only of cycles),
	 * 	and with no two consecutive edges lying on the same straight line.
	 * @param shape
	 * 	Cells that are considered inner: including both border of the graph and its inside.
	 * @param tolerance
	 * 	What is the greatest possible {@code Math.min(dx, dy)} between neighbors of a vertex for which the
	 * 	optimization will still be performed. The lesser is this value, the lesser will be portions of territory
	 * 	removed from the inside of the graph by shrinking its border while removing vertices. Consequently,
	 * 	the greater this value, the more optimisation will be performed, i.e., the more vertices will be removed.
	 * @return Mutated {@code ladderyCycleGraph}.
	 * @throws java.lang.IllegalArgumentException
	 * 	if {@code tolerance < 2}
	 */
	static UndirectedGraph<Point2D, Segment2D> optimize(
		UndirectedGraph<Point2D, Segment2D> ladderyCycleGraph,
		CellSet shape,
		int tolerance
	) {
		if (tolerance < 2) {
			throw new IllegalArgumentException("tolerance must be >= 2 (it is " + tolerance + " now");
		}
		Set<Point2D> vertices = ImmutableSet.copyOf(ladderyCycleGraph.vertexSet());
		int initialVerticesNumber = ladderyCycleGraph.vertexSet().size();
		int verticesRemoved = 0;
		for (Point2D vertex : vertices) {
			Neighbors neighbors = findNeighbors(vertex, ladderyCycleGraph);
			Rectangle hullingRectangle = createRectangleAroundPoints(neighbors.one, neighbors.another);
			if (hullingRectangle.width() > tolerance && hullingRectangle.height() > tolerance) {
				continue;
			}
			if (!areAllCellsOfRectangleInInnerArea(shape, hullingRectangle)) {
				continue;
			}
			removeVertex(vertex, neighbors.one, neighbors.another, ladderyCycleGraph);
			verticesRemoved++;
		}
//		System.out.println(verticesRemoved + "/" + initialVerticesNumber + " vertices removed");
		return ladderyCycleGraph;
	}

	/**
	 * @param vertex
	 * 	A vertex of {@code cycleGraph}.
	 * @param cycleGraph
	 * 	A graph with all its vertices of degree 2.
	 * @return Two neighbors of {@code vertex}.
	 */
	private static Neighbors findNeighbors(Point2D vertex, UndirectedGraph<Point2D, Segment2D> cycleGraph) {
		Point2D previousVertex = null, nextVertex = null;
		Set<Segment2D> edgesOfVertex = cycleGraph.edgesOf(vertex);
		assert edgesOfVertex.size() == 2;
		for (Segment2D edgeOfVertex : edgesOfVertex) {
			if (edgeOfVertex.start().equals(vertex)) {
				if (previousVertex == null) {
					previousVertex = edgeOfVertex.end();
				} else {
					assert nextVertex == null;
					nextVertex = edgeOfVertex.end();
				}
			} else {
				assert edgeOfVertex.end().equals(vertex);
				if (previousVertex == null) {
					previousVertex = edgeOfVertex.start();
				} else {
					assert nextVertex == null;
					nextVertex = edgeOfVertex.start();
				}
			}
		}
		assert previousVertex != null;
		assert nextVertex != null;
		return new Neighbors(previousVertex, nextVertex);
	}

	private static class Neighbors {
		private final Point2D one;
		private final Point2D another;

		private Neighbors(Point2D one, Point2D another) {
			this.one = one;
			this.another = another;
		}
	}

	/**
	 * Removes a vertex and connects two its neighbors with an edge.
	 *
	 * @param vertex
	 * 	A vertex to remove.
	 * @param previous
	 * 	One neighbor.
	 * @param next
	 * 	Another neighbor.
	 * @param graph
	 * 	The graph containing all the three vertices.
	 */
	private static void removeVertex(
		Point2D vertex,
		Point2D previous,
		Point2D next,
		UndirectedGraph<Point2D, Segment2D> graph
	) {
		graph.removeEdge(vertex, previous);
		graph.removeEdge(vertex, next);
		graph.removeVertex(vertex);
		graph.addEdge(previous, next);
	}

	private static Rectangle createRectangleAroundPoints(Point2D previousVertex, Point2D nextVertex) {
		return new RectangleByTwoCorners(
			previousVertex.toCell(),
			nextVertex.toCell()
		);
	}

	private static boolean areAllCellsOfRectangleInInnerArea(CellSet innerArea, Rectangle hullingRectangle) {
		int maxX = hullingRectangle.maxX();
		int maxY = hullingRectangle.maxY();
		for (int x = hullingRectangle.x(); x < maxX; x++) {
			for (int y = hullingRectangle.y(); y < maxY; y++) {
				if (!innerArea.contains(x, y)) {
					return false;
				}
			}
		}
		return true;
	}
}
