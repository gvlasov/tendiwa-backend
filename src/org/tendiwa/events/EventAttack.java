package org.tendiwa.events;

import tendiwa.core.*;
import tendiwa.core.Character;

public class EventAttack implements Event {
private final Character aim;

public EventAttack(Character aim) {
	this.aim = aim;
}
}
