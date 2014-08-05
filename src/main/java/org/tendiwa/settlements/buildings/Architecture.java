package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Location;
import org.tendiwa.geometry.Rectangle;


/**
 * Defines a way to construct new {@link Building}s.
 */
public interface Architecture {
	/**
	 * Creates a new building by both drawing it into a {@link org.tendiwa.core.Location} and describing its {@link
	 * org.tendiwa.settlements.buildings.BuildingFeatures}.
	 *
	 * @param features
	 * 	Empty BuildingFeatures to be modified. Provided by {@link org.tendiwa.settlements.buildings.UrbanPlanner}.
	 * @param front
	 * 	A direction the actual building is facing, so implementor won't need to operate on concrete
	 * 	CardinalDirection, but rather a direction relative to {@code front} so building can be rotated arbitrarily.
	 * 	Provided by {@link org.tendiwa.settlements.buildings.UrbanPlanner}.
	 * @param location
	 * 	Implementation should draw cells' contents here. Provided by {@link org.tendiwa.settlements.buildings
	 * 	.UrbanPlanner}.
	 */
	public void draw(BuildingFeatures features, CardinalDirection front, Location location);

	/**
	 * Checks if this Architecture may raise a building in a particular place.
	 *
	 * @param rectangle
	 * 	A place to raise buildings at.
	 * @return true if this place has right size for a building to be raised in it, false otherwise.
	 */
	public boolean fits(Rectangle rectangle);

	public Rectangle typicalBuildingPlace();

	/**
	 * Lists tags
	 *
	 * @return
	 */
	public BuildingTag[] tags();
}
