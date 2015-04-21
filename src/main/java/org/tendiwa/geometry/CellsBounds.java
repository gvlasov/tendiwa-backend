package org.tendiwa.geometry;

import org.tendiwa.core.meta.Cell;

public final class CellsBounds extends Rectangle_Wr {
	public CellsBounds(Iterable<BasicCell> cells) {
		super(bounds(cells));
	}

	private static Rectangle bounds(Iterable<BasicCell> cells) {
		int xMin = Integer.MAX_VALUE;
		int xMax = Integer.MIN_VALUE;
		int yMin = Integer.MAX_VALUE;
		int yMax = Integer.MIN_VALUE;
		for (Cell cell : cells) {
			if (cell.x() < xMin) {
				xMin = cell.x();
			}
			if (cell.x() > xMax) {
				xMax = cell.x();
			}
			if (cell.y() < yMin) {
				yMin = cell.y();
			}
			if (cell.y() > yMax) {
				yMax = cell.y();
			}
		}
		return new BasicRectangle(
			xMin,
			yMin,
			xMax - xMin + 1,
			yMax - yMin + 1
		);
	}
}
