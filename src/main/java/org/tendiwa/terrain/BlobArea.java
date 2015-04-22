package org.tendiwa.terrain;

import com.google.common.collect.ImmutableList;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.Rectangle;

import java.util.Iterator;

public final class BlobArea<T extends CellParams> implements Iterable<Cell> {
	private final Iterable<Cell> cells;
	private Chunk<T>[][] chunks;
	private final Rectangle maxBound;

	public BlobArea(
		Rectangle maxBound,
		Iterable<Cell> cells,
		CellParamsFactory<T> factory
	) {
		this.maxBound = maxBound;
		this.cells = ImmutableList.copyOf(cells);
		createChunks(maxBound);
		for (Cell cell : cells) {
			int startX = cell.x() - cell.x() % getChunkSize();
			int startY = cell.y() - cell.y() % getChunkSize();
			touchChunk(startX, startY).put(cell.x(), cell.y(), factory);
		}
	}

	private void createChunks(Rectangle maxBound) {
		chunks = (Chunk<T>[][]) new Chunk
			[numberOfChunks(maxBound.width())]
			[numberOfChunks(maxBound.height())];
	}

	private int numberOfChunks(int cells) {
		return cells / getChunkSize() + (cells % getChunkSize() > 0 ? 1 : 0);
	}

	private Chunk<T> touchChunk(int startX, int startY) {
		int nChunkX = (startX - maxBound.x()) / getChunkSize();
		int nChunkY = (startY - maxBound.y()) / getChunkSize();
		Chunk<T> chunk = chunks[nChunkX][nChunkY];
		if (chunk == null) {
			return chunks[nChunkX][nChunkY] = new Chunk<>(startX, startY, getChunkSize());
		}
		return chunk;
	}

	public void put(int x, int y, CellParamsFactory<T> factory) {
		chunks
			[(x - maxBound.x()) / getChunkSize()]
			[(y - maxBound.y()) / getChunkSize()]
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
		return getChunkWithCell(cell).getCell(cell.x(), cell.y());
	}

	public Chunk<T> getChunkWithCell(Cell cell) {
		int chunkX = (cell.x() - cell.x() % getChunkSize()) / getChunkSize();
		int chunkY = (cell.y() - cell.y() % getChunkSize()) / getChunkSize();
		return chunks[chunkX][chunkY];
	}
}
