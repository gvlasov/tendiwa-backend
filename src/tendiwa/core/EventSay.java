package tendiwa.core;

import org.tendiwa.events.Event;

public class EventSay implements Event {
public final String message;
public final Character character;

public EventSay(String message, Character character) {
	this.message = message;
	this.character = character;
}

public String getMessage() {
	return message;
}
}
