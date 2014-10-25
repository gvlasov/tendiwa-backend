package org.tendiwa.core.observation;

import com.google.inject.Inject;

import java.util.LinkedList;

public class ThreadProxy extends Observable {
	private final Observable underlyingObservable;
	private final LinkedList<ProxyOccurrence> collected = new LinkedList<>();
	private boolean waitForNext;

	@Inject
	public ThreadProxy(
		Observable underlyingObservable
	) {
		this.underlyingObservable = underlyingObservable;
	}

	@Override
	public <T extends Event> void subscribe(final Observer<T> observer, Class<T> clazz) {
		underlyingObservable.subscribe(new Observer<T>() {
			@Override
			public void update(T event, Finishable<T> emitter) {
				collected.add(new ProxyOccurrence<>(event, emitter, observer, this));
			}
		}, clazz);
	}

	public void waitForNextEventInCurrentFrame() {
		waitForNext = true;
	}

	public void executeCollected() {
		do {
			LinkedList<ProxyOccurrence> oldCollected = new LinkedList<>(collected);
			collected.clear();
			for (final ProxyOccurrence occurrence : oldCollected) {
				occurrence.proxiedObserver.update(occurrence.event, new Finishable() {
					@Override
					public void done(Observer observer) {
						occurrence.emitter.done(occurrence.proxyingObserver);
					}
				});
			}
			if (waitForNext) {
				if (collected.isEmpty()) {
					synchronized (underlyingObservable) {
						try {
							underlyingObservable.wait();
						} catch (InterruptedException ignored) {
						}
					}
				}
				waitForNext = false;
			}
		} while (!collected.isEmpty());
	}

	@Override
	public boolean areAllEmittersCheckedOut() {
		return underlyingObservable.areAllEmittersCheckedOut();
	}

	public boolean hasEventsCollected() {
		return !collected.isEmpty();
	}
}
