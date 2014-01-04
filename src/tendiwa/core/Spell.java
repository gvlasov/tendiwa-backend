package tendiwa.core;

public class Spell<T extends ActionTargetType> extends CharacterAbility {
int mana;

public void mana(int mana) {
	this.mana = mana;
}

}