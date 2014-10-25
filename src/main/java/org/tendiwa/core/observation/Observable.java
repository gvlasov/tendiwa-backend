package org.tendiwa.core.observation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Observable {

	private final Object lock = new Object();
	private final Map<Class<? extends Event>, EventEmitter<? extends Event>> eventEmitters = new HashMap<>();
	private final Set<EventEmitter<? extends Event>> checkedOutEmitters = new HashSet<>();
	private boolean isBusy = false;

	public Object getLock() {
		return lock;
	}

	public <T extends Event> void createEventEmitter(Class<T> eventType) {
		assert !eventEmitters.containsKey(eventType);
		EventEmitter<T> emitter = new EventEmitter<>(this);
		eventEmitters.put(eventType, emitter);
		boolean add = checkedOutEmitters.add(emitter);
		assert add;
	}

	public <T extends Event> void subscribe(Observer<T> observer, Class<T> clazz) {
		getEventEmitter(clazz).subscribe(observer);
	}

	public synchronized <T extends Event> void emitEvent(T event) {
		isBusy = true;
		checkedOutEmitters.clear();
		getEventEmitter(event.getClass()).emitEvent(event);
		boolean alreadyReady = true;
		for (Map.Entry<Class<? extends Event>, EventEmitter<? extends Event>> e : eventEmitters.entrySet()) {
			if (!e.getValue().areAllSubscribersCheckedOut()) {
				alreadyReady = false;
			} else {
				checkedOutEmitters.add(e.getValue());
			}
		}
		if (alreadyReady) {
			isBusy = false;
			checkedOutEmitters.addAll(eventEmitters.values());
			onAllEmittersCheckedOut();
		}
		this.notify();
	}

	private <T extends Event> EventEmitter getEventEmitter(Class<T> clazz) {
		assert eventEmitters.containsKey(clazz) : "No emitter for event class " + clazz.getCanonicalName();
		return eventEmitters.get(clazz);

	}

	<T extends Event> void observersCheckedOut(EventEmitter<T> emitter) {
		assert eventEmitters.containsValue(emitter);
		checkedOutEmitters.add(emitter);
		if (areAllEmittersCheckedOut()) {
			isBusy = false;
			onAllEmittersCheckedOut();
//		checkedOutEmitters.clear();
		}
	}

	public boolean areAllEmittersCheckedOut() {
		return checkedOutEmitters.size() == eventEmitters.size();
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
