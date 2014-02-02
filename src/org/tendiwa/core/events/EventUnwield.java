package org.tendiwa.core.events;

import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;

public class EventUnwield implements Event {
public final org.tendiwa.core.Character character;
public final Item item;

public EventUnwield(Character character, Item item) {
	super();
	this.character = character;
	this.item = item;
}
}
