package org.tendiwa.geometry.extensions;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.tendiwa.geometry.*;

import java.util.ArrayList;
import java.util.List;

public final class InnerFreeSpaceOfPolygon {
	private InnerFreeSpaceOfPolygon() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated");
	}

	/**
	 * For each edge of a polygon, finds how long would be the segment starting from the middle of the edge,
	 * going inwards until it collides with another edge of the polygon.
	 *
	 * @param polygon
	 * 	A polygon.
	 * @return List of edges of a polygon, sorted by aforementioned criteria. The longer the segment from edge to
	 * another edge, the lesser the index.
	 */
	public static List<Segment2D> compute(List<Point2D> polygon) {
		boolean counterClockWise = JTSUtils.isYDownCCW(polygon);
		int polygonSize = polygon.size();

		List<Segment2D> edges = new ArrayList<>(polygonSize);
		int lastButOne = polygonSize - 1;
		for (int i = 0; i < lastButOne; i++) {
			edges.add(new Segment2D(polygon.get(i), polygon.get(i + 1)));
		}
		edges.add(new Segment2D(polygon.get(lastButOne), polygon.get(0)));

		assert !ShamosHoeyAlgorithm.areIntersected(edges);
		TObjectDoubleMap<Segment2D> map = new TObjectDoubleHashMap<>(polygonSize);
		double[] distancesToOtherEdges = new double[polygonSize];
		for (int i = 0; i < polygonSize; i++) {
			Segment2D edge = edges.get(i);
			Vector2D perpendicular = Vector2D.fromStartToEnd(edge.start, edge.end).rotateQuarterClockwise();
			if (counterClockWise) {
				perpendicular.multiply(-1);
			}
			Point2D edgeCenter = edge.start.moveBy(edge.dx() / 2, edge.dy() / 2);
			Point2D target = edgeCenter.add(perpendicular);
			for (int j = 0; j < polygonSize; j++) {
				if (i == j) {
					distancesToOtherEdges[j] = Double.MAX_VALUE;
					continue;
				}
				Segment2D anotherEdge = edges.get(j);
				RayIntersection rayIntersection = new RayIntersection(edgeCenter, target, anotherEdge);
				if (!rayIntersection.intersects) {
					distancesToOtherEdges[j] = Double.MAX_VALUE;
					continue;
				}
				Point2D intersectionPoint = rayIntersection.commonPoint();
				if (rayIntersection.r < 0 || !pointInSegmentRectangle(intersectionPoint, anotherEdge)) {
					distancesToOtherEdges[j] = Double.MAX_VALUE;
					continue;
				}
				assert rayIntersection.r != 0;
				distancesToOtherEdges[j] = rayIntersection.r;
			}
			double min = Double.MAX_VALUE;
			for (int j = 0; j < polygonSize; j++) {
				if (min > distancesToOtherEdges[j]) {
					min = distancesToOtherEdges[j];
				}
			}
			assert min > 0 : min;
			map.put(edge, min * perpendicular.magnitude());
		}
		List<Segment2D> answer = new ArrayList<>(polygonSize);
		answer.addAll(map.keySet());
		answer.sort((o1, o2) -> {
			if (map.get(o1) < map.get(o2)) {
				return 1;
			}
			if (map.get(o1) > map.get(o2)) {
				return -1;
			}
			return 0;
		});
		return answer;
	}

	private static boolean pointInSegmentRectangle(Point2D point, Segment2D segment) {
		return point.x >= segment.start.x && point.x <= segment.end.x
			|| point.x >= segment.end.x && point.x <= segment.start.x;
	}
}
