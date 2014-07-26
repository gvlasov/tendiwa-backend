package org.tendiwa.core;

import org.tendiwa.core.factories.CharacterFactory;
import org.tendiwa.core.factories.NpcFactory;
import org.tendiwa.core.factories.WorldFactory;

/**
 * Implementing this interface allows a Module class to create a new World. There should be at least one module in
 * Tendiwa distribution that implements this class, otherwise it won't be possible to create a new world.
 */
@FunctionalInterface
public interface WorldProvidingModule {
	World createWorld(
		WorldFactory worldFactory,
		CharacterFactory characterFactory,
		NpcFactory npcFactory
	);
}
