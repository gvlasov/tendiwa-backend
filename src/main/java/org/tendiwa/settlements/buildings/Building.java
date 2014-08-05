package org.tendiwa.settlements.buildings;

import org.tendiwa.core.Character;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.lexeme.Localizable;

import java.util.LinkedList;
import java.util.List;

public class Building {
	Character owner;
	final String name;
	final List<Room> rooms;
	final Street street;
	final Rectangle place;

	public Building(Rectangle place, List<Room> rooms, Character owner, Street street, String localizationId) {
		this.place = place;
		this.rooms = rooms;
		this.owner = owner;
		this.street = street;
		this.name = localizationId;
	}
}
