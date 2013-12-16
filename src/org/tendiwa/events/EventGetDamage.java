package org.tendiwa.events;

import tendiwa.core.Character;
import tendiwa.core.DamageSource;

public class EventGetDamage implements Event {
public final Character character;
public final int amount;
public final DamageSource damageSource;

public EventGetDamage(Character character, int amount, DamageSource damageSource) {
	this.character = character;
	this.amount = amount;
	this.damageSource = damageSource;
}
}
