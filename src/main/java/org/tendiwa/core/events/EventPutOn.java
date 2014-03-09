package org.tendiwa.core.events;

import org.tendiwa.core.*;
import org.tendiwa.core.Character;
import org.tendiwa.core.observation.Event;

public class EventPutOn implements Event {
public final Character character;
public final UniqueItem item;

public EventPutOn(Character character, UniqueItem item) {
	this.character = character;
	this.item = item;
}

}
