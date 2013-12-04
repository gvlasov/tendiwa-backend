package org.tendiwa.events;

import tendiwa.core.Character;
import tendiwa.core.Item;

public class EventUnwield implements Event {
private final Character character;
private final Item item;

public EventUnwield(Character character, Item item) {
	super();
	this.character = character;
	this.item = item;
}

public Character getCharacter() {
	return character;
}

public Item getItem() {
	return item;
}
}
