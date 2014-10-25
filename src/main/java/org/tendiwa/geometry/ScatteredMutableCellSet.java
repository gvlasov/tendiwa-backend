package org.tendiwa.geometry;

import java.util.HashSet;

/**
 * An editable CellSet that holds a finite number of {@link org.tendiwa.geometry.Cell}s.
 */
public class ScatteredMutableCellSet extends ScatteredCellSet implements MutableCellSet {
	public ScatteredMutableCellSet() {
		super();
		cells = new HashSet<>();
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
			throw new IllegalArgumentException(
				"Can't remove cell " + cell + " because it doesn't exist in this set"
			);
		}
	}
}
