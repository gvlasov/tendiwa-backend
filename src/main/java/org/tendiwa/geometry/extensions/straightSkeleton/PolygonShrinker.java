package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.settlements.RayIntersection;

import java.awt.Color;
import java.util.*;

public class PolygonShrinker {

	protected Set<Segment2D> shrunkPolygonsSegments;

	protected PolygonShrinker() {

	}

	public PolygonShrinker(
		Multimap<Point2D, Point2D> arcs,
		List<Segment2D> edges,
		double depth
	) {
		UndirectedGraph<Point2D, Segment2D> graph = fillNewGraphWithArcsAndEdges(arcs, edges);
		Map<Segment2D, Iterable<Segment2D>> edgesToFaces = mapOriginalEdgesToFaces(graph, edges);
		shrunkPolygonsSegments = findShrunkPolygonsSegments(depth, edgesToFaces);
	}


	public UndirectedGraph<Point2D, Segment2D> asGraph() {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);
		for (Segment2D segment : shrunkPolygonsSegments) {
			graph.addVertex(segment.start);
			graph.addVertex(segment.end);
			graph.addEdge(segment.start, segment.end, segment);
		}
		return graph;
	}

	protected Set<Segment2D> findShrunkPolygonsSegments(
		double depth,
		Map<Segment2D, Iterable<Segment2D>> edgesToFaces
	) {
		ApproximatedEdges edges = new ApproximatedEdges();
		for (Map.Entry<Segment2D, Iterable<Segment2D>> e : edgesToFaces.entrySet()) {
			Queue<RayIntersection> queue = new PriorityQueue<>((a, b) -> a.r - b.r > 0 ? 1 : -1);
			Segment2D deepenedEdge = e.getKey().createParallelSegment(depth, true);
			for (Segment2D faceEdge : e.getValue()) {
				if (faceEdge == e.getKey()) {
					continue;
				}
				if (faceEdge.start.equals(faceEdge.end)) {
					continue;
				}
				RayIntersection intersection = new RayIntersection(faceEdge, deepenedEdge);
				if (intersection.r > 0 && intersection.r < 1) {
					queue.add(new RayIntersection(deepenedEdge, faceEdge));
				}
			}
			assert queue.size() % 2 == 0;
			while (!queue.isEmpty()) {
				edges.addFixedEdge(new Segment2D(
					// Get two consecutive intersections
					queue.poll().getLinesIntersectionPoint(),
					queue.poll().getLinesIntersectionPoint()
				));
			}
		}
		return edges.edges;
	}

	protected Map<Segment2D, Iterable<Segment2D>> mapOriginalEdgesToFaces(
		UndirectedGraph<Point2D, Segment2D> graph,
		List<Segment2D> edges
	) {
		Set<Segment2D> unusedEdges = new HashSet<>(edges);

		MinimumCycleBasis<Point2D, Segment2D> basis = new MinimumCycleBasis<>(graph, Point2DVertexPositionAdapter.get());
		Map<Segment2D, Iterable<Segment2D>> edgeToFace = new HashMap<>();

		for (MinimalCycle<Point2D, Segment2D> cycle : basis.minimalCyclesSet()) {
			Set<Point2D> cycleVertices = ImmutableSet.copyOf(cycle.vertexList());
			Segment2D originalFaceEdge = null;
			for (Segment2D edge : unusedEdges) {
				if (cycleVertices.contains(edge.start) && cycleVertices.contains(edge.end)) {
					originalFaceEdge = edge;
					break;
				}
			}
			assert originalFaceEdge != null;
			edgeToFace.put(originalFaceEdge, cycle);
		}
		return edgeToFace;
	}

	public UndirectedGraph<Point2D, Segment2D> fillNewGraphWithArcsAndEdges(
		Multimap<Point2D, Point2D> arcs,
		List<Segment2D> edges
	) {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);

		for (Map.Entry<Point2D, Point2D> e : arcs.entries()) {
			graph.addVertex(e.getKey());
			graph.addVertex(e.getValue());
			graph.addEdge(e.getKey(), e.getValue());
		}
		for (Segment2D edge : edges) {
			graph.addEdge(edge.start, edge.end, edge);
		}
		return graph;
	}

}
