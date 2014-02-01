package org.tendiwa.core.observation;

public interface Observer<T extends Event> {
public void update(T event, EventEmitter<T> emitter);

}
