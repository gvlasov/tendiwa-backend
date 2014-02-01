package org.tendiwa.core.events;

import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;

public class EventUnwield implements Event {
private final org.tendiwa.core.Character character;
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
