package org.tendiwa.settlements.buildings;

import org.tendiwa.core.Character;
import org.tendiwa.geometry.Rectangle;

import java.util.LinkedList;
import java.util.List;

public class Building {
	Character owner;
	final List<Room> rooms = new LinkedList<>();
	final Street street;
	final Rectangle place;
}
