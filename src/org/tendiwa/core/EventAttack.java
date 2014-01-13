package org.tendiwa.core;

public class EventAttack implements Event {
public final Character aim;
public final Character attacker;

public EventAttack(Character attacker, Character aim) {
	this.attacker = attacker;
	this.aim = aim;
}
}
