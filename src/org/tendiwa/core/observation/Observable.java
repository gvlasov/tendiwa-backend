package org.tendiwa.core.observation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Observable {

private static final Object lock = new Object();
private final Map<Class<? extends Event>, EventEmitter<? extends Event>> eventEmitters = new HashMap<>();
private final Set<EventEmitter<? extends Event>> checkedOutEmitters = new HashSet<>();
private boolean isBusy = false;

public static Object getLock() {
	return lock;
}

public <T extends Event> void createEventEmitter(Class<T> eventType) {
	eventEmitters.put(eventType, new EventEmitter<T>(this));
}

public <T extends Event> void subscribe(Observer<T> observer, Class<T> clazz) {
	getEventEmitter(clazz).subscribe(observer);
}

public <T extends Event> void emitEvent(T event) {
	isBusy = true;
	getEventEmitter(event.getClass()).emitEvent(event);
	boolean alreadyReady = true;
	for (EventEmitter emitter : eventEmitters.values()) {
		if (!emitter.areAllSubscribersCheckedOut()) {
			alreadyReady = false;
			break;
		}
	}
	if (alreadyReady) {
		isBusy = false;
		checkedOutEmitters.addAll(eventEmitters.values());
		onAllEmittersCheckedOut();
		checkedOutEmitters.clear();
	}
}

private <T extends Event> EventEmitter getEventEmitter(Class<T> clazz) {
	assert eventEmitters.containsKey(clazz) : clazz.getCanonicalName();
	return eventEmitters.get(clazz);

}

<T extends Event> void observersCheckedOut(EventEmitter<T> emitter) {
	assert eventEmitters.containsValue(emitter);
	checkedOutEmitters.add(emitter);
	if (areAllEmittersCheckedOut()) {
		isBusy = false;
		onAllEmittersCheckedOut();
		checkedOutEmitters.clear();
	}
}

public boolean areAllEmittersCheckedOut() {
	return !isBusy;
}

public void waitForAnimationToStartAndComplete() {
	synchronized (lock) {
		if (!areAllEmittersCheckedOut()) {
			try {
				lock.wait();
			} catch (InterruptedException ignored) {
			}
		}
	}
}

public void onAllEmittersCheckedOut() {
	synchronized (lock) {
		lock.notify();
	}
}
}
