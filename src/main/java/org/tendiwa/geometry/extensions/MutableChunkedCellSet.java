package org.tendiwa.geometry.extensions;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.MutableCellSet;
import org.tendiwa.geometry.Rectangle;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Iterator;

public final class MutableChunkedCellSet implements MutableCellSet, BoundedCellSet {
	private final Rectangle bounds;
	private final int chunkSize;
	private final Chunk[] chunks;
	private final int chunksInRow;

	public MutableChunkedCellSet(Rectangle bounds, int chunkSize) {
		this.bounds = bounds;
		this.chunkSize = chunkSize;
		this.chunksInRow = getWholeNumberOfChunks(bounds.width, chunkSize);
		int numberOfChunks = chunksInRow * getWholeNumberOfChunks(bounds.height, chunkSize);
		this.chunks = new Chunk[numberOfChunks];
	}

	private int getWholeNumberOfChunks(int fullSize, int chunkSize) {
		return fullSize / chunkSize + (fullSize % chunkSize > 0 ? 1 : 0);
	}

	private Chunk lazilyGetChunkByCell(int x, int y) {
		int chunkIndex = (x - bounds.x) / chunkSize + (y - bounds.y) / chunkSize * chunksInRow;
		if (chunks[chunkIndex] == null) {
			chunks[chunkIndex] = new Chunk();
		}
		return chunks[chunkIndex];
	}

	@Override
	public void add(int x, int y) {
		lazilyGetChunkByCell(x, y).add((x - bounds.x) % chunkSize, (y - bounds.y) % chunkSize);
	}

	@Override
	public void add(BasicCell cell) {
		lazilyGetChunkByCell(cell.x, cell.y).add((cell.x - bounds.x) % chunkSize, (cell.y - bounds.y) % chunkSize);
	}

	@Override
	public void remove(int x, int y) {
		lazilyGetChunkByCell(x, y).remove((x - bounds.x) % chunkSize, (y - bounds.y) % chunkSize);
	}

	@Override
	public void remove(BasicCell cell) {
		lazilyGetChunkByCell(cell.x, cell.y).remove((cell.x - bounds.x) % chunkSize, (cell.y - bounds.y) % chunkSize);
	}

	@Override
	public ImmutableSet<BasicCell> toSet() {
		ImmutableSet.Builder<BasicCell> builder = ImmutableSet.builder();
		int chunksInColumn = getWholeNumberOfChunks(bounds.height, chunkSize);
		for (int chunkColumn = 0; chunkColumn < chunksInRow; chunkColumn++) {
			for (int chunkRow = 0; chunkRow < chunksInColumn; chunkRow++) {
				Chunk chunk = chunks[chunkColumn + chunkRow * chunksInRow];
				if (chunk != null) {
					for (int cellRow = 0; cellRow < chunkSize; cellRow++) {
						for (int cellColumn = 0; cellColumn < chunkSize; cellColumn++) {
							if (chunk.cells[cellRow][cellColumn]) {
								builder.add(
									new BasicCell(
										bounds.x + chunkColumn * chunkSize + cellRow,
										bounds.y + chunkRow * chunkSize + cellColumn
									)
								);
							}
						}
					}
				}
			}
		}
		return builder.build();
	}

	@Override
	public boolean contains(int x, int y) {
		return lazilyGetChunkByCell(x, y).contains((x - bounds.x) % chunkSize, (y - bounds.y) % chunkSize);
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public Iterator<BasicCell> iterator() {
		throw new NotImplementedException();
	}

	private class Chunk {
		private final boolean[][] cells;

		private Chunk() {
			cells = new boolean[chunkSize][chunkSize];
		}

		private void add(int x, int y) {
			cells[x][y] = true;
		}

		private void remove(int x, int y) {
			cells[x][y] = false;
		}

		private void toggle(int x, int y) {
			cells[x][y] = !cells[x][y];
		}

		private boolean contains(int x, int y) {
			return cells[x][y];
		}
	}
}
