package tendiwa.core;

import org.tendiwa.events.Event;

import java.util.ArrayList;


/**
 * EventQueue is an ordered sequence of {@link ServerEvent}s.
 */
class EventQueue {
	protected ArrayList<Event> events = new ArrayList<>();
	EventQueue add(Event event) {
		events.add(event);
		return this;
	}
	/**
	 * Remove all the {@link ServerEvent}s from this EventQueue so it can be reused.
	 */
	void clear() {
		events.clear();
	}
}
