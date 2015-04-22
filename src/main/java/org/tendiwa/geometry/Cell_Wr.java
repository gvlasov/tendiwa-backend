package org.tendiwa.geometry;

import org.tendiwa.core.meta.Cell;

public abstract class Cell_Wr implements Cell {
	private final int x;
	private final int y;

	@Override
	public int x() {
		return x;
	}

	@Override
	public int y() {
		return y;
	}

	protected Cell_Wr(int x, int y) {
		this.x = x;
		this.y = y;
	}

	protected Cell_Wr(Cell cell) {
		this.x = cell.x();
		this.y = cell.y();
	}
}
