package org.tendiwa.core;

public class RequestActionToCell implements Request {
private final ActionToCell action;
private final int x;
private final int y;

public RequestActionToCell(ActionToCell action, int x, int y) {
	this.action = action;
	this.x = x;
	this.y = y;
}

@Override
public void process() {
	action.act(Tendiwa.getPlayerCharacter(), x, y);
}
}
