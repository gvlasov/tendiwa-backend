package org.tendiwa.settlements.cityBounds;

import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingCellSet;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellSet;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds cells around "knots".
 * <p>
 * Knots are cells in a {@link CellSet} that are present in that set and have 3 or 4 of its cardinal neighbors also
 * present in the {@link CellSet}.
 */
final class KnotResolver {

	private final CachedCellSet bufferBorder;
	private final CellSet insideBufferBorder;
	private final ScatteredMutableCellSet cells;
	private final ScatteredMutableCellSet removedCells;
	private Set<Cell> currentWave;
	private Set<Cell> previousWave;
	private int cellsAdded;

	public KnotResolver(CachedCellSet bufferBorder, CellSet insideBufferBorder) {
		this.bufferBorder = bufferBorder;
		this.insideBufferBorder = insideBufferBorder;
		this.cells = new ScatteredMutableCellSet();
		this.removedCells = new ScatteredMutableCellSet();
	}

	CellSet cellsAroundKnots() {
		Rectangle bounds = bufferBorder.getBounds();
		currentWave = new HashSet<>();
		previousWave = new HashSet<>();
		cellsAdded = 0;
		for (int i = 0; i < bounds.width; i++) {
			for (int j = 0; j < bounds.height; j++) {
				int x = bounds.x + i;
				int y = bounds.y + j;
				rememberSurroundingCellsIfItIsKnot(new Cell(x, y));
			}
		}
		saveCells(currentWave);
		System.out.println("Cells added: " + cellsAdded);
		while (!currentWave.isEmpty()) {
			previousWave = currentWave;
			currentWave = new HashSet<>();
			cellsAdded = 0;
			previousWave.forEach(this::rememberSurroundingCellsIfItIsKnot);
			saveCells(currentWave);
			System.out.println("Cells added: " + cellsAdded);
		}
		return cells;
	}

	private void saveCells(Set<Cell> wave) {
		wave.forEach(cells::add);
	}


	/**
	 * If {@code cell} is a knot point, saves surrounding cells to {@link #cells}.
	 *
	 * @param cell
	 */
	private void rememberSurroundingCellsIfItIsKnot(Cell cell) {
		int x = cell.x;
		int y = cell.y;
		if ((bufferBorder.contains(x, y) || cells.contains(cell)) && !removedCells.contains(cell)) {
			int n = isCellToBeAdded(x, y - 1);
			int e = isCellToBeAdded(x + 1, y);
			int s = isCellToBeAdded(x, y + 1);
			int w = isCellToBeAdded(x - 1, y);
			if (n + e + s + w > 2) {
				addToRemovedCells(cell);
				addCell(x, y - 1);
				addCell(x + 1, y);
				addCell(x, y + 1);
				addCell(x - 1, y);
				addCell(x + 1, y + 1);
				addCell(x + 1, y - 1);
				addCell(x - 1, y + 1);
				addCell(x - 1, y - 1);
			}
		}
	}

	private void addToRemovedCells(Cell cell) {
		removedCells.add(cell);
	}

	private int isCellToBeAdded(int x, int y) {
		return (bufferBorder.contains(x, y) || cells.contains(x, y)) ? 1 : 0;
	}

	private void addCell(int x, int y) {
		Cell cell = new Cell(x, y);
		if (!insideBufferBorder.contains(cell) || cells.contains(cell)) {
			return;
		}
		currentWave.add(cell);
		cellsAdded++;
	}
}
