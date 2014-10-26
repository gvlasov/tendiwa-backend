package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * A finite set of cells. All the cells reside within certain rectangle.
 * <p>
 * Note that it is still up to implementer whether to allow cells out of bounds to be present in the set or not.
 */
public interface BoundedCellSet extends FiniteCellSet {
	/**
	 * Returns a rectangle in which all cells of this CellSet reside. Note that the bound in not necessarily the least
	 * rectangular hull of all computed cells.
	 *
	 * @return Rectangular bounds of this CellSet.
	 */
	public Rectangle getBounds();

	/**
	 * Iterates over cells in this {@link CellSet}.
	 * <p>
	 * Most of the time it is better to use {@link org.tendiwa.geometry.BoundedCellSet#forEach(CellConsumer)}.
	 *
	 * @return
	 */
	@Override
	public default Iterator<Cell> iterator() {
		return new Iterator<Cell>() {
			private int n = -1;
			private final int maxN = getBounds().width * getBounds().height;
			private Cell next = findNext();


			@Override
			public boolean hasNext() {
				return next != null;
			}

			@Override
			public Cell next() {
				Cell answer = next;
				findNext();
				return answer;
			}

			private Cell findNext() {
				int x, y;
				do {
					n++;
					x = (n % getBounds().width) + getBounds().x;
					y = (n / getBounds().width) + getBounds().y;
				}
				while (n < maxN && !contains(x, y));
				if (n < maxN) {
					next = new Cell(x, y);
				} else {
					next = null;
				}
				return next;
			}


			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Creates a new ImmutableSet containing all of cells within {@link #getBounds()} that are buffer border cells.
	 *
	 * @return A new ImmutableSet containing all of cells within {@link #getBounds()} that are buffer border cells.
	 */
	public default ImmutableSet<Cell> toSet() {
		ImmutableSet.Builder<Cell> builder = ImmutableSet.builder();
		int width = getBounds().getWidth();
		int height = getBounds().getHeight();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (contains(i + getBounds().x, j + getBounds().y)) {
					builder.add(new Cell(i + getBounds().x, j + getBounds().y));
				}
			}
		}
		return builder.build();
	}

	@Override
	public default void forEach(Consumer<? super Cell> action) {
		int startX = getBounds().x;
		int startY = getBounds().y;
		int maxX = getBounds().getMaxX()+1;
		int maxY = getBounds().getMaxY()+1;
		for (int x = startX; x < maxX; x++) {
			for (int y = startY; y < maxY; y++) {
				if (contains(x, y)) {
					action.accept(new Cell(x, y));
				}
			}
		}
	}

	@Override
	public default void forEach(CellConsumer action) {
		int startX = getBounds().x;
		int startY = getBounds().y;
		int maxX = getBounds().getMaxX()+1;
		int maxY = getBounds().getMaxY()+1;
		for (int x = startX; x < maxX; x++) {
			for (int y = startY; y < maxY; y++) {
				if (contains(x, y)) {
					action.consume(x, y);
				}
			}
		}
	}
}
