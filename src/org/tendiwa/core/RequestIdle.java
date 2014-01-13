package org.tendiwa.core;

public class RequestIdle implements Request {
@Override
public void process() {
	Tendiwa.getPlayerCharacter().idle();
}
}
