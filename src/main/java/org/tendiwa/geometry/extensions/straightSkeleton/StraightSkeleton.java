package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.Lists;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.settlements.LineIntersection;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import static org.tendiwa.graphs.MinimumCycleBasis.perpDotProduct;

public class StraightSkeleton {

	private final ListOfActiveVertices lav;
	private final List<Segment2D> edges;
	TestCanvas canvas = new TestCanvas(2, 200, 200);

	public StraightSkeleton(MinimalCycle<Point2D, Segment2D> cycle) {
		// Transform clockwise list to a counter-clockwise list.
		this(Lists.reverse(cycle.vertexList()), true);

	}

	public StraightSkeleton(List<Point2D> vertices) {
		this(vertices, false);
	}

	private StraightSkeleton(List<Point2D> vertices, boolean trustCounterClockwise) {
		if (!trustCounterClockwise && !testCounterClockwise(vertices)) {
			vertices = Lists.reverse(vertices);
		}
		int l = vertices.size();
		edges = new ArrayList<>(l);
		for (int i = 0; i < l; i++) {
			edges.add(
				new Segment2D(
					vertices.get(i),
					vertices.get(i + 1 < l ? i + 1 : 0)
				)
			);
			canvas.draw(edges.get(i), DrawingSegment2D.withColor(Color.RED));
		}


		this.lav = new ListOfActiveVertices(vertices, edges);
		// [Obdrzalek 1998, paragraph 2.2, algorithm step 1c]
		int i = 0;
		for (
			Node node = lav.nodes.getFirst();
			i < l;
			i++, node = node.next
			) {
			Point2D intersectionPoint = computeNearerBisectorsIntersection(node);
			if (intersectionPoint != null) {
				canvas.draw(intersectionPoint, DrawingPoint2D.withColorAndSize(Color.GREEN, 1));
			}
		}
	}

	private static boolean testCounterClockwise(List<Point2D> vertices) {
		int l = vertices.size();
		Coordinate[] coordinates = new Coordinate[l + 1];
		int i = 0;
		for (Point2D point : vertices) {
			coordinates[i++] = new Coordinate(point.x, point.y);
		}
		coordinates[l] = coordinates[0];
		return CGAlgorithms.isCCW(coordinates);
	}

	private Point2D computeNearerBisectorsIntersection(Node node) {
		LineIntersection next = node.bisector.intersectionWith(node.next.bisector);
		LineIntersection previous = node.bisector.intersectionWith(node.previous.bisector);
		Point2D nearer = null;
		if (next.r > 0 && previous.r > 0) {
			if (next.r < previous.r) {
				nearer = next.getIntersectionPoint();
			} else {
				nearer = previous.getIntersectionPoint();
			}
		} else if (next.r > 0) {
			nearer = next.getIntersectionPoint();
		} else if (previous.r > 0) {
			nearer = previous.getIntersectionPoint();
		}
		if (node.isReflex) {
			Point2D splitPoint = findSplitEvent(node);
			if (
				nearer == null
					|| splitPoint != null
					&& node.vertex.distanceTo(splitPoint) < node.vertex.distanceTo(nearer)
				) {
				nearer = splitPoint;
			}
		}
		return nearer;
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, figure 4]
	 * <p>
	 * Computes the point where a split event occurs.
	 *
	 * @return The point where split event occurs, or null if there is no split event.
	 */
	private Point2D findSplitEvent(Node reflexNode) {
		assert reflexNode.isReflex;
		Point2D splitPoint = null;
		for (Node node : lav.nodes) {
			if (
				new LineIntersection(
					node.bisector.segment.start, node.next.bisector.segment.end,
					node.currentEdge
				).r < 0
				) {
				continue;
			}
			Point2D point = computeSplitPoint(reflexNode, node.currentEdge);
			if (isPointInAreaBetweenEdgeAndItsBisectors(point, node)) {
				if (
					splitPoint == null
						|| reflexNode.vertex.distanceTo(splitPoint) > reflexNode.vertex.distanceTo(point)
					)
					splitPoint = point;
			}
		}
		return splitPoint;
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, Figure 4]
	 * <p>
	 * Computes point B_i.
	 *
	 * @param currentNode
	 * 	A reflex node that creates a split event.
	 * @param oppositeEdge
	 * 	The tested line segment.
	 * @return Intersection between the bisector at {@code currentNode} and the axis of the angle between one of the
	 * edges starting at {@code currentNode} and the tested line segment {@code oppositeEdge}.
	 */
	private static Point2D computeSplitPoint(Node currentNode, Segment2D oppositeEdge) {
		assert currentNode.isReflex;
		Point2D intersectionPoint = new LineIntersection(
			currentNode.previous.vertex, currentNode.vertex, oppositeEdge
		).getIntersectionPoint();
		return new Bisector(
			new Segment2D(currentNode.vertex, intersectionPoint),
			new Segment2D(intersectionPoint, oppositeEdge.end)
		).intersectionWith(currentNode.bisector).getIntersectionPoint();
	}

	/**
	 * [Obdrzalek 1998, paragraph 2.2, Figure 4]
	 * <p>
	 * Checks if a point (namely point B coming from a reflex vertex) is located in an area bounded by an edge and
	 * bisectors coming from start and end nodes of this edge.
	 *
	 * @param point
	 * 	The point to test.
	 * @param currentNode
	 * 	A node at which starts the area-forming edge.
	 * @return true if the point is located within the area marked by an edge and edge's bisectors, false otherwise.
	 */
	private static boolean isPointInAreaBetweenEdgeAndItsBisectors(Point2D point, Node currentNode) {
		Bisector currentBisector = currentNode.bisector;
		Bisector nextBisector = currentNode.next.bisector;
		Point2D a = currentBisector.segment.end;
		Point2D b = currentNode.vertex;
		Point2D c = currentNode.next.vertex;
		Point2D d = nextBisector.segment.end;
		return isPointReflex(a, point, b) && isPointReflex(b, point, c) && isPointReflex(c, point, d);
	}

	/**
	 * Given 3 counter-clockwise points of a polygon, check if the middle one is convex or reflex.
	 *
	 * @param previous
	 * 	Beginning of vector 1.
	 * @param point
	 * 	End of vector 1 and beginning of vector 2.
	 * @param next
	 * 	End of vector 2.
	 * @return true if {@code point} is reflex, false if it is convex of if all points lie on the same line.
	 */
	private static boolean isPointReflex(Point2D previous, Point2D point, Point2D next) {
		return perpDotProduct(
			new double[]{point.x - previous.x, point.y - previous.y},
			new double[]{next.x - point.x, next.y - point.y}
		) > 0;
	}

}
