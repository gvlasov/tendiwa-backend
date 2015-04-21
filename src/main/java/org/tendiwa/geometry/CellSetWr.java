package org.tendiwa.geometry;

public abstract class CellSetWr implements CellSet {
	private final CellSet cells;

	public CellSetWr(CellSet cells) {
		this.cells = cells;
	}

	@Override
	public boolean contains(int x, int y) {
		return cells.contains(x, y);
	}
}
