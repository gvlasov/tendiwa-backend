package org.tendiwa.core;

public class EventWield implements Event {
private final Character character;
private final Item item;

public EventWield(Character character, Item item) {
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
