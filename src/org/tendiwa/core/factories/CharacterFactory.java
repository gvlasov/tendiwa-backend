package org.tendiwa.core.factories;

import org.tendiwa.core.Character;
import org.tendiwa.core.CharacterType;
import org.tendiwa.core.observation.Observable;

public class CharacterFactory {
private final Observable backend;

public CharacterFactory(Observable backend) {
	this.backend = backend;
}

public Character create(int x, int y, CharacterType type, String name) {
	return new Character(backend, type, x, y, name);
}
}
