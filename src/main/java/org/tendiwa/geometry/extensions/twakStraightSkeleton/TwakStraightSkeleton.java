package org.tendiwa.geometry.extensions.twakStraightSkeleton;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.straightSkeleton.CycleExtraVerticesRemover;
import org.tendiwa.geometry.extensions.twakStraightSkeleton.ui.Bar;
import org.tendiwa.geometry.extensions.twakStraightSkeleton.utils.*;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is a facade for <a href="http://twak.blogspot.ru/2009/10/skeleton-index-page.html">a straight skeleton
 * implementation</a> written by a fella named twak.
 */
public class TwakStraightSkeleton implements StraightSkeleton {

	private final Collection<Output.SharedEdge> edges;
	private final List<Segment2D> originalEdges;

	public static StraightSkeleton create(List<Point2D> vertices) {
		assert vertices.size() > 2 : "Too little vertices: " + vertices.size();
		if (JTSUtils.isYDownCCW(vertices)) {
			vertices = Lists.reverse(vertices);
		}
		vertices = CycleExtraVerticesRemover.removeVerticesOnLineBetweenNeighbors(vertices);
		if (vertices.size() < 3) {
			throw new GeometryException("Trying to create a straight skeleton of a polygon with less than 3 vertices");
		}
		LoopL<Bar> edges = new LoopL<>();
		Loop<Bar> aloop = new Loop<>();
		edges.add(aloop);

		List<Point2d> transformedPoints = vertices.stream().map(v -> new Point2d(v.x, v.y)).collect(Collectors.toList());
		assert !transformedPoints.isEmpty();
		for (Pair<Point2d, Point2d> pair : new ConsecutivePairs<>(transformedPoints, true)) {
			aloop.append(new Bar(pair.first(), pair.second()));
		}

		// controls the gradient of the edge
		Machine machine = new Machine(Math.PI / 4);

		final LoopL<Edge> out = new LoopL<>();
		for (Loop<Bar> lb : edges) {
			Loop<Edge> loop = new Loop<>();
			out.add(loop);

			for (Bar bar : lb) {
				// 3D representation of 2D ui input
				Edge e = new Edge(
					new Point3d(bar.start.x, bar.start.y, 0),
					new Point3d(bar.end.x, bar.end.y, 0),
					Math.PI / 4);

				e.machine = machine;

				loop.append(e);
			}

			// the points defining the start and end of a loop must be the same object
			for (Loopable<Edge> le : loop.loopableIterator())
				le.get().end = le.getNext().get().start;
		}

		Skeleton skeleton = new Skeleton(out, true);
		try {
			skeleton.skeleton();
		} catch (RuntimeException e) {
			List<Segment2D> originalEdges1 = pointsToSegments(
				vertices
					.stream()
					.map(v -> new Point2D(v.x + 300, v.y - 200))
					.collect(Collectors.toList())
			);

			new TestCanvas(1, 600, 600).drawAll(originalEdges1, DrawingSegment2D.withColor(Color.red));
			throw e;
		}
		return new TwakStraightSkeleton(skeleton, pointsToSegments(vertices));
	}

	private TwakStraightSkeleton(Skeleton skeleton, List<Segment2D> originalEdges) {
		this.originalEdges = originalEdges
			.stream()
			.map(e -> new Segment2D(e.end, e.start))
			.collect(Collectors.toList());
		edges = skeleton.output.edges.map.values();
	}


	@Override
	public UndirectedGraph<Point2D, Segment2D> graph() {
		SimpleGraph<Point2D, Segment2D> graph = new SimpleGraph<>(org.tendiwa.geometry.extensions.PlanarGraphs.getEdgeFactory());
		edges
			.stream()
			.map(
				e -> new Segment2D(
					new Point2D(e.start.x, e.start.y),
					new Point2D(e.end.x, e.end.y)
				)
			)
			.filter(a -> !originalEdges.contains(a) && !originalEdges.contains(a.reverse()))
			.forEach(segment -> {
				if (!graph.containsEdge(segment.end, segment.start)) {
					graph.addVertex(segment.start);
					graph.addVertex(segment.end);
					graph.addEdge(segment.start, segment.end, segment);
				}
			});
		return graph;
	}

	/**
	 * Builds new polygons by shrinking this one.
	 *
	 * @param depth
	 * 	How much to shrink this polygon.
	 * @return A planar graph of polygons' edges.
	 * @throws java.lang.UnsupportedOperationException
	 * 	if depth is negative. May be implemented in future.
	 */
	@Override
	public ImmutableSet<Polygon> cap(double depth) {
		if (depth < 0) {
			throw new UnsupportedOperationException("Negative depth in not implemented yet");
		}
//		SimpleGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);
//		Point2D previous = null;
//		Loop<Corner> corners = skeleton.capCopy(depth).get(0);
//		Point2D first = new Point2D(
//			corners.getFirst().x,
//			corners.getFirst().y
//		);
//		for (Corner corner : corners) {
//			Point2D current = new Point2D(corner.x, corner.y);
//			graph.addVertex(current);
//			if (previous != null) {
//				graph.addEdge(previous, current);
//			}
//			previous = current;
//		}
//		graph.addEdge(previous, first);
//		return graph;

		throw new UnsupportedOperationException();
//		return new TwakPolygonShrinker(graph(), originalEdges, depth).asGraph();
	}

	@Override
	public Set<Polygon> faces() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Segment2D> originalEdges() {
		return originalEdges;
	}

	static List<Segment2D> pointsToSegments(List<Point2D> points) {
		List<Segment2D> originalEdges = new ArrayList<>(points.size());
		Point2D previous = points.get(points.size() - 1);
		for (Point2D current : points) {
			originalEdges.add(new Segment2D(
				previous,
				current
			));
			previous = current;
		}
		return originalEdges;
	}
}
