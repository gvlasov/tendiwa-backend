package org.tendiwa.settlements.buildings;

import org.tendiwa.core.Character;
import org.tendiwa.geometry.CellSet;
import org.tendiwa.geometry.Rectangle;

import java.util.LinkedList;
import java.util.List;

public class BuildingFeatures {
	private final List<Room> rooms = new LinkedList<>();
	private Character owner;
	private Street street;
	private Rectangle lot;

	void setBuildingLot(Rectangle rectangle) {
		this.lot = rectangle;
	}

	public void addRoom(CellSet room) {
		rooms.add(new Room(room));
	}

	public void setOwner(Character owner) {
		this.owner = owner;
	}

	public void setStreet(Street street) {
		this.street = street;
	}

	public Building build() {
		return new Building(lot, rooms, owner, street);
	}

}
