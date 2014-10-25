package org.tendiwa.core.events;

import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;

public class EventWield implements Event {
	public final Character character;
	public final Item item;

	public EventWield(Character character, Item item) {
		super();
		this.character = character;
		this.item = item;
	}
}
