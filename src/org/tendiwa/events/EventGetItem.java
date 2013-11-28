package org.tendiwa.events;

import tendiwa.core.Item;

public class EventGetItem implements Event {
private final Item item;

public EventGetItem(Item item) {
	this.item = item;
}

public Item getItem() {
	return item;
}
}
