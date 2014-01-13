package org.tendiwa.core;

public class RequestWield implements Request {
private final Item item;

public RequestWield(Item item) {
	this.item = item;
}

@Override
public void process() {
	Tendiwa.getPlayerCharacter().wield(item);
}
}
