package org.tendiwa.core.observation;

public interface Finishable<T extends Event> {
void done(Observer<T> observer);
}
