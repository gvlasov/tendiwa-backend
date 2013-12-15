package org.tendiwa.events;

import tendiwa.core.Request;
import tendiwa.core.Tendiwa;

public class RequestIdle implements Request {
@Override
public void process() {
	Tendiwa.getPlayerCharacter().idle();
}
}
