package org.tendiwa.core.worlds;

import org.tendiwa.core.TypePlaceableInCell;
import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.Rectangle;

import java.util.function.Function;

public final class PlacementWrap implements Placement<TypePlaceableInCell> {

	private final BoundedCellSet cells;
	private final Function<Cell, TypePlaceableInCell> function;

	PlacementWrap(
		BoundedCellSet cells,
		Function<Cell, TypePlaceableInCell> function
	) {
		this.cells = cells;
		this.function = function;
	}

	@Override
	public TypePlaceableInCell contentAt(Cell cell) {
		return function.apply(cell);
	}

	@Override
	public Rectangle getBounds() {
		return cells.getBounds();
	}

	@Override
	public boolean contains(int x, int y) {
		return cells.contains(x, y);
	}
}
