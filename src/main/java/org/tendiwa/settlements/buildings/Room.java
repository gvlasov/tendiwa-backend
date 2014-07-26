package org.tendiwa.settlements.buildings;

import org.tendiwa.geometry.CellSet;

public class Room {
	private Character owner;
	private final CellSet shape;

	public Room(CellSet shape) {
		this.shape = shape;
	}

	public void setOwner(Character owner) {
		this.owner = owner;
	}
}
