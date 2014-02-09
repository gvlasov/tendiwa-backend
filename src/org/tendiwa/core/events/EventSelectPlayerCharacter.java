package org.tendiwa.core.events;

import org.tendiwa.core.Character;
import org.tendiwa.core.World;
import org.tendiwa.core.observation.Event;

public class EventSelectPlayerCharacter implements Event {
public final Character player;
public final World world;

public EventSelectPlayerCharacter(Character player, World world) {
	this.player = player;
	this.world = world;
}
}
