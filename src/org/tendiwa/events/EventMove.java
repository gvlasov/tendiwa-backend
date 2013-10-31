package org.tendiwa.events;
import tendiwa.core.Character;

public class EventMove {
final Character character;
final int x;
final int y;

EventMove(int x, int y, Character character) {
	this.x = x;
	this.y = y;
	this.character = character;
}

public int getX() {
	return x;
}

public int getY() {
	return y;
}

public Character getCharacter() {
	return character;
}
}
