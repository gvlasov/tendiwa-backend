package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.core.meta.Utils;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.CellSegment;
import org.tendiwa.geometry.Point2D;

import java.util.*;

public final class PolygonRasterizer {

	private PolygonRasterizer() {
		throw new UnsupportedOperationException();
	}

	/**
	 * The algorithm is described here: <a href="http://habrahabr.ru/post/116398/">Алгоритмы со списком рёберных
	 * точек</a>.
	 * <p>
	 * The base algorithm is pretty simple. However, this method's complexity comes from handling the case where there
	 * are several consecutive points of a polygon on the same horizontal line with a whole number y-coordinate.
	 * <p>
	 */
	private static ResultData rasterize(List<Point2D> poly) {
		Objects.requireNonNull(poly);
		if (poly.size() < 3) {
			throw new IllegalArgumentException("Polygon must have at least 3 vertices");
		}
		double minXd = Integer.MAX_VALUE,
			maxXd = Integer.MIN_VALUE,
			minYd = Integer.MAX_VALUE,
			maxYd = Integer.MIN_VALUE;
		int indexWithMinX = Integer.MIN_VALUE;
		int size = poly.size();
		for (int i = 0; i < size; i++) {
			Point2D vertex = poly.get(i);
			if (minXd > vertex.x) {
				minXd = vertex.x;
				indexWithMinX = i;
			}
			if (minYd > vertex.y) {
				minYd = vertex.y;
			}
			if (maxXd < vertex.x) {
				maxXd = vertex.x;
			}
			if (maxYd < vertex.y) {
				maxYd = vertex.y;
			}
		}
		int minX = (int) Math.floor(minXd);
		int maxX = (int) Math.ceil(maxXd);
		int minY = (int) Math.floor(minYd);
		int maxY = (int) Math.ceil(maxYd);
		List<Point2D> polygon = PolygonRebuilding.rebuildIfBad(poly, indexWithMinX);
		int numberOfVertices = polygon.size();
		Map<Point2D, PointSlidingOnEdge> map = new HashMap<>(numberOfVertices);
		polygon.forEach(v -> map.put(v, new PointSlidingOnEdge()));
		// Indices x and y in this array are swapped
		// so Arrays.fill bellow will operate on continuous ranges.
		boolean[][] bitmap = new boolean[maxY - minY + 1][maxX - minX + 1];
		boolean anythingWasRasterized = false;
		for (int y = minY; y <= maxY; y++) {
			int numberOfIntersections = 0;
			/* Here are saved pairs of indices of vertices for which (1) vertex.y == y
			 * and (2) for that pair of indices a vertex before the 0-th index and a vertex after the 1-th index
			 * are from different sides from the horizontal line y. */
			List<int[]> consecutiveSegments = null;
			Point2D vertex;
			for (int i = 0; i < numberOfVertices; i++) {
				vertex = polygon.get(i);
				Point2D nextVertex = polygon.get(i + 1 == numberOfVertices ? 0 : i + 1);
				if (vertex.y < y && nextVertex.y > y || vertex.y > y && nextVertex.y < y) {
					numberOfIntersections++;
				} else if (vertex.y == y) {
					/*
					If we encounter a point right on the horizontal line y, then there are 0+ points after it on line
					 y (there will usually be 0 points after it).
					 */
					int[] segment = new int[2];
					segment[0] = i;
					while (i < numberOfVertices && polygon.get(i + 1 == numberOfVertices ? 0 : i + 1).y == y) {
						// Modification of the counter!
						i++;
					}
					segment[1] = i == numberOfVertices ? 0 : i;
					Point2D point1 = polygon.get(segment[0]);
					Point2D point2 = polygon.get(segment[1]);
					assert point1.y == point2.y;
					boolean westToEast = true;
					if (point1.x > point2.x) {
						// Index of a point with lesser x-coordinate must be first.
						westToEast = false;
						int buf = segment[0];
						segment[0] = segment[1];
						segment[1] = buf;
					}
					Point2D westFromFirstPoint = polygon.get(
						westToEast ? Utils.previousIndex(numberOfVertices, segment[0])
							: Utils.nextIndex(numberOfVertices, segment[0])
					);
					int nextIndexAfterEastern = Utils.nextIndex(numberOfVertices, segment[1]);
					Point2D eastFromLastPoint = polygon.get(
						westToEast ? nextIndexAfterEastern : Utils.previousIndex(numberOfVertices, segment[1])
					);
					if (Math.signum(westFromFirstPoint.y - y) == -Math.signum(eastFromLastPoint.y - y)) {
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
				if (vertex.y < y && nextVertex.y > y || vertex.y > y && nextVertex.y < y) {
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
					valueA = polygon.get(((int[]) a)[0]).x;
				}
				if (b instanceof PointSlidingOnEdge) {
					valueB = ((PointSlidingOnEdge) b).x;
				} else {
					assert a instanceof int[];
					valueB = polygon.get(((int[]) b)[0]).x;
				}
				return Double.compare(valueA, valueB);
			});
			assert intersections.length % 2 == 0 || intersections.length == 1 && intersections[0] instanceof int[]
				: intersections.length + ", y=" + y;

			if (intersections.length == 1) {
				assert intersections[0] instanceof int[];
				int[] array = (int[]) intersections[0];
				double ax = polygon.get(array[0]).x;
				double bx = polygon.get(array[1]).x;
				assert bx > ax;
				Arrays.fill(
					bitmap[y - minY],
					(int) Math.ceil(ax) - minX,
					(int) Math.floor(bx) - minX + 1,
					true
				);
				anythingWasRasterized = true;
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
							Utils.previousIndex(numberOfVertices, aint[1])
							: Utils.nextIndex(numberOfVertices, aint[1]);
						if (nextGreaterXIndex == numberOfVertices) {
							nextGreaterXIndex = 0;
						}
						ax = polygon.get(aint[0]).x;
					}
					if (b instanceof PointSlidingOnEdge) {
						bx = ((PointSlidingOnEdge) b).x;
					} else {
						assert b instanceof int[];
						int[] bint = (int[]) b;
						int nextGreaterXIndex = bint[0] > bint[1] ?
							Utils.previousIndex(numberOfVertices, bint[1])
							: Utils.nextIndex(numberOfVertices, bint[1]);
						if (nextGreaterXIndex == numberOfVertices) {
							nextGreaterXIndex = 0;
						}
						bx = polygon.get(bint[1]).x;
					}
					assert bx > ax;
					Arrays.fill(
						bitmap[y - minY],
						(int) Math.ceil(ax) - minX,
						(int) Math.floor(bx) - minX + 1,
						true
					);
					anythingWasRasterized = true;
				}
			}
		}
		drawPolygonEdgesToBitmap(polygon, bitmap, minX, minY);

