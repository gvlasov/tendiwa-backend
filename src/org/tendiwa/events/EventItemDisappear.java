package org.tendiwa.events;

import tendiwa.core.Item;

public class EventItemDisappear implements Event {
private final int x;
private final int y;
private final Item item;

public EventItemDisappear(int x, int y, Item item) {
	this.x = x;
	this.y = y;
	this.item = item;
}

public int getX() {
	return x;
}

public int getY() {
	return y;
}

public Item getItem() {
	return item;
}
}
