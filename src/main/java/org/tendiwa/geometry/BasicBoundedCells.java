package org.tendiwa.geometry;

public final class BasicBoundedCells implements BoundedCellSet {
	private final CellSet cells;
	private final Rectangle bounds;

	public BasicBoundedCells(
		CellSet cells,
		Rectangle bounds
	) {
		this.cells = cells;
		this.bounds = bounds;
	}

	public BasicBoundedCells(Rectangle rectangle) {
		this((x, y) -> true, rectangle);
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public boolean contains(int x, int y) {
		return cells.contains(x, y);
	}
}
