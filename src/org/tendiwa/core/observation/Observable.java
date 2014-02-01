package org.tendiwa.core.observation;

import java.util.HashMap;
import java.util.Map;

public class Observable {

private final Map<Class<? extends Event>, EventEmitter<? extends Event>> eventEmitters = new HashMap<>();

public <T extends Event> void createEventEmitter(Class<T> eventType) {
	eventEmitters.put(eventType, new EventEmitter<T>(this));
}

public <T extends Event> void subscribe(Observer<T> observer, Class<T> clazz) {
	getEventEmitter(clazz).subscribe(observer);
}

public <T extends Event> void emitEvent(T event) {
	getEventEmitter(event.getClass()).emitEvent(event);
}

private <T extends Event> EventEmitter getEventEmitter(Class<T> clazz) {
	assert eventEmitters.containsKey(clazz);
	return eventEmitters.get(clazz);

}

public void observersCheckedOut(EventEmitter emitter) {

}
}
