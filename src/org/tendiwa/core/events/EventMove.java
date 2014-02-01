package org.tendiwa.core.events;

import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;

public class EventMove implements Event {
public final org.tendiwa.core.Character character;
public final MovingStyle movingStyle;
public final int xPrev;
public final int yPrev;

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
public EventMove(int xPrev, int yPrev, Character character, MovingStyle movingStyle) {
	this.xPrev = xPrev;
	this.yPrev = yPrev;
	this.character = character;
	this.movingStyle = movingStyle;
}

}