		if (!anythingWasRasterized || bitmap[0].length == 0) {
			bitmap = new boolean[0][0];
		}
		return new ResultData(minX, minY, bitmap);
	}

	/**
	 * Rasterizes edges of a polygon and puts their cells to {@code bitmap}.
	 *
	 * @param polygon
	 * 	A polygon to draw.
	 * @param bitmap
	 * 	A bitmap to put cells into.
	 */
	private static void drawPolygonEdgesToBitmap(List<Point2D> polygon, boolean[][] bitmap, int minX, int minY) {
		int polygonSize = polygon.size();
		Point2D vertex = polygon.get(0);
		for (int i = 0; i < polygonSize; i++) {
			Point2D nextVertex = polygon.get(Utils.nextIndex(polygonSize, i));
			Cell[] cells = CellSegment.cells(
				(int) Math.round(vertex.x),
				(int) Math.round(vertex.y),
				(int) Math.round(nextVertex.x),
				(int) Math.round(nextVertex.y)
			);
			for (Cell cell : cells) {
				bitmap[cell.y-minY][cell.x-minX] = true;
			}
			vertex = nextVertex;
		}
	}

	/**
	 * Creates a new {@link org.tendiwa.geometry.BoundedCellSet} that is a raster image of {@code polygon}.
	 *
	 * @param simplePolygon
	 * 	A simple polygon.
	 * @return A new cell set.
	 */
	public static BoundedCellSet rasterizeToCellSet(List<Point2D> simplePolygon) {
		ResultData data = rasterize(simplePolygon);
		return new RasterizationResult(data.minX, data.minY, data.bitmap);
	}

	public static MutableRasterizationResult rasterizeToMutable(List<Point2D> polygon) {
		ResultData data = rasterize(polygon);
		return new MutableRasterizationResult(data.minX, data.minY, data.bitmap);
	}

}
