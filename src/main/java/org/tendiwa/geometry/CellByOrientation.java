package org.tendiwa.geometry;

import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Cell;

final class CellByOrientation extends Cell_Wr {
	/**
	 * Creates a new Cell relative to this point.
	 *
	 * @param dStatic
	 * 	Shift by static axis.
	 * @param dDynamic
	 * 	Shift by dynamic axis.
	 * @param orientation
	 * 	Orientation that determines which axis is dynamic or static.
	 * @return New Cell.
	 * @see BasicCell For explanation of what static and dynamic axes are.
	 */
	public CellByOrientation(Cell cell, int dStatic, int dDynamic, Orientation orientation) {
		super(
			orientation.isHorizontal() ? cell.x() + dDynamic : cell.x() + dStatic,
			orientation.isHorizontal() ? cell.y() + dStatic : cell.y() + dDynamic
		);
	}
}
