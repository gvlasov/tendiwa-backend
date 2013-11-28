package org.tendiwa.events;

import tendiwa.core.Character;

public class EventMove implements Event {
final Character character;
final int xPrev;
final int yPrev;

/**
 * Denotes a movement of a character (whether by walking or teleporting or any other means) from one cell to another.
 *
 * @param xPrev
 * 	X coordinate of a cell the Character was at, in world coordinates.
 * @param yPrev
 * 	Y coordinate of a cell the Character was at, in world coordinates.
 * @param character
 * 	The character that moves.
 */
public EventMove(int xPrev, int yPrev, Character character) {
	this.xPrev = xPrev;
	this.yPrev = yPrev;
	this.character = character;
}

public int getXPrev() {
	return xPrev;
}

public int getYPrev() {
	return yPrev;
}

public Character getCharacter() {
	return character;
}
}
