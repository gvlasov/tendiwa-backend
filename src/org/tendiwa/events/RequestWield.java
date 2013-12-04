package org.tendiwa.events;

import tendiwa.core.Item;
import tendiwa.core.Request;
import tendiwa.core.Tendiwa;

public class RequestWield implements Request {
private final Item item;

public RequestWield(Item item) {
	this.item = item;
}

@Override
public void process() {
	Tendiwa.getPlayer().wield(item);
}
}
