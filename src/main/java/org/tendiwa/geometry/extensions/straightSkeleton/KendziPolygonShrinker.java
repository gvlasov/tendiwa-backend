package org.tendiwa.geometry.extensions.straightSkeleton;

import kendzi.math.geometry.polygon.PolygonList2d;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import javax.vecmath.Point2d;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KendziPolygonShrinker extends PolygonShrinker {
	public KendziPolygonShrinker(List<PolygonList2d> faces, List<Point2d> edges, double depth) {
		shrunkPolygonsSegments = findShrunkPolygonsSegments(
			depth,
			mapOriginalEdgesToFaces(edges, faces)
		);
		assert !shrunkPolygonsSegments.isEmpty();
	}

	private Map<Segment2D, Iterable<Segment2D>> mapOriginalEdgesToFaces(
		List<Point2d> originalEdgePoints,
		List<PolygonList2d> faces
	) {
		List<Segment2D> originalEdges = pointsToSegments(originalEdgePoints);
		Map<Segment2D, Iterable<Segment2D>> edgeToFace = new HashMap<>();
		for (PolygonList2d face : faces) {
			Stream<Point2d> map = face
				.getPoints()
				.stream();
			Set<Point2D> cycleVertices = map
				.map(p -> new Point2D(p.x, p.y))
				.collect(Collectors.toSet());
			Segment2D originalFaceEdge = null;
			for (Segment2D edge : originalEdges) {
				if (cycleVertices.contains(edge.start) && cycleVertices.contains(edge.end)) {
					originalFaceEdge = edge;
					break;
				}
			}
			assert originalFaceEdge != null;
			edgeToFace.put(originalFaceEdge, pointsToSegments(
				face.getPoints()
			));
		}
		return edgeToFace;
	}

	static List<Segment2D> pointsToSegments(List<Point2d> points) {
		List<Segment2D> originalEdges = new ArrayList<>(points.size());
		Point2d previous = points.get(points.size() - 1);
		for (Point2d current : points) {
			originalEdges.add(new Segment2D(
				new Point2D(previous.x, previous.y),
				new Point2D(current.x, current.y)
			));
			previous = current;
		}
		return originalEdges;
	}
}
