package org.tendiwa.core.events;

import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;

public class EventSay implements Event {
	public final String message;
	public final org.tendiwa.core.Character character;

	public EventSay(String message, Character character) {
		this.message = message;
		this.character = character;
	}

	public String getMessage() {
		return message;
	}
}
