package org.tendiwa.geometry;

import org.tendiwa.core.meta.Cell;

import java.util.Iterator;

public abstract class FiniteCellSet_Wr implements FiniteCellSet {
	private final FiniteCellSet cells;

	public FiniteCellSet_Wr(FiniteCellSet cells) {
		this.cells = cells;
	}

	@Override
	public boolean contains(int x, int y) {
		return cells.contains(x, y);
	}

	@Override
	public Iterator<Cell> iterator() {
		return cells.iterator();
	}
}
