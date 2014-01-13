package org.tendiwa.core;

public class RequestThrowItem implements Request {
private final Item item;
private final int x;
private final int y;

public RequestThrowItem(Item item, int x, int y) {
	this.item = item;
	this.x = x;
	this.y = y;
}

@Override
public void process() {
	Tendiwa.getPlayerCharacter().propel(item, x, y);
}
}
