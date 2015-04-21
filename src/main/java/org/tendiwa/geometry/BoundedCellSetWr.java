package org.tendiwa.geometry;

public abstract class BoundedCellSetWr implements BoundedCellSet {
	private final BoundedCellSet cells;

	public BoundedCellSetWr(BoundedCellSet cells) {
		this.cells = cells;
	}

	@Override
	public Rectangle getBounds() {
		return cells.getBounds();
	}

	@Override
	public boolean contains(int x, int y) {
		return cells.contains(x, y);
	}
}
