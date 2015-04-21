package org.tendiwa.geometry.extensions;

import org.tendiwa.geometry.CellSet;

public class ChebyshovDistanceBuffer implements CellSet {
	private final int distance;
	private final CellSet innerCells;

	public ChebyshovDistanceBuffer(int distance, CellSet innerCells) {
		this.distance = distance;
		this.innerCells = innerCells;
	}

	@Override
	public boolean contains(int x, int y) {
		if (innerCells.contains(x, y)) {
			return false;
		}
		// Check if a square of all cells with
		// Chebyshev distance to x:y<distance doesn't contain outer cells
		int maxX = x + distance;
		int maxY = y + distance;
		for (int i = x - distance; i <= maxX; i++) {
			for (int j = y - distance; j <= maxY; j++) {
				if (innerCells.contains(i, j)) {
					return true;
				}
			}
		}
		return false;
	}
}
