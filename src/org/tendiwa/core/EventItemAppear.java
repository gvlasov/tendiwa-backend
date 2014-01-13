package org.tendiwa.core;

public class EventItemAppear implements Event {
public final Item item;
public final int x;
public final int y;

public EventItemAppear(Item item, int x, int y) {
	this.item = item;
	this.x = x;
	this.y = y;
}

}
