package org.tendiwa.core.factories;

import com.google.inject.assistedinject.Assisted;
import org.tendiwa.core.CharacterType;
import org.tendiwa.core.NonPlayerCharacter;

public interface NpcFactory {
public NonPlayerCharacter create(
	@Assisted("x") int x,
	@Assisted("y") int y,
	CharacterType type,
	String name
);
}
