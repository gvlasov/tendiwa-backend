package org.tendiwa.core.events;

import org.tendiwa.core.Item;
import org.tendiwa.core.observation.Event;

public class EventGetItem implements Event {
private final Item item;

public EventGetItem(Item item) {
	this.item = item;
}

public Item getItem() {
	return item;
}
}
