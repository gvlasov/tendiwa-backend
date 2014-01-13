package org.tendiwa.core;

public class RequestUnwield implements Request {
private final Item item;

public RequestUnwield(Item item) {
	this.item = item;
}

@Override
public void process() {
	Tendiwa.getPlayerCharacter().cease(item);
}
}
