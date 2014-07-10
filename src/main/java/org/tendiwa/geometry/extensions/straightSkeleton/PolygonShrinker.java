package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.Colors;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingMinimalCycle;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
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

	public List<Segment2D> edges;
	protected List<Segment2D> shrunkPolygonsSegments;
	public static TestCanvas canvas;
	static Iterator<Color> colors = Iterators.cycle(Color.green, Color.blue, Color.orange, Color.yellow);

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

	protected List<Segment2D> findShrunkPolygonsSegments(
		double depth,
		Map<Segment2D, Iterable<Segment2D>> edgesToFaces
	) {
		ApproximatedEdges edges = new ApproximatedEdges();
		for (Map.Entry<Segment2D, Iterable<Segment2D>> e : edgesToFaces.entrySet()) {
			Queue<RayIntersection> queue = new PriorityQueue<>((a, b) -> (int) Math.signum(a.r - b.r));
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
			assert queue.size() % 2 == 0 : queue.size();
			while (!queue.isEmpty()) {
				Segment2D edge = new Segment2D(
					// Get two consecutive intersections
					queue.poll().getLinesIntersectionPoint(),
					queue.poll().getLinesIntersectionPoint()
				);
				edges.addFixedEdge(edge);
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
		for (MinimalCycle<Point2D, Segment2D> cycle : basis.minimalCyclesSet()) {
			Color nextColor = colors.next();
//			canvas.draw(cycle, DrawingMinimalCycle.withColor(nextColor, Point2DVertexPositionAdapter.get()));
		}

//		assert edges.size() == basis.minimalCyclesSet().size() : edges.size() + " " + basis.minimalCyclesSet().size();
		Map<Segment2D, Iterable<Segment2D>> edgeToFace = new HashMap<>();

		for (MinimalCycle<Point2D, Segment2D> cycle : basis.minimalCyclesSet()) {
			Segment2D originalFaceEdge = findUnusedEdgeForFace(
				unusedEdges,
				ImmutableSet.copyOf(cycle.vertexList())
			);
			assert !edgeToFace.containsKey(originalFaceEdge) : originalFaceEdge;
			edgeToFace.put(originalFaceEdge, cycle);
		}
//		assert edges.size() == edgeToFace.size() : edges.size() + " " + edgeToFace.size();
		return edgeToFace;
	}

	private Segment2D findUnusedEdgeForFace(Set<Segment2D> unusedEdges, Set<Point2D> face) {
		Segment2D originalFaceEdge = null;
		for (Segment2D edge : unusedEdges) {
			if (face.contains(edge.start) && face.contains(edge.end)) {
				originalFaceEdge = edge;
				unusedEdges.remove(originalFaceEdge);
				break;
			}
		}
//		canvas.draw(originalFaceEdge, DrawingSegment2D.withColor(Color.red));
//		Color next = colors.next();
//		for (Point2D v : face) {
//			canvas.draw(v, DrawingPoint2D.withColorAndSize(next, 4));
//		}
		if (originalFaceEdge == null) {
			assert originalFaceEdge != null;
		}

		return originalFaceEdge;
	}

	public UndirectedGraph<Point2D, Segment2D> fillNewGraphWithArcsAndEdges(
		Multimap<Point2D, Point2D> arcs,
		List<Segment2D> edges
	) {
		assert !arcs.isEmpty();
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
