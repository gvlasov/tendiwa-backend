package org.tendiwa.core.observation;

public class ProxyOccurrence<T extends Event> {
final T event;
final Finishable<T> emitter;
final Observer<T> proxiedObserver;
final Observer<T> proxyingObserver;

public ProxyOccurrence(T event, Finishable<T> emitter, Observer<T> proxiedObserver, Observer<T> proxyingObserver) {
	this.event = event;
	this.emitter = emitter;
	this.proxiedObserver = proxiedObserver;
	this.proxyingObserver = proxyingObserver;
}
}
