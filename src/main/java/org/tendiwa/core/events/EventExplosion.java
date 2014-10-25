package org.tendiwa.core.events;

import org.tendiwa.core.observation.Event;

public class EventExplosion implements Event {
	public final int x;
	public final int y;

	public EventExplosion(int x, int y) {

		this.x = x;
		this.y = y;
	}
}
