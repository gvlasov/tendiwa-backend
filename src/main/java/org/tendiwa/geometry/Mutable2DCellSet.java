package org.tendiwa.geometry;

import org.tendiwa.core.meta.Cell;

import java.util.function.Consumer;

/**
 * An editable CellSet that holds a finite number of {@link Cell}s.
 * <p>
 * It is preferable to {@link ScatteredMutableCellSet} when cells in this set cover a significant part of their
 * bounding rectangle. Uses {@code O(bounds.width*bound.height)} memory.
 */
public class Mutable2DCellSet implements MutableCellSet, BoundedCellSet {

	private final Rectangle bounds;
	private final boolean[][] cells;

	/**
	 * @param bounds
	 * 	Bounds where you can write cells.
	 */
	public Mutable2DCellSet(Rectangle bounds) {
		this.bounds = bounds;
		cells = new boolean[bounds.width()][bounds.height()];
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Makes a cell present in this set.
	 * <p>
	 * You can remove already absent cells.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 	If {x:y} is not within bounds.
	 */
	@Override
	public void add(int x, int y) {
		boolean present = cells[x - bounds.x()][y - bounds.y()];
		if (present) {
			throw new IllegalArgumentException(
				"Can't add cell " + x + " " + y + " because it is already present in this set"
			);
		}
		cells[x - bounds.x()][y - bounds.y()] = true;
	}

	/**
	 * Adds a new cell to this cell set without checking if that cell is already present.
	 * <p>
	 * If the cell is already present, it remains present.
	 * <p>
	 * This method is much more effective than {@link #add(int, int)}.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 */
	public void addAnyway(int x, int y) {
		cells[x - bounds.x()][y - bounds.y()] = true;
	}

	@Override
	public void add(Cell cell) {
		add(cell.x(), cell.y());
	}

	/**
	 * Makes a cell absent in this set.
	 * <p>
	 * You can remove already absent cells.
	 *
	 * @param cell
	 * 	A cell to remove from this
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 	If {x:y} is not within bounds.
	 */
	@Override
	public void remove(Cell cell) {
		remove(cell.x(), cell.y());
	}

	/**
	 * Makes a cell absent in this set.
	 * <p>
	 * You can remove already absent cells.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 */
	@Override
	public void remove(int x, int y) {
		boolean present = cells[x - bounds.x()][y - bounds.y()];
		if (present) {
			throw new IllegalArgumentException(
				"Can't remove cell " + x + " " + y + " because it is not present in this set"
			);
		}
		cells[x - bounds.x()][y - bounds.y()] = false;
	}

	/**
	 * Makes a cell present in this set if it is not, makes it absent if it is.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 	If cell is not within {@link #getBounds()}.
	 */
	public void toggle(int x, int y) {
		cells[x - bounds.x()][y - bounds.y()] = !cells[x - bounds.x()][y - bounds.y()];
	}

	/**
	 * Checks if this set contains a particular cell.
	 *
	 * @param x
	 * 	X coordinate of a cell.
	 * @param y
	 * 	Y coordinate of a cell.
	 * @return true if this set contains a cell {x:y}, false otherwise.
	 */
	@Override
	public boolean contains(int x, int y) {
		return bounds.contains(x, y) && cells[x - bounds.x()][y - bounds.y()];
	}

	@Override
	public void forEach(Consumer<? super Cell> action) {
		for (int x = 0; x < bounds.width(); x++) {
			for (int y = 0; y < bounds.height(); y++) {
				if (cells[x][y]) {
					action.accept(new BasicCell(x + bounds.x(), y + bounds.y()));
				}
			}
		}
	}
}
