package tendiwa.core;

import org.tendiwa.events.Event;

public class EventSay implements Event {
private final String message;

public EventSay(String message) {
	this.message = message;
}

public String getMessage() {
	return message;
}
}
