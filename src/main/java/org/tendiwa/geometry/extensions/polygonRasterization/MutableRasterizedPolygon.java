package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.geometry.*;

import java.util.*;

/**
 * The algorithm is described here: <a href="http://habrahabr.ru/post/116398/">Алгоритмы со списком рёберных
 * точек</a>.
 * <p>
 * The base algorithm is pretty simple. However, this method's complexity comes from handling the case where there
 * are several consecutive points of a polygon on the same horizontal line with a whole number y-coordinate.
 * <p>
 */
public final class MutableRasterizedPolygon extends Mutable2DCellSet {

	private final Polygon polygon;

	public MutableRasterizedPolygon(Polygon poly) {
		super(poly.integerBounds());
		Rectangle bounds = poly.integerBounds();
		this.polygon = new CollapsedHorizontalChainsPolygon(poly);
		int numberOfVertices = polygon.size();
		Map<Point2D, PointSlidingOnEdge> map = new HashMap<>(numberOfVertices);
		polygon.forEach(v -> map.put(v, new PointSlidingOnEdge()));
		for (int y = bounds.y(); y <= bounds.maxY(); y++) {
			int numberOfIntersections = 0;
			/* Here are saved pairs of indices of vertices for which (1) vertex.y == y
			 * and (2) for that pair of indices a vertex before the 0th index and a vertex after the 1st index
			 * are from different sides from the horizontal line y. */
			List<int[]> consecutiveSegments = null;
			Point2D vertex;
			for (int i = 0; i < numberOfVertices; i++) {
				vertex = polygon.get(i);
				Point2D nextVertex = polygon.get(Utils.nextIndex(i, numberOfVertices));
				if (vertex.y() < y && nextVertex.y() > y || vertex.y() > y && nextVertex.y() < y) {
					numberOfIntersections++;
				} else if (vertex.y() == y) {
					/*
					If we encounter a point right on the horizontal line y, then there are 0+ points after it on line
					 y (there will usually be 0 points after it).
					 */
					int[] segment = new int[2];
					segment[0] = i;
					while (i < numberOfVertices && polygon.get(i + 1 == numberOfVertices ? 0 : i + 1).y() == y) {
						// Modification of the counter!
						i++;
					}
					segment[1] = i == numberOfVertices ? 0 : i;
					Point2D point1 = polygon.get(segment[0]);
					Point2D point2 = polygon.get(segment[1]);
					assert point1.y() == point2.y();
					boolean westToEast = true;
					if (point1.x() > point2.x()) {
						// Index of a point with lesser x-coordinate must be first.
						westToEast = false;
						int buf = segment[0];
						segment[0] = segment[1];
						segment[1] = buf;
					}
					Point2D westFromFirstPoint = polygon.get(
						westToEast ? Utils.previousIndex(segment[0], numberOfVertices)
							: Utils.nextIndex(segment[0], numberOfVertices)
					);
					int nextIndexAfterEastern = Utils.nextIndex(segment[1], numberOfVertices);
					Point2D eastFromLastPoint = polygon.get(
						westToEast ? nextIndexAfterEastern : Utils.previousIndex(segment[1], numberOfVertices)
					);
					if (Math.signum(westFromFirstPoint.y() - y) == -Math.signum(eastFromLastPoint.y() - y)) {
						if (consecutiveSegments == null) {
							consecutiveSegments = new LinkedList<>();
						}
						consecutiveSegments.add(segment);
						numberOfIntersections++;
					}
				}
			}
			assert numberOfIntersections % 2 == 0 || numberOfIntersections == 1 && consecutiveSegments.size() == 1;

			Object[] intersections = new Object[numberOfIntersections];
			int j = 0;
			vertex = polygon.get(0);
			for (int i = 0; i < numberOfVertices; i++) {
				Point2D nextVertex = polygon.get(i + 1 == numberOfVertices ? 0 : i + 1);
				if (vertex.y() < y && nextVertex.y() > y || vertex.y() > y && nextVertex.y() < y) {
					PointSlidingOnEdge pointSlidingOnEdge = map.get(vertex);
					pointSlidingOnEdge.setToIntersection(vertex, nextVertex, y);
					intersections[j++] = pointSlidingOnEdge;
				}
				vertex = nextVertex;
			}
			if (consecutiveSegments != null) {
				for (int[] segment : consecutiveSegments) {
					intersections[j++] = segment;
				}
			}
			assert j == numberOfIntersections;

			Arrays.sort(intersections, (Object a, Object b) -> {
				double valueA, valueB;
				if (a instanceof PointSlidingOnEdge) {
					valueA = ((PointSlidingOnEdge) a).x;
				} else {
					assert a instanceof int[];
					valueA = polygon.get(((int[]) a)[0]).x();
				}
				if (b instanceof PointSlidingOnEdge) {
					valueB = ((PointSlidingOnEdge) b).x;
				} else {
					assert a instanceof int[];
					valueB = polygon.get(((int[]) b)[0]).x();
				}
				return Double.compare(valueA, valueB);
			});
			assert intersections.length % 2 == 0 || intersections.length == 1 && intersections[0] instanceof int[]
				: intersections.length + ", y=" + y;

			if (intersections.length == 1) {
				assert intersections[0] instanceof int[];
				int[] array = (int[]) intersections[0];
				double ax = polygon.get(array[0]).x();
				double bx = polygon.get(array[1]).x();
				assert bx > ax;
				fillHorizontalSegment(new RasterizationSegment(ax, bx, y));
			} else {
				for (int i = 0; i < intersections.length; i += 2) {
					Object a = intersections[i];
					Object b = intersections[i + 1];
					double ax;
					double bx;
					if (a instanceof PointSlidingOnEdge) {
						ax = ((PointSlidingOnEdge) a).x;
					} else {
						assert a instanceof int[];
						int[] aint = (int[]) a;
						int nextGreaterXIndex = aint[0] > aint[1] ?
							Utils.previousIndex(aint[1], numberOfVertices)
							: Utils.nextIndex(aint[1], numberOfVertices);
						if (nextGreaterXIndex == numberOfVertices) {
							nextGreaterXIndex = 0;
						}
						ax = polygon.get(aint[0]).x();
					}
					if (b instanceof PointSlidingOnEdge) {
						bx = ((PointSlidingOnEdge) b).x;
					} else {
						assert b instanceof int[];
						int[] bint = (int[]) b;
						int nextGreaterXIndex = bint[0] > bint[1] ?
							Utils.previousIndex(bint[1], numberOfVertices)
							: Utils.nextIndex(bint[1], numberOfVertices);
						if (nextGreaterXIndex == numberOfVertices) {
							nextGreaterXIndex = 0;
						}
						bx = polygon.get(bint[1]).x();
					}
					assert bx > ax;
					fillHorizontalSegment(new RasterizationSegment(ax, bx, y));
				}
			}
		}
		drawIntegerHorizontalEdges();
	}

	private void drawIntegerHorizontalEdges() {
		polygon.toSegments().stream()
			.filter(this::edgeNeedsExplicitRasterization)
			.map(BasicCellSegment::new)
			.forEach(this::fillWithCells);
	}

	private boolean edgeNeedsExplicitRasterization(Segment2D segment) {
		double startY = segment.start().y();
		if (startY != Math.floor(startY)) {
			return false;
		}
		double endY = segment.end().y();
		return startY == endY;
	}

	private void fillWithCells(Iterable<Cell> cells) {
		cells.forEach(
			cell -> {
				if (!this.contains(cell)) {
					this.add(cell);
				}
			}
		);
	}
}