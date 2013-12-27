package org.tendiwa.events;

import tendiwa.core.Character;
import tendiwa.core.Request;
import tendiwa.core.Tendiwa;

public class RequestAttack implements Request {
public final Character aim;

public RequestAttack(Character aim) {
	this.aim = aim;
}

@Override
public void process() {
	Tendiwa.getPlayerCharacter().attack(aim);
}
}
