package org.tendiwa.terrain;

class Chunk<T extends CellParams> {
	private final int size;
	private final T[][] cells;
	private final int startX;
	private final int startY;

	public Chunk(int startX, int startY, int size) {
		this.startX = startX;
		this.startY = startY;
		this.size = size;
		cells = (T[][]) new CellParams[size][size];
	}

	public T getCell(int x, int y) {
		return cells[x - startX][y - startY];
	}

	public void put(int x, int y, CellParamsFactory<T> factory) {
		cells[x - startX][y - startY] = factory.create(x, y);
	}
}
