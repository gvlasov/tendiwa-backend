package org.tendiwa.core.events;

import org.tendiwa.core.Character;
import org.tendiwa.core.Volition;
import org.tendiwa.core.World;
import org.tendiwa.core.observation.Event;

public class EventSelectPlayerCharacter implements Event {
public final Volition volition;
public final Character player;
public final World world;

public EventSelectPlayerCharacter(Volition volition, Character player, World world) {
	this.volition = volition;
	this.player = player;
	this.world = world;
}
}
