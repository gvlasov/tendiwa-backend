package org.tendiwa.core.events;

import com.google.common.collect.ImmutableList;
import org.tendiwa.core.Border;
import org.tendiwa.core.Item;
import org.tendiwa.core.clients.RenderBorder;
import org.tendiwa.core.clients.RenderCell;
import org.tendiwa.core.observation.Event;

/**
 * This class should be instantiated
 */
public class EventFovChange implements Event {

public final ImmutableList<RenderCell> seenCells;
public final ImmutableList<Integer> unseenCells;
public final ImmutableList<Item> seenItems;
public final ImmutableList<RenderBorder> seenBorders;
public final ImmutableList<Border> unseenBorders;

public EventFovChange(
	ImmutableList<RenderCell> seenCells,
	ImmutableList<Integer> unseenCells,
	ImmutableList<Item> seenItems,
	ImmutableList<RenderBorder> seenBorders,
	ImmutableList<Border> unseenBorders
) {

	this.seenCells = seenCells;
	this.unseenCells = unseenCells;
	this.seenItems = seenItems;
	this.seenBorders = seenBorders;
	this.unseenBorders = unseenBorders;
}
}
