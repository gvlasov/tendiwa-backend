package org.tendiwa.events;

import tendiwa.core.Item;
import tendiwa.core.Request;
import tendiwa.core.Tendiwa;

public class RequestDrop implements Request {
private final Item item;

public RequestDrop(Item item) {
	this.item = item;
}

@Override
public void process() {
	if (!Tendiwa.getPlayerCharacter().getInventory().contains(item)) {
		throw new RuntimeException("Attempt to drop an item that PlayerCharacter doens't have");
	}
	Tendiwa.getPlayerCharacter().drop(item);
}
}
