package org.tendiwa.core;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.geometry.BasicCell;

import static org.tendiwa.geometry.GeometryPrimitives.cell;

/**
 * Marker interface for classes that represents types of entities that can be placed in a cell.
 */
public interface TypePlaceableInCell {
	default void getPlaced(HorizontalPlane plane, Cell cell) {
		throw new UnsupportedOperationException();
	}

	default void getPlaced(HorizontalPlane plane, int x, int y) {
		getPlaced(plane, cell(x, y));
	}
}
