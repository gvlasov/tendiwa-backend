package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.collections.IterableToStream;
import org.tendiwa.core.meta.Cell;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
	Rectangle getBounds();

	@Override
	default Iterator<Cell> iterator() {
		return new Iterator<Cell>() {
			private int n = -1;
			private final int maxN = getBounds().width() * getBounds().height();
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
					x = (n % getBounds().width()) + getBounds().x();
					y = (n / getBounds().width()) + getBounds().y();
				}
				while (n < maxN && !contains(x, y));
				if (n < maxN) {
					next = new BasicCell(x, y);
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

	default Stream<Cell> stream() {
		return IterableToStream.stream(iterator());
	}

	/**
	 * Creates a new ImmutableSet containing all of cells within {@link #getBounds()} that are buffer border cells.
	 *
	 * @return A new ImmutableSet containing all of cells within {@link #getBounds()} that are buffer border cells.
	 */
	default ImmutableSet<Cell> toSet() {
		ImmutableSet.Builder<Cell> builder = ImmutableSet.builder();
		int width = getBounds().width();
		int height = getBounds().height();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (contains(i + getBounds().x(), j + getBounds().y())) {
					builder.add(new BasicCell(i + getBounds().x(), j + getBounds().y()));
				}
			}
		}
		return builder.build();
	}

	@Override
	default void forEach(Consumer<? super Cell> action) {
		int startX = getBounds().x();
		int startY = getBounds().y();
		int maxX = getBounds().maxX() + 1;
		int maxY = getBounds().maxY() + 1;
		for (int x = startX; x < maxX; x++) {
			for (int y = startY; y < maxY; y++) {
				if (contains(x, y)) {
					action.accept(new BasicCell(x, y));
				}
			}
		}
	}

	@Override
	default BoundedCellSet without(CellSet set) {
		return new BoundedCellSet() {
			@Override
			public Rectangle getBounds() {
				return getBounds();
			}

			@Override
			public boolean contains(int x, int y) {
				return BoundedCellSet.this.contains(x, y) && !set.contains(x, y);
			}
		};
	}

}
