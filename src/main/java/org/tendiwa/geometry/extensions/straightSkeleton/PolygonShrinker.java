package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.*;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.RayIntersection;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.awt.Color;
import java.util.*;

public class PolygonShrinker {

	public List<Segment2D> edges;
	protected List<Segment2D> shrunkPolygonsSegments;
	static Iterator<Color> colors = Iterators.cycle(Color.green, Color.blue, Color.orange, Color.yellow);
	private Set<Polygon> polygons;

	protected PolygonShrinker() {
	}

	/**
	 * @param faces
	 * 	Clockwise polygons where first element in list is the end of the face's original edge, and the
	 * 	last element in list is the start of the face's original edge.
	 * @param depth
	 * 	How much to intrude the polygon.
	 */
	public PolygonShrinker(
		Set<Polygon> faces,
		double depth
	) {
		// Minimum possible number of points on a front is faces.size(), so we pick a value twice as big. That should
		// be enough for most cases and not too much.
		ShrinkedFront front = new ShrinkedFront(faces.size() * 2);
		BiMap<RayIntersection, Segment2D> intersectionsOnSegments = HashBiMap.create();
		for (Polygon face : faces) {
			Queue<Point2D> queue = new PriorityQueue<>((Point2D a, Point2D b) -> {
				int signum = (int) Math.signum(a.x - b.x);
				if (signum == 0) {
					return (int) Math.signum(a.y - b.y);
				}
				return signum;
			});
			Segment2D intruded = new Segment2D(
				face.get(0),
				face.get(face.size() - 1)
			).createParallelSegment(depth, true);
			face.asSegments().forEach(segment -> {
				Segment2D reverse = segment.reverse();
				if (intersectionsOnSegments.containsValue(reverse)) {
					Point2D newPoint = intersectionsOnSegments
						.inverse()
						.get(reverse)
						.getLinesIntersectionPoint();
					queue.add(newPoint);
					assert Boolean.TRUE;
				} else {
					assert !intersectionsOnSegments.containsValue(segment);
					RayIntersection intersection = new RayIntersection(
						segment,
						intruded
					);
					if (intersection.r > 0 && intersection.r < 1) {
						RayIntersection anotherIntersection = new RayIntersection(intruded, segment);
						intersectionsOnSegments.put(anotherIntersection, segment);
						Point2D newPoint = anotherIntersection.getLinesIntersectionPoint();
						queue.add(newPoint);
						assert Boolean.TRUE;
					}
				}
			});

			assert queue.size() % 2 == 0 : queue.size();
			while (!queue.isEmpty()) {
				front.add(
					// Get two consecutive intersection points
					queue.poll(),
					queue.poll()
				);
			}
		}
		polygons = front.constructPolygons();
	}


	public UndirectedGraph<Point2D, Segment2D> asGraph() {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
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

		Map<Segment2D, Iterable<Segment2D>> edgeToFace = new HashMap<>();

		for (MinimalCycle<Point2D, Segment2D> face : basis.minimalCyclesSet()) {
			Segment2D originalFaceEdge = findUnusedEdgeForFace(
				unusedEdges,
				ImmutableSet.copyOf(face.vertexList())
			);
			assert !edgeToFace.containsKey(originalFaceEdge) : originalFaceEdge;
			// None of faces may include more than 1 original edge.
			// http://twak.blogspot.ru/2011/01/degeneracy-in-weighted-straight.html Ctrl+F parallel consecutive edge
			boolean allMatch = Lists.newArrayList(face).stream().allMatch(p -> !unusedEdges.contains(p));
			if (!allMatch) {
				assert false : "Not all edges of " +
					"straight skeleton could be constructed; presumably because at some stage of skeleton construction 2 " +
					"parallel edges become adjacent; please draw the graph to make sure";
			}
			edgeToFace.put(originalFaceEdge, face);
		}
		return edgeToFace;
	}

	private Segment2D findUnusedEdgeForFace(Set<Segment2D> unusedEdges, Set<Point2D> face) {
		Segment2D originalFaceEdge = null;
		for (Segment2D edge : unusedEdges) {
			assert edge != null;
			if (face.contains(edge.start) && face.contains(edge.end)) {
				originalFaceEdge = edge;
				unusedEdges.remove(originalFaceEdge);
				break;
			}
		}
		assert originalFaceEdge != null;
//		Color next = colors.next();
//		for (Point2D v : face) {
//			TestCanvas.canvas.draw(v, DrawingPoint2D.withColorAndSize(next, 4));
//		}
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

	public Set<Polygon> shrink() {
		return polygons;
	}
}
