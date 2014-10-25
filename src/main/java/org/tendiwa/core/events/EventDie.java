package org.tendiwa.core.events;

import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;

public class EventDie implements Event {
	public final org.tendiwa.core.Character character;

	public EventDie(Character character) {
		this.character = character;
	}
}
