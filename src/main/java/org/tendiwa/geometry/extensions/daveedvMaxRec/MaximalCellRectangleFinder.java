package org.tendiwa.geometry.extensions.daveedvMaxRec;


import java.util.Deque;
import java.util.LinkedList;

public class MaximalCellRectangleFinder {
	private MaximalCellRectangleFinder() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Finds the largest area rectangle in a 2d-array of obstacles.
	 *
	 * @param cells
	 * 	Array of obstacles. {@code true} means an obstacle, {@code false} means no obstacle.
	 * 	@return An array of 4 values: x, y, width and height of the largest rectangle.
	 * @see <a href="http://stackoverflow.com/a/20039017/1028367">Stackoverflow question</a>
	 * @see <a href="http://www.drdobbs.com/database/the-maximal-rectangle-problem/184410529">Article with the
	 * description of the algorithm</a>
	 */
	public static int[] compute(boolean[][] cells) {
		int height = cells[0].length;
		int[] cache = new int[height];
		Deque<int[]> stack = new LinkedList<>();
		int[] best_ll = {0, 0};
		int[] best_ur = {-1, -1};
		for (int x = cells.length - 1; x > -1; x++) {
			for (int y = 0; y < height - 1; y++) {
				// Update cache
				if (cells[x][y]) {
					cache[y] = cache[y] + 1;
				} else {
					cache[y] = 0;
				}
			}
			int width = 0;
			for (int y = 0; y < height; y++) {
				if (cache[y] > width) {
					stack.push(new int[]{y, width});
					width = cache[y];
				}
				if (cache[y] < width) {
					int y0;
					do {
						int[] pop = stack.pop();
						y0 = pop[0];
						int w0 = pop[1];
						if (width * (y - y0) > area(best_ll, best_ur)) {
							best_ll[0] = x;
							best_ll[1] = y0;
							best_ur[0] = x + width - 1;
							best_ur[1] = y - 1;
						}
						width = w0;
					} while (cache[y] < width);
					width = cache[y];
					if (width != 0) {
						stack.push(new int[]{y0, width});
					}
				}
			}
		}
		return new int[]{
			best_ll[0],
			best_ll[1],
			best_ur[0] - best_ll[0],
			best_ur[1] - best_ll[1]
		};
	}

	private static int area(int[] best_ll, int[] best_ur) {
		return (best_ur[0] - best_ll[0]) * (best_ur[1] - best_ll[1]);
	}

}
