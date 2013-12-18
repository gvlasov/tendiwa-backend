package org.tendiwa.events;

import tendiwa.core.Character;
import tendiwa.core.DamageSource;
import tendiwa.core.DamageType;

public class EventGetDamage implements Event {
public final Character character;
public final int amount;
public final DamageSource damageSource;
public final DamageType damageType;

public EventGetDamage(Character character, int amount, DamageSource damageSource, DamageType damageType) {
	this.character = character;
	this.amount = amount;
	this.damageSource = damageSource;
	this.damageType = damageType;
}
}
