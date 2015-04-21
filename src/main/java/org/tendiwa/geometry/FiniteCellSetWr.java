package org.tendiwa.geometry;

import java.util.Iterator;

public abstract class FiniteCellSetWr implements FiniteCellSet {
	private final FiniteCellSet cells;

	public FiniteCellSetWr(FiniteCellSet cells) {
		this.cells = cells;
	}

	@Override
	public boolean contains(int x, int y) {
		return cells.contains(x, y);
	}

	@Override
	public Iterator<BasicCell> iterator() {
		return cells.iterator();
	}
}
