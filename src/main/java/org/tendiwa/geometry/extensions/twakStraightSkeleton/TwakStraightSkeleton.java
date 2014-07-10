package org.tendiwa.geometry.extensions.twakStraightSkeleton;

import com.google.common.collect.Lists;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.JTSUtils;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.StraightSkeleton;
import org.tendiwa.geometry.extensions.straightSkeleton.CycleExtraVerticesRemover;
import org.tendiwa.geometry.extensions.twakStraightSkeleton.ui.Bar;
import org.tendiwa.geometry.extensions.twakStraightSkeleton.utils.*;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is a facade for <a href="http://twak.blogspot.ru/2009/10/skeleton-index-page.html">a straight skeleton
 * implementation</a> written by a fella named twak.
 */
public class TwakStraightSkeleton implements StraightSkeleton {

	private final Collection<Output.SharedEdge> edges;
	private final List<Segment2D> originalEdges;

	public static StraightSkeleton create(List<Point2D> vertices) {
		assert vertices.size() > 2 : "list of "+vertices.size();
		if (JTSUtils.isYDownCCW(vertices)) {
			vertices = Lists.reverse(vertices);
		}
		vertices = CycleExtraVerticesRemover.removeVerticesOnLineBetweenNeighbors(vertices);
		LoopL<Bar> edges = new LoopL<>();
		Loop<Bar> aloop = new Loop<>();
		edges.add(aloop);

		List<Point2d> transformedPoints = vertices.stream().map(v -> new Point2d(v.x, v.y)).collect(Collectors.toList());
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
		skeleton.skeleton();
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
		SimpleGraph<Point2D, Segment2D> graph = new SimpleGraph<>(Segment2D::new);
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

	@Override
	public UndirectedGraph<Point2D, Segment2D> cap(double depth) {
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
		System.out.println();
		return new TwakPolygonShrinker(graph(), originalEdges, depth).asGraph();
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
