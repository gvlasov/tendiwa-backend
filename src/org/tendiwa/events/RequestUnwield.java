package org.tendiwa.events;

import tendiwa.core.Item;
import tendiwa.core.Request;
import tendiwa.core.Tendiwa;

public class RequestUnwield implements Request {
private final Item item;

public RequestUnwield(Item item) {
	this.item = item;
}

@Override
public void process() {
	Tendiwa.getPlayer().cease(item);
}
}
