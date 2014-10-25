package org.tendiwa.geometry;

/**
 * A cell set that you can write to.
 */
public interface MutableCellSet extends FiniteCellSet {
	public void add(int x, int y);

	public default void add(Cell cell) {
		add(cell.x, cell.y);
	}

	public default void addAll(FiniteCellSet cells) {
		for (Cell cell : cells) {
			add(cell);
		}
	}

	public default void remove(Cell cell) {
		remove(cell.x, cell.y);
	}

	public void remove(int x, int y);
}
