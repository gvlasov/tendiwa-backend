package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.Placeable;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.lexeme.Localizable;

import java.util.*;

public final class City {
	private final Localizable name;
	final Map<Rectangle, Building> buildings;
	final Set<Street> streets;
	final Set<Placeable> districts;

	City(Localizable name, Map<Rectangle, Building> buildings, Set<Street> streets, Set<Placeable> districts) {
		this.name = name;
		this.buildings = buildings;
		this.streets = streets;
		this.districts = districts;
	}


	/**
	 * Checks if a place is already occupied by some building.
	 *
	 * @param rectangle
	 * 	A place.
	 * @return true if it is occupied, false if it is free or if it is not even a valid building place for this City.
	 */
	boolean isOccupied(Rectangle rectangle) {
		return buildings.get(rectangle) != null;
	}

}

