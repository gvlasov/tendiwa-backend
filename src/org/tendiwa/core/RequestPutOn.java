package org.tendiwa.core;

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
