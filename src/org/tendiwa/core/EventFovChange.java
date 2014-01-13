package org.tendiwa.core;

import com.google.common.collect.ImmutableList;

/**
 * This class should be instantiated
 */
public class EventFovChange {

public final ImmutableList<RenderCell> seenCells;
public final ImmutableList<Integer> unseenCells;
public final ImmutableList<Item> seenItems;
public final ImmutableList<RenderBorder> seenBorders;
public final ImmutableList<RenderBorder> unseenBorders;

EventFovChange(
	ImmutableList<RenderCell> seenCells,
	ImmutableList<Integer> unseenCells,
	ImmutableList<Item> seenItems,
	ImmutableList<RenderBorder> seenBorders,
	ImmutableList<RenderBorder> unseenBorders
) {

	this.seenCells = seenCells;
	this.unseenCells = unseenCells;
	this.seenItems = seenItems;
	this.seenBorders = seenBorders;
	this.unseenBorders = unseenBorders;
}
}
