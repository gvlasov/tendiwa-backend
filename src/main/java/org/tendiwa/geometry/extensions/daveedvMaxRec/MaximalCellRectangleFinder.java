package org.tendiwa.geometry.extensions.daveedvMaxRec;


import org.tendiwa.geometry.Rectangle;

import java.util.Deque;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MaximalCellRectangleFinder {
	private MaximalCellRectangleFinder() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Finds the largest area axis-parallel rectangle in a 2d array of obstacles.
	 *
	 * @param cells
	 * 	Array of obstacles. {@code false} means an obstacle, {@code true} means no obstacle. First index is
	 * 	y-coordinate, second index is x-coordinate.
	 * @return An Optional with the computed Rectangle, or an {@link java.util.Optional#empty()} if there are no
	 * cells in {@code cells} free of obstacles (for instance, when {@code cells} is a 0-length array).
	 * @see <a href="http://stackoverflow.com/a/20039017/1028367">Stackoverflow question</a>
	 * @see <a href="http://www.drdobbs.com/database/the-maximal-rectangle-problem/184410529">Article with the
	 * description of the algorithm</a>
	 */
	public static Optional<Rectangle> compute(boolean[][] cells) {
		if (cells.length == 0) {
			return Optional.empty();
		}
		int height = cells.length;
		int[] cache = new int[height + 1];
		Deque<int[]> stack = new LinkedList<>();
		int[] best_ll = {0, 0};
		int[] best_ur = {-1, -1};
		int bestArea = 0;
		for (int x = cells[0].length - 1; x > -1; x--) {
			for (int y = 0; y < height; y++) {
				// Update cache
				if (cells[y][x]) {
					cache[y]++;
				} else {
					cache[y] = 0;
				}
			}
			int width = 0;
			for (int y = 0; y < height + 1; y++) {
				if (cache[y] > width) {
					stack.push(new int[]{y, width});
					width = cache[y];
				} else if (cache[y] < width) {
					int y0, w0;
					do {
						int[] pop = stack.pop();
						y0 = pop[0];
						w0 = pop[1];
						int area = width * (y - y0);
						if (area > bestArea) {
							bestArea = area;
							best_ll[0] = x;
							best_ll[1] = y0;
							best_ur[0] = x + width - 1;
							best_ur[1] = y - 1;
						}
						width = w0;
					} while (cache[y] < width);
					width = cache[y];
					if (width != 0) {
						stack.push(new int[]{y0, w0});
					}
				}
			}
		}
		if (best_ur[0] == -1 && best_ur[1] == -1) {
			return Optional.empty();
		}
		return Optional.of(new Rectangle(
			best_ll[0],
			best_ll[1],
			best_ur[0] - best_ll[0] + 1,
			best_ur[1] - best_ll[1] + 1
		));
	}
}
