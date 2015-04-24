package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.core.meta.Cell;

import java.util.Iterator;
import java.util.Set;

import static java.util.Objects.*;

/**
 * An CellSet that holds a finite number of {@link Cell}s.
 * <p>
 * This implementation of {@link CellSet} is better memory-wise than {@link org.tendiwa.geometry.extensions
 * .CachedCellSet} when a bounding rectangle of all the cells would cover much more cells than there are in the set.
 * Particularly, this implementation uses {@code O(n)} where n is the number of cells in a set, when CachedCellSet uses
 * {@code O(w*h)} memory where {@code w} and {@code h} are width and height of the bounding rectangle.
 *
 * @see org.tendiwa.geometry.ScatteredMutableCellSet
 */
public final class ScatteredCellSet implements FiniteCellSet {
	// cells is not of type ImmutableSet to be mutated in ScatteredMutableCellSet subclass.
	Set<Cell> cells;

	public ScatteredCellSet(ImmutableSet<Cell> cells) {
		this.cells = requireNonNull(cells);
	}

	private ScatteredCellSet() {
		throw new UnsupportedOperationException();
	}

	public ScatteredCellSet(CellSet infiniteSet, Rectangle bounds) {
		requireNonNull(infiniteSet);
		requireNonNull(bounds);
		ImmutableSet.Builder<Cell> builder = ImmutableSet.builder();
		for (int i = 0; i < bounds.width(); i++) {
			for (int j = 0; j < bounds.height(); j++) {
				if (infiniteSet.contains(bounds.x() + i, bounds.y() + j)) {
					builder.add(new BasicCell(bounds.x() + i, bounds.y() + j));
				}
			}
		}
		cells = builder.build();
	}

	public ScatteredCellSet(Iterable<Cell> cells) {
		ImmutableSet.Builder<Cell> builder = ImmutableSet.builder();
		builder.addAll(cells);
		this.cells = builder.build();
	}


	@Override
	public Iterator<Cell> iterator() {
		return cells.iterator();
	}

	@Override
	public boolean contains(Cell cell) {
		return cells.contains(cell);
	}

	@Override
	public boolean contains(int x, int y) {
		return contains(new BasicCell(x, y));
	}

	@Override
	public ImmutableSet<Cell> toSet() {
		return (ImmutableSet<Cell>) cells;
	}
}
