package org.tendiwa.core.events;

import org.tendiwa.core.Item;
import org.tendiwa.core.observation.Event;

public class EventItemDisappear implements Event {
	public final int x;
	public final int y;
	public final Item item;

	public EventItemDisappear(int x, int y, Item item) {
		this.x = x;
		this.y = y;
		this.item = item;
	}
}
