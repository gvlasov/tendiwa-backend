package org.tendiwa.core.dependencies;

import com.google.inject.Provider;
import org.tendiwa.core.Character;

public class PlayerCharacterProvider implements Provider<Character> {
private Character playerCharacter;

public PlayerCharacterProvider() {

}

public void setCharacter(Character playerCharacter) {
	this.playerCharacter = playerCharacter;
}

@Override
public Character get() {
	assert playerCharacter != null;
	return playerCharacter;
}
}
