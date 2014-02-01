package org.tendiwa.core.observation;

import java.util.HashSet;
import java.util.Set;

public class EventEmitter<T extends Event> {
private final Observable observable;
private Set<Observer<T>> subscribers = new HashSet<>();
private Set<Observer<T>> done = new HashSet<>();

EventEmitter(Observable observable) {
	this.observable = observable;
}

void emitEvent(T event) {
	for (Observer<T> observer : subscribers) {
		observer.update(event, this);
	}
}

void subscribe(Observer<T> observer) {
	subscribers.add(observer);
}

/**
 * Called by an Observer when it is done animating.
 *
 * @param observer
 */
public void done(Observer<T> observer) {
	if (done.contains(observer)) {
		throw new RuntimeException("Duplicated done message from observer "+observer);
	}
	if (!subscribers.contains(observer)) {
		throw new RuntimeException("Observer "+observer+" is not the one emitter "+this+" sends events to");
	}
	done.add(observer);
	if (done.size() == subscribers.size()) {
		observable.observersCheckedOut(this);
		done.clear();
	}
}
}
