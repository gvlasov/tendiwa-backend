package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.geometry.Rectangle;


public abstract class Architecture {
	public abstract void draw(BuildingFeatures features, CardinalDirection front);

	public abstract boolean fits(Rectangle rectangle);

	public abstract BuildingTag[] tags();
}
