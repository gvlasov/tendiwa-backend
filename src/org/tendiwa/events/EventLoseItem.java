package org.tendiwa.events;

import tendiwa.core.Item;

public class EventLoseItem implements Event {
private final Item item;

public EventLoseItem(Item item) {
	this.item = item;
}

public Item getItem() {
	return item;
}
}
