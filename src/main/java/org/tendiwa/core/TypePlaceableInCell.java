package org.tendiwa.core;

import org.tendiwa.geometry.BasicCell;

/**
 * Marker interface for classes that represents types of entities that can be placed in a cell.
 */
public interface TypePlaceableInCell {
	default void getPlaced(HorizontalPlane plane, BasicCell cell) {
	}
}
