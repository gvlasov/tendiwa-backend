package org.tendiwa.core.observation;

public interface Finishable<T extends Event> {
	// TODO: Instead of done method, maybe better make notFinishRightNow method, so it would finish on exit from
	// method by default.
void done(Observer<T> observer);
}
