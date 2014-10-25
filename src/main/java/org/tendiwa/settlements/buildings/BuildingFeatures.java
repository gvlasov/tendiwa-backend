package org.tendiwa.settlements.buildings;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.Character;
import org.tendiwa.geometry.CellSet;
import org.tendiwa.settlements.RectangleWithNeighbors;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class BuildingFeatures {
	private final List<Room> rooms = new LinkedList<>();
	private Character owner;
	private RectangleWithNeighbors lot;
	private String localizationId;
	private CardinalDirection front;

	public void setPlace(RectangleWithNeighbors rectangle) {
		Objects.requireNonNull(rectangle);
		this.lot = rectangle;
	}

	public void addRoom(CellSet room) {
		rooms.add(new Room(room));
	}

	public void setOwner(Character owner) {
		Objects.requireNonNull(owner);
		this.owner = owner;
	}

	public void setFront(CardinalDirection front) {
		Objects.requireNonNull(front);
		this.front = front;
	}

	public void setLocalizationId(String localizationId) {
		this.localizationId = localizationId;
	}

	public Building build() {
		return new Building(lot, rooms, owner, front, localizationId);
	}

}
