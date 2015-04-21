package org.tendiwa.terrain;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.Rectangle;

import java.util.Iterator;

public class BlobArea<T extends CellParams> implements Iterable<BasicCell> {
	private final Iterable<BasicCell> cells;
	private Chunk<T>[][] chunks;
	private final Rectangle maxBound;

	public BlobArea(Rectangle maxBound, Iterable<BasicCell> cells, CellParamsFactory<T> factory) {
		this.maxBound = maxBound;
		this.cells = ImmutableList.copyOf(cells);
		createChunks(maxBound);
		for (BasicCell cell : cells) {
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
	public Iterator<BasicCell> iterator() {
		return cells.iterator();
	}

	public T get(BasicCell cell) {
		return getChunkWithCell(cell).getCell(cell.x(), cell.y());
	}

	public Chunk<T> getChunkWithCell(BasicCell cell) {
		int chunkX = (cell.x() - cell.x() % getChunkSize()) / getChunkSize();
		int chunkY = (cell.y() - cell.y() % getChunkSize()) / getChunkSize();
		return chunks[chunkX][chunkY];
	}
}
