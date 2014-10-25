package org.tendiwa.core.events;

import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;

public class EventGetDamage implements Event {
	public final org.tendiwa.core.Character character;
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
