package org.tendiwa.geometry;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;

public interface ArrayBackedCellSet extends BoundedCellSet {
	boolean arrayElement(int arrayX, int arrayY);

	/**
	 * Finds a large enough axis-parallel rectangle in a 2d array of obstacles.
	 *
	 * @return An Optional with the computed Rectangle with {@link org.tendiwa.geometry.Rectangle#x} and {@link org
	 * .tendiwa.geometry.Rectangle#y} relative to {@code cells}' top-left corner,
	 * or an {@link java.util.Optional#empty()} if there are no
	 * cells in {@code cells} free of obstacles (for instance, when {@code cells} is a 0-length array).
	 * <p>
	 * For the computed Rectangle {@code rectangle.area() <= maximumArea}.
	 * <p>
	 * Note that, since {@code cells} has width and height but no defined top-left corner, the
	 * @throws java.lang.IllegalArgumentException
	 * 	if {@code maximumArea < 0}.
	 * @see org.tendiwa.geometry.extensions.polygonRasterization.MutableRasterizedPolygon One way to produce the
	 * {@code cells} array.
	 * @see <a href="http://stackoverflow.com/a/20039017/1028367">Stackoverflow question</a>
	 * @see <a href="http://www.drdobbs.com/database/the-maximal-rectangle-problem/184410529">Article with the
	 * description of the algorithm</a>
	 */
	default Optional<Rectangle> maximalRectangle() {
		Rectangle bounds = getBounds();
		int height = bounds.height();
		int[] cache = new int[height + 1];
		Deque<int[]> stack = new LinkedList<>();
		int[] best_ll = {0, 0};
		int[] best_ur = {-1, -1};
		int bestArea = 0;
		all:
		for (int x = bounds.width() - 1; x > -1; x--) {
			for (int y = 0; y < height; y++) {
				// Update cache
				if (arrayElement(x, y)) {
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
		return Optional.of(new BasicRectangle(
			best_ll[0],
			best_ll[1],
			best_ur[0] - best_ll[0] + 1,
			best_ur[1] - best_ll[1] + 1
		));
	}
}
