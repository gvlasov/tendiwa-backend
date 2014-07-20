package org.tendiwa.geometry.extensions;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.linked.TDoubleLinkedList;
import org.tendiwa.geometry.*;

import java.util.Arrays;
import java.util.List;

public class PolygonRasterizer {
	private PolygonRasterizer() {
		throw new UnsupportedOperationException();
	}

	public static Result rasterize(List<Point2D> polygon) {
		double minXd = Integer.MAX_VALUE,
			maxXd = Integer.MIN_VALUE,
			minYd = Integer.MAX_VALUE,
			maxYd = Integer.MIN_VALUE;
		for (Point2D vertex : polygon) {
			if (minXd > vertex.x) {
				minXd = vertex.x;
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
		int minX = (int) minXd;
		int maxX = (int) maxXd;
		int minY = (int) minYd;
		int maxY = (int) maxYd;
		int polygonSize = polygon.size();
		Segment2D[] edges = new Segment2D[polygonSize];
		for (int i = 0; i < polygonSize; i++) {
			edges[i] = new Segment2D(
				polygon.get(i),
				polygon.get(i + 1 == polygonSize ? 0 : i + 1)
			);
		}
		// Indices x and y in this array are swapped
		// so Arrays.fill bellow will operate on continuous ranges.
		boolean[][] bitmap = new boolean[maxY - minY + 1][maxX - minX + 1];
		for (int y = minY; y <= maxY; y++) {
			TDoubleList intersections = new TDoubleLinkedList();
			for (int i = 0; i < edges.length; i++) {
				Segment2D edge = edges[i];
				if (edge.start.y < y && edge.end.y > y || edge.start.y > y && edge.end.y < y) {
					intersections.add(edge.toLine().intersectionWith(new Line2D(0, y, 1, y)).x);
				} else if (edge.start.y == y) {
					intersections.add(edge.start.x);
					if (areEdgesFromSameSide(
						edges[i - 1 < 0 ? edges.length - 1 : i - 1].start,
						edge.start,
						edge.end)) {

						intersections.add(edge.start.x);
					}
				}
				// Case for edge.start.y is omitted (though it may be present instead of the edge.start.x case)
				// because otherwise it would produce 2 equal x-values when testing neighbor edges.

			}
			intersections.sort();
			assert intersections.size() % 2 == 0;
			System.out.println(maxX - minX + 1);
			for (int i = 0; i < intersections.size(); i += 2) {
				Arrays.fill(
					bitmap[y - minY],
					(int) intersections.get(i) - minX,
					(int) intersections.get(i + 1) - minX,
					true
				);
			}
		}
		return new Result(minX, minY, bitmap);
	}

	/**
	 * Checks if both ends lie above {@code middle}, or that they both lie below {@code middle}.
	 *
	 * @param oneEnd
	 * @param middle
	 * @param anotherEnd
	 * @return
	 */
	private static boolean areEdgesFromSameSide(Point2D oneEnd, Point2D middle, Point2D anotherEnd) {
		return Math.signum(middle.y - oneEnd.y) == Math.signum(middle.y - anotherEnd.y);
	}

	public static class Result {
		public final int x;
		public final int y;
		public final int width;
		public final int height;
		private final boolean[][] bitmap;

		Result(int x, int y, boolean[][] bitmap) {
			this.x = x;
			this.y = y;
			this.width = bitmap[0].length;
			this.height = bitmap.length;
			this.bitmap = bitmap;
		}

		public boolean get(int x, int y) {
			return bitmap[y][x];
		}

		public BoundedCellSet toCellSet() {
			return new BoundedCellSet() {
				@Override
				public Rectangle getBounds() {
					return new Rectangle(x, y, width, height);
				}

				@Override
				public boolean contains(int x, int y) {
					return bitmap[y - Result.this.y][x - Result.this.x];
				}
			};
		}


	}
}
