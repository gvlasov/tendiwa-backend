package org.tendiwa.core.events;

import org.tendiwa.core.Item;
import org.tendiwa.core.observation.Event;

public class EventItemAppear implements Event {
	public final Item item;
	public final int x;
	public final int y;

	public EventItemAppear(Item item, int x, int y) {
		this.item = item;
		this.x = x;
		this.y = y;
	}

}
