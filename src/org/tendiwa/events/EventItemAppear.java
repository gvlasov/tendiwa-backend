package org.tendiwa.events;

import tendiwa.core.Item;

public class EventItemAppear implements Event {
private final Item item;

public EventItemAppear(Item item) {
	this.item = item;
}

public Item getItem() {
	return item;
}
}
