package org.tendiwa.geometry.extensions;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TDoubleLinkedList;
import gnu.trove.list.linked.TIntLinkedList;
import org.tendiwa.geometry.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class PolygonRasterizer {
	private PolygonRasterizer() {
		throw new UnsupportedOperationException();
	}

	private static ResultData rasterize(List<Point2D> polygon) {
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
			TIntList intersections = new TIntLinkedList();
			for (int i = 0; i < numberOfVertices; i++) {
				Segment2D edge = edges[i];
				Point2D vertex = polygon.get(i);
				Point2D nextVertex = polygon.get(i + 1 == numberOfVertices ? 0 : i + 1);
				if (vertex.y < y && nextVertex.y > y || vertex.y > y && nextVertex.y < y) {
					intersections.add(i);
					// edge.toLine().intersectionWith(new Line2D(0, y, 1, y)).x
				} else if (vertex.y == y) {
					intersections.add(i);
//					if (
//						areEdgesFromSameSide(
//							edges[i - 1 < 0 ? edges.length - 1 : i - 1].start,
//							edge.start,
//							edge.end
//						)
//						) {
//						// Add the same coordinate one more time
//						intersections.add(edge.start.x);
//					} else if (edge.end.y == y) {
//						double previousVertexY = polygon.get(i-1).y;
//						int startIndex= i;
//						i+=1;
//						while (i<numberOfVertices && polygon.get(i+1).y == y) {
//							i++;
//						}
//						int endIndex = i;
//						double nextVertexY = polygon.get(i+1).y;
//						if (previousVertexY > y && nextVertexY < y || previousVertexY < y && nextVertexY > y) {
//
//						}
//						intersections.add(polygon.get(startIndex).x);
//						intersections.add(polygon.get(endIndex).x);
//
//					}
				}
				// Case for edge.start.y is omitted (though it may be present instead of the edge.start.x case)
				// because otherwise it would produce 2 equal x-values when testing neighbor edges.
			}
			int[] array = intersections.toArray();
//			Arrays.sort(array, (a, b) -> Double.compare(polygon.get(a).x, polygon.get(b).x));
			assert intersections.size() % 2 == 0 : intersections.size() + ", y=" + y;
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
		return new ResultData(minX, minY, bitmap);
	}

	public static BoundedCellSet rasterizeToCellSet(List<Point2D> polygon) {
		ResultData data = rasterize(polygon);
		return new Result(data.minX, data.minY, data.bitmap);
	}

	public static MutableResult rasterizeToMutable(List<Point2D> polygon) {
		ResultData data = rasterize(polygon);
		return new MutableResult(data.minX, data.minY, data.bitmap);
	}

	private final static class ResultData {
		final int minX;
		final int minY;
		final boolean[][] bitmap;

		public ResultData(int minX, int minY, boolean[][] bitmap) {
			this.minX = minX;
			this.minY = minY;
			this.bitmap = bitmap;
		}
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

	public static class MutableResult {
		public final int x;
		public final int y;
		public final int width;
		public final int height;
		public final boolean[][] bitmap;

		MutableResult(int minX, int minY, boolean[][] bitmap) {
			this.x = minX;
			this.y = minY;
			this.width = (bitmap.length == 0) ? 0 : bitmap[0].length;
			this.height = bitmap.length;
			this.bitmap = bitmap;
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
	}

	/**
	 * Represents the result of polygon rasterization: a grid within a rectangular area with cells that either belong
	 * to polygon's area or not.
	 * <p>
	 * This class is <b>mutable</b> for performance reasons.
	 */
	public static class Result implements BoundedCellSet {
		private final Rectangle bounds;
		/**
		 * Array of obstacles. {@code false} means an obstacle, {@code true} means no obstacle. First index is
		 * y-coordinate, second index is x-coordinate.
		 * <p>
		 * Bitmap itself has to be public (and hence <b>mutable</b>),
		 * because otherwise we would need to defensively copy it each time the result is
		 * passed to another algorithm, which is time-consuming.
		 */
		private final boolean[][] bitmap;

		Result(int x, int y, boolean[][] bitmap) {
			this.bounds = new Rectangle(
				x,
				y,
				(bitmap.length == 0) ? 0 : bitmap[0].length,
				bitmap.length
			);
			this.bitmap = bitmap;
		}

		public boolean get(int x, int y) {
			return bitmap[y][x];
		}


		@Override
		public boolean contains(int x, int y) {
			return bounds.contains(x, y) && bitmap[y - bounds.y][x - bounds.x];
		}

		@Override
		public void forEach(Consumer<? super Cell> action) {
			int maxX = bounds.getMaxX();
			int maxY = bounds.getMaxY();
			for (int i = bounds.x; i < maxX; i++) {
				for (int j = bounds.y; j < maxY; j++) {
					if (bitmap[j - bounds.y][i - bounds.x]) {
						action.accept(new Cell(i, j));
					}
				}
			}
		}

		@Override
		public void forEach(CellConsumer action) {
			int maxX = bounds.getMaxX();
			int maxY = bounds.getMaxY();
			for (int i = bounds.x; i < maxX; i++) {
				for (int j = bounds.y; j < maxY; j++) {
					if (bitmap[j - bounds.y][i - bounds.x]) {
						action.consume(i, j);
					}
				}
			}
		}

		@Override
		public Rectangle getBounds() {
			return bounds;
		}
	}
}
