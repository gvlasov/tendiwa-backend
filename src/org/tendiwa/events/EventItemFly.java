package org.tendiwa.events;

import tendiwa.core.Item;

public class EventItemFly implements Event {
public final Item item;
public final int fromX;
public final int fromY;
public final int toX;
public final int toY;


public EventItemFly(Item item, int fromX, int fromY, int toX, int toY) {
	this.item = item;
	this.fromX = fromX;
	this.fromY = fromY;
	this.toX = toX;
	this.toY = toY;
}
}
