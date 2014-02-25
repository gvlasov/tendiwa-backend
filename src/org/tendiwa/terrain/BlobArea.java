package org.tendiwa.terrain;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Rectangle;

import java.util.Iterator;

public class BlobArea<T extends CellParams> implements Iterable<Cell> {
private final Iterable<Cell> cells;
private Chunk<T>[][] chunks;
private final Rectangle maxBound;

public BlobArea(Rectangle maxBound, Iterable<Cell> cells, CellParamsFactory<T> factory) {
	this.maxBound = maxBound;
	this.cells = ImmutableList.copyOf(cells);
	createChunks(maxBound);
	for (Cell cell : cells) {
		int startX = cell.getX() - cell.getX() % getChunkSize();
		int startY = cell.getY() - cell.getY() % getChunkSize();
		touchChunk(startX, startY).put(cell.getX(), cell.getY(), factory);
	}
}

private void createChunks(Rectangle maxBound) {
	chunks = (Chunk<T>[][]) new Chunk
		[numberOfChunks(maxBound.getWidth())]
		[numberOfChunks(maxBound.getHeight())];
}

private int numberOfChunks(int cells) {
	return cells / getChunkSize() + (cells % getChunkSize() > 0 ? 1 : 0);
}

private Chunk<T> touchChunk(int startX, int startY) {
	int nChunkX = (startX - maxBound.getX()) / getChunkSize();
	int nChunkY = (startY - maxBound.getY()) / getChunkSize();
	Chunk<T> chunk = chunks[nChunkX][nChunkY];
	if (chunk == null) {
		return chunks[nChunkX][nChunkY] = new Chunk<>(startX, startY, getChunkSize());
	}
	return chunk;
}

public void put(int x, int y, CellParamsFactory<T> factory) {
	chunks
		[(x - maxBound.getX()) / getChunkSize()]
		[(y - maxBound.getY()) / getChunkSize()]
		.put(x, y, factory);
}

private int getChunkSize() {
	return 32;
}

@Override
public Iterator<Cell> iterator() {
	return cells.iterator();
}

public T get(Cell cell) {
	return getChunkWithCell(cell).getCell(cell.getX(), cell.getY());
}

public Chunk<T> getChunkWithCell(Cell cell) {
	int chunkX = (cell.getX() - cell.getX() % getChunkSize()) / getChunkSize();
	int chunkY = (cell.getY() - cell.getY() % getChunkSize()) / getChunkSize();
	return chunks[chunkX][chunkY];
}
}
