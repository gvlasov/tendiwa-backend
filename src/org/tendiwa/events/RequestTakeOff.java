package org.tendiwa.events;

import tendiwa.core.Request;
import tendiwa.core.Tendiwa;
import tendiwa.core.UniqueItem;

public class RequestTakeOff implements Request {
private final UniqueItem item;

public RequestTakeOff(UniqueItem item) {
	this.item = item;
}

@Override
public void process() {
	Tendiwa.getPlayerCharacter().takeOff(item);
}
}
