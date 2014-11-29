package org.tendiwa.geometry.extensions;

import org.tendiwa.core.meta.Range;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.geometry.LineCircleIntersection;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Places points on the border of a polygon, attempting to choose such point positions that distance between
 * consecutive points equals certain number ± certain deviation.
 * <p>
 * For some concave polygons with accordion-shaped parts this algorithm may produce points that are too close to each
 * other, because the algorithm keeps track only of the last point, while accordion shape allows points to be placed
 * in boundaries of some previous point after traversing several edges.
 */
public final class IntervalsAlongPolygonBorder {

	private final Random random;
	private final List<Point2D> polygon;
	private final double interval;
	private final double deviation;
	private final int numOfVertices;
	private final BiFunction<Point2D, Point2D, Segment2D> endpointsToSegments;
	private final Map<Segment2D, List<Point2D>> answer;
	private Point2D nextVertex;
	private Point2D currentVertex;
	private int nextNextVertexIndex;
	private Point2D firstAnswerPoint;
	private Point2D lastCircleIntersection;
	private double intervalToSpend;
	private boolean returnedToEdgeIndex0 = false;
	private boolean returnedToEdgeIndex1 = false;
	private boolean isChainEnclosed = false;
	private List<Point2D> currentAnswerList;

	private IntervalsAlongPolygonBorder(List<Point2D> polygon, double interval, double deviation, BiFunction<Point2D, Point2D, Segment2D> endpointsToSegments, Random random) {
		if (interval <= 0) {
			throw new IllegalArgumentException("Interval must be >= 0, now it is " + interval);
		}
		if (deviation >= interval) {
			throw new IllegalArgumentException("Variance must be less than interval (now " + deviation + ">=" + interval + ")");
		}
		if (polygon.size() < 4) {
			throw new IllegalArgumentException(
				"There must be at least 3 edges in a polygon (it has size " + polygon.size() + ")"
			);
		}
		this.random = new Random(random.nextInt());
		this.endpointsToSegments = endpointsToSegments;
		this.polygon = polygon;
		this.interval = interval;
		this.deviation = deviation;
		this.numOfVertices = polygon.size();
		this.answer = new HashMap<>();
	}

	public Map<Segment2D, List<Point2D>> compute() {
		setUpChainStartPoint();
		while (!isChainEnclosed) {
			List<Point2D> intersections = computeIntersectionsOfCircleWithSegment();
			if (intersections.isEmpty()) {
				moveToNextSegment();
			} else {
				tryPlacingNextPointOnCurrentSegment(intersections);
			}
		}
		return answer;
	}

	private List<Point2D> computeIntersectionsOfCircleWithSegment() {
		List<Point2D> intersections = LineCircleIntersection.findIntersections(
			currentVertex,
			nextVertex,
			lastCircleIntersection,
			intervalToSpend
		);
		double minX = Math.min(currentVertex.x, nextVertex.x);
		double maxX = Math.max(currentVertex.x, nextVertex.x);
		double minY = Math.min(currentVertex.y, nextVertex.y);
		double maxY = Math.max(currentVertex.y, nextVertex.y);
		assert minX != maxX && minY != maxY; // I just don't know what to do in this case, though it is legit.
		intersections.removeIf(p -> !Range.contains(minX, maxX, p.x) || !Range.contains(minY, maxY, p.y));
		assert intersections.stream().allMatch(p -> !p.equals(nextVertex)); // I just don't knot what to do in this case, though it is legit.
		return intersections;
	}

	/**
	 * Places a point on the polygon border if the point to place doesn't go past the 0'th point from the other end
	 * after making the full cycle.
	 *
	 * @param intersections
	 * 	[0..2] points where a circle intersects the current edge.
	 */
	private void tryPlacingNextPointOnCurrentSegment(List<Point2D> intersections) {
		assert intersections.size() <= 2;
		Point2D intersectionPoint;
		if (intersections.size() == 2) {
			intersectionPoint = getPointCloserToNextPoint(intersections);
		} else {
			intersectionPoint = intersections.get(0);
		}
		if (isBeforeCurrentPoint(intersectionPoint)) {
			moveToNextSegment();
			if (returnedToEdgeIndex1) {
				isChainEnclosed = true;
			}
		} else {
			placePointOnCurrentSegment(intersectionPoint);
			if (returnedToEdgeIndex1 || returnedToEdgeIndex0 && isCircleIntersectionBetween0thPointAnd1stVertex()) {
				undoPlacingLastPoint();
				isChainEnclosed = true;
			}
		}
	}

	private void undoPlacingLastPoint() {
		currentAnswerList.remove(currentAnswerList.size() - 1);
	}

