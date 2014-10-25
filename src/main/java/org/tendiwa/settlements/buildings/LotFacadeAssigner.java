package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.settlements.RectangleWithNeighbors;

@FunctionalInterface
public interface LotFacadeAssigner {
	public CardinalDirection getFacadeDirection(RectangleWithNeighbors lot);
}
