package org.tendiwa.geometry.extensions.straightSkeleton;

import kendzi.math.geometry.polygon.PolygonList2d;
import kendzi.math.geometry.skeleton.Skeleton;
import kendzi.math.geometry.skeleton.SkeletonOutput;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.StraightSkeleton;

import javax.vecmath.Point2d;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KendziStraightSkeleton implements StraightSkeleton {

	private final List<PolygonList2d> faces;
	private final List<Point2d> edges;

	public KendziStraightSkeleton(List<Point2D> points) {
		edges = points.stream().map(p -> new Point2d(p.x, p.y)).collect(Collectors.toList());
		SkeletonOutput skeleton = Skeleton.skeleton(edges);
		faces = skeleton.getFaces();
	}

	@Override
	public UndirectedGraph<Point2D, Segment2D> graph() {
		return null;
	}

	@Override
	public Set<Polygon> cap(double depth) {
		throw new UnsupportedOperationException();
//		return new KendziPolygonShrinker(faces, edges, depth).asGraph();
	}

	@Override
	public Set<Polygon> faces() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Segment2D> originalEdges() {
		return KendziPolygonShrinker.pointsToSegments(edges);
	}
}
