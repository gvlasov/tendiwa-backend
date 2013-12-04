package org.tendiwa.events;

import tendiwa.core.Character;
import tendiwa.core.UniqueItem;

public class EventTakeOff implements Event {
private final Character character;
private final UniqueItem item;

public EventTakeOff(Character character, UniqueItem item) {
	super();
	this.character = character;
	this.item = item;
}

public UniqueItem getItem() {
	return item;
}

public Character getCharacter() {
	return character;
}
}
