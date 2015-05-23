package org.tendiwa.geometry;

import org.tendiwa.core.meta.Cell;

/**
 * A cell set that you can write to.
 */
public interface MutableCellSet extends FiniteCellSet {
	void add(int x, int y);

	default void add(Cell cell) {
		add(cell.x(), cell.y());
	}

	default void addAll(FiniteCellSet cells) {
		for (Cell cell : cells) {
			add(cell);
		}
	}

	default void remove(Cell cell) {
		remove(cell.x(), cell.y());
	}

	void remove(int x, int y);
}
