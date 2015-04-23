package org.tendiwa.demos.settlements;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.FiniteCellSet;

import java.util.Iterator;

public abstract class FiniteCellSet_Wr implements FiniteCellSet {

	private final FiniteCellSet cells;

	protected FiniteCellSet_Wr(FiniteCellSet cells) {
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