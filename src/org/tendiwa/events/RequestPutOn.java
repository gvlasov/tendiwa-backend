package org.tendiwa.events;

import tendiwa.core.Request;
import tendiwa.core.Tendiwa;
import tendiwa.core.UniqueItem;

public class RequestPutOn implements Request {
private final UniqueItem item;

public RequestPutOn(UniqueItem item) {
	this.item = item;
}

@Override
public void process() {
	Tendiwa.getPlayerCharacter().putOn(item);
}
}
