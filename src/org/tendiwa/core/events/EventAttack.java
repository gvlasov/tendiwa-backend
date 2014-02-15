package org.tendiwa.core.events;

import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;

public class EventAttack implements Event {
public final Character aim;
public final Character attacker;

public EventAttack(Character attacker, Character aim) {
	this.attacker = attacker;
	this.aim = aim;
}
}
