package org.tendiwa.core;

public class Spell<T extends ActionTargetType> extends CharacterAbility<T> {
	int mana;

	public void mana(int mana) {
		this.mana = mana;
	}

}