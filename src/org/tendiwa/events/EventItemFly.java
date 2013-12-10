package org.tendiwa.events;

import tendiwa.core.Item;

public class EventItemFly implements Event {
public final Item item;
public final int fromX;
public final int fromY;
public final int toX;
public final int toY;
public final FlightStyle style;

public EventItemFly(Item item, int fromX, int fromY, int toX, int toY, FlightStyle style) {
	this.item = item;
	this.fromX = fromX;
	this.fromY = fromY;
	this.toX = toX;
	this.toY = toY;
	this.style = style;
}

/**
 * Describes how the item flies to its destination point.
 */
public enum FlightStyle {
	/**
	 * If item is not a specialized missile (for example, if a character throws a cloth or a sword).
	 */
	CAST,
	/**
	 * If item is propelled as a specialized missile (for example, if a character throws a dart, javelin, rock).
	 */
	PROPELLED
}
}
