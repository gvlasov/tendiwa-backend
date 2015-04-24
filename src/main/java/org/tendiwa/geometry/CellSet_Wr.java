package org.tendiwa.geometry;

public abstract class CellSet_Wr implements CellSet {
	private final CellSet cells;

	public CellSet_Wr(CellSet cells) {
		this.cells = cells;
	}

	@Override
	public boolean contains(int x, int y) {
		return cells.contains(x, y);
	}
}
