package org.tendiwa.events;

import tendiwa.core.UniqueItem;
import tendiwa.core.Character;

public class EventPutOn implements Event {
private final Character character;
private final UniqueItem item;

public EventPutOn(Character character, UniqueItem item) {
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
