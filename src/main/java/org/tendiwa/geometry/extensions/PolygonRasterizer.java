package org.tendiwa.geometry.extensions;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.linked.TDoubleLinkedList;
import org.tendiwa.geometry.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
		int minX = (int) Math.ceil(minXd);
		int maxX = (int) Math.floor(maxXd);
		int minY = (int) Math.ceil(minYd);
		int maxY = (int) Math.floor(maxYd);
		int numberOfVertices = polygon.size();
		Segment2D[] edges = new Segment2D[numberOfVertices];
		for (int i = 0; i < numberOfVertices; i++) {
			edges[i] = new Segment2D(
				polygon.get(i),
				polygon.get(i + 1 == numberOfVertices ? 0 : i + 1)
			);
		}
		// Indices x and y in this array are swapped
		// so Arrays.fill bellow will operate on continuous ranges.
		boolean[][] bitmap = new boolean[maxY - minY + 1][maxX - minX + 1];
		boolean anythingWasRasterized = false;
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
			for (int i = 0; i < intersections.size(); i += 2) {
				Arrays.fill(
					bitmap[y - minY],
					(int) Math.ceil(intersections.get(i)) - minX,
					(int) Math.floor(intersections.get(i + 1)) - minX + 1,
					true
				);
				anythingWasRasterized = true;
			}
		}

		if (!anythingWasRasterized || bitmap[0].length == 0) {
			bitmap = new boolean[0][0];
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

	/**
	 * Represents the result of polygon rasterization: a grid within a rectangular area with cells that either belong
	 * to polygon's area or not.
	 * <p>
	 * This class is <b>mutable</b> for performance reasons.
	 */
	public static class Result {
		public final int x;
		public final int y;
		public final int width;
		public final int height;
		/**
		 * Array of obstacles. {@code false} means an obstacle, {@code true} means no obstacle. First index is
		 * y-coordinate, second index is x-coordinate.
		 * <p>
		 * Bitmap itself has to be public (and hence <b>mutable</b>),
		 * because otherwise we would need to defensively copy it each time the result is
		 * passed to another algorithm, which is time-consuming.
		 */
		public final boolean[][] bitmap;

		Result(int x, int y, boolean[][] bitmap) {
			this.x = x;
			this.y = y;
			this.height = bitmap.length;
			if (this.height == 0) {
				this.width = 0;
			} else {
				this.width = bitmap[0].length;
			}
			this.bitmap = bitmap;
		}

		public boolean get(int x, int y) {
			return bitmap[y][x];
		}

		/**
		 * Fills a rectangular area within {@link #bitmap} with obstacle cells. Coordinates of filled cells are
		 * relative to {@link #x} and {@link #y}.
		 *
		 * @param r
		 * 	A rectangle to fill.
		 */
		public void excludeRectangle(Rectangle r) {
			int startX = r.x - x;
			int endX = r.x + r.width - x;
			int endY = r.y - this.y + r.height;
			for (int row = r.y - this.y; row < endY; row++) {
				Arrays.fill(
					bitmap[row],
					startX,
					endX,
					false
				);
			}
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
