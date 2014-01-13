package org.tendiwa.core;

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
