package org.tendiwa.core;

public class EventGetItem implements Event {
private final Item item;

public EventGetItem(Item item) {
	this.item = item;
}

public Item getItem() {
	return item;
}
}
