package org.tendiwa.core.factories;

import com.google.inject.assistedinject.Assisted;
import org.tendiwa.core.Character;
import org.tendiwa.core.CharacterType;

public interface CharacterFactory {
	public Character create(@Assisted("x") int x, @Assisted("y") int y, CharacterType type, String name);
}
