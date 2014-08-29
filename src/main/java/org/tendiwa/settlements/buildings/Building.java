package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Character;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.lexeme.Localizable;
import org.tendiwa.settlements.RectangleWithNeighbors;

import java.util.LinkedList;
import java.util.List;

public class Building {
	Character owner;
	final String name;
	final List<Room> rooms;
	final RectangleWithNeighbors place;
	final CardinalDirection front;

	Building(RectangleWithNeighbors place, List<Room> rooms, Character owner,
			 CardinalDirection front, String localizationId) {
		this.place = place;
		this.rooms = rooms;
		this.owner = owner;
		this.front = front;
		this.name = localizationId;
	}
}
