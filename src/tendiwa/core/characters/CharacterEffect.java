package tendiwa.core.characters;

import tendiwa.core.Character;

public abstract class CharacterEffect {
	public int id;
	public int effectType;
	
	public CharacterEffect() {
		
	}
	public abstract void effect(Character ch, int modifier);
}
