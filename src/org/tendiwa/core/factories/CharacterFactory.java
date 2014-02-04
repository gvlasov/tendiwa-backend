package org.tendiwa.core.factories;

import org.tendiwa.core.Character;
import org.tendiwa.core.CharacterType;
import org.tendiwa.core.observation.Observable;

public interface CharacterFactory {
public Character create(int x, int y, CharacterType type, String name);
}