	private void placePointOnCurrentSegment(Point2D point) {
		lastCircleIntersection = point;
		currentAnswerList.add(lastCircleIntersection);
		generateNextDistance();
	}

	private boolean isCircleIntersectionBetween0thPointAnd1stVertex() {
		assert returnedToEdgeIndex0;
		Point2D polygon1 = polygon.get(1);
		double minX = Math.min(firstAnswerPoint.x, polygon1.x);
		double maxX = Math.max(firstAnswerPoint.x, polygon1.x);
		double minY = Math.min(firstAnswerPoint.y, polygon1.y);
		double maxY = Math.max(firstAnswerPoint.y, polygon1.y);
		return Range.contains(minX, maxX, lastCircleIntersection.x)
			&& Range.contains(minY, maxY, lastCircleIntersection.y);
	}

	/**
	 * Places the first point of the result at the beginning of the first segment in a randomized distance from the
	 * first point of the polygon, and sets up all the fields necessary for computation.
	 */
	private void setUpChainStartPoint() {
		nextVertex = polygon.get(1);
		currentVertex = polygon.get(0);
		lastCircleIntersection = findFirstPointPosition(currentVertex, nextVertex);
		firstAnswerPoint = lastCircleIntersection;
		nextNextVertexIndex = 2;
		switchToNewSegmentList(currentVertex, nextVertex);
		currentAnswerList.add(lastCircleIntersection);
		generateNextDistance();
	}

	private void switchToNewSegmentList(Point2D currentPoint, Point2D nextPoint) {
		Segment2D segment = endpointsToSegments.apply(currentPoint, nextPoint);
		if (answer.containsKey(segment)) {
			currentAnswerList = answer.get(segment);
		} else {
			currentAnswerList = new ArrayList<>(getOptimalListSize(segment));
			answer.put(segment, currentAnswerList);
		}
	}

	private int getOptimalListSize(Segment2D segment) {
		return (int) Math.ceil(segment.length() / interval + 1);
	}

	private void moveToNextSegment() {
		currentVertex = nextVertex;
		nextVertex = polygon.get(nextNextVertexIndex);
		switchToNewSegmentList(currentVertex, nextVertex);
		nextNextVertexIndex = Utils.nextIndex(numOfVertices, nextNextVertexIndex);
		if (nextNextVertexIndex == 2) {
			returnedToEdgeIndex0 = true;
		}
		if (returnedToEdgeIndex0 && nextNextVertexIndex == 3) {
			returnedToEdgeIndex1 = true;
		}
	}

	/**
	 * @param polygon
	 * 	Points of a polygon, going consecutively.
	 * @param interval
	 * 	A default interval size.
	 * @param deviation
	 * 	A value within which {@code interval} may randomly change.
	 * @param random
	 * 	Source of randomness.
	 * @return A map from segments of a {@code polygon} to points on those segments.
	 */
	public static Map<Segment2D, List<Point2D>> compute(
		List<Point2D> polygon,
		double interval,
		double deviation,
		BiFunction<Point2D, Point2D, Segment2D> endpointsToSegments,
		Random random
	) {
		return new IntervalsAlongPolygonBorder(polygon, interval, deviation, endpointsToSegments, random).compute();
	}

	private boolean isBeforeCurrentPoint(Point2D intersectionPoint) {
		return intersectionPoint.squaredDistanceTo(nextVertex) > lastCircleIntersection.squaredDistanceTo(nextVertex);
	}

	private Point2D getPointCloserToNextPoint(List<Point2D> points) {
		assert points.size() == 2;
		Point2D point1 = points.get(0);
		Point2D point2 = points.get(1);
		return point1.squaredDistanceTo(nextVertex) < point2.squaredDistanceTo(nextVertex) ? point1 : point2;
	}

	private void generateNextDistance() {
		intervalToSpend = interval + deviation * 2 * (random.nextDouble() - 0.5);
	}

	/**
	 * Computes the first point of the answer. First point of the answer always lies on the first segment of the
	 * polygon in a randomized position
	 *
	 * @param start
	 * 	Starting point of a segment.
	 * @param end
	 * 	End point of a segment.
	 * 	{@link org.tendiwa.geometry.Segment2D#end}. May be between 0.0 and 1.0
	 * @return The middle point of an edge.
	 */
	private Point2D findFirstPointPosition(Point2D start, Point2D end) {
		double position = random.nextDouble() * Math.min(1, interval / start.distanceTo(end));
		return new Point2D(start.x + (end.x - start.x) * position, start.y + (end.y - start.y) * position);
	}
}
