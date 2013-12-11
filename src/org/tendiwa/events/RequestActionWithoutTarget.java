package org.tendiwa.events;

import tendiwa.core.ActionWithoutTarget;
import tendiwa.core.Request;
import tendiwa.core.Tendiwa;

public class RequestActionWithoutTarget implements Request {
private final ActionWithoutTarget action;

public RequestActionWithoutTarget(ActionWithoutTarget action) {
	this.action = action;
}

@Override
public void process() {
	action.act(Tendiwa.getPlayerCharacter());
}
}
