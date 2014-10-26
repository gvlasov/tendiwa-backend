package org.tendiwa.geometry;

import java.util.HashSet;
import java.util.NoSuchElementException;

/**
 * An editable CellSet that holds a finite number of {@link org.tendiwa.geometry.Cell}s.
 * <p>
 * This class wraps a {@link java.util.HashSet}, so complexity of operations and memory used are those of a
 * {@link java.util.HashSet}.
 */
public class ScatteredMutableCellSet extends ScatteredCellSet implements MutableCellSet {
	public ScatteredMutableCellSet() {
		super();
		cells = new HashSet<>();
	}

	@Override
	public void add(int x, int y) {
		if (!cells.add(new Cell(x, y))) {
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
		if (!cells.remove(new Cell(x, y))) {
			throw new NoSuchElementException(
				"Can't remove cell " + x + ":" + y + " because it doesn't exist in this set"
			);
		}
	}
}
