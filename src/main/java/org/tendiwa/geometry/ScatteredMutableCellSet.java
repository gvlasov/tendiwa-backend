package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.core.meta.Cell;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An editable CellSet that holds a finite number of {@link BasicCell}s.
 * <p>
 * This class wraps a {@link java.util.HashSet}, so complexity of operations and memory used are those of a
 * {@link java.util.HashSet}.
 */
public class ScatteredMutableCellSet implements MutableCellSet, FiniteCellSet {
	private final Set<Cell> cells;

	public ScatteredMutableCellSet() {
		cells = new HashSet<>();
	}

	@Override
	public void add(int x, int y) {
		if (!cells.add(new BasicCell(x, y))) {
			throw new IllegalArgumentException(
				"Cell " + x + ":" + y + " is already present in this set of " + cells.size() + " cells"
			);
		}
	}

	/**
	 * Adds a cell to this set.
	 *
	 * @param cell
	 * 	A cell to add to this set.
	 * @throws java.lang.IllegalArgumentException
	 * 	If the cell is already present in this set.
	 */
	@Override
	public void add(Cell cell) {
		if (!cells.add(cell)) {
			throw new IllegalArgumentException(
				"Cell " + cell + " is already present in this set of " + cells.size() + " cells"
			);
		}
	}

	/**
	 * Removes a set from this set.
	 *
	 * @param cell
	 * 	A cell to remove from this set.
	 * @throws java.lang.IllegalArgumentException
	 * 	If {@code cell} doesn't exist in this set.
	 */
	@Override
	public void remove(Cell cell) {
		if (!cells.remove(cell)) {
			throw new NoSuchElementException(
				"Can't remove cell " + cell + " because it doesn't exist in this set"
			);
		}
	}

	@Override
	public void remove(int x, int y) {
		if (!cells.remove(new BasicCell(x, y))) {
			throw new NoSuchElementException(
				"Can't remove cell " + x + ":" + y + " because it doesn't exist in this set"
			);
		}
	}

	@Override
	public boolean contains(int x, int y) {
		return cells.contains(new BasicCell(x, y));
	}

	@Override
	public Iterator<Cell> iterator() {
		return ImmutableSet.copyOf(cells).iterator();
	}
}
