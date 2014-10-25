package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Character;
import org.tendiwa.settlements.RectangleWithNeighbors;

import java.util.List;

public class Building {
	final String name;
	final List<Room> rooms;
	final RectangleWithNeighbors place;
	final CardinalDirection front;
	Character owner;

	Building(RectangleWithNeighbors place, List<Room> rooms, Character owner,
			 CardinalDirection front, String localizationId) {
		this.place = place;
		this.rooms = rooms;
		this.owner = owner;
		this.front = front;
		this.name = localizationId;
	}
}
