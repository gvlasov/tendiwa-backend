package org.tendiwa.events;

import tendiwa.core.Character;

public class EventDie implements Event {
public final Character character;

public EventDie(Character character) {
	this.character = character;
}
}
