package org.tendiwa.events;

import tendiwa.core.SoundType;

public class EventSound implements Event {
public final SoundType shout;
public final int x;
public final int y;

public EventSound(SoundType shout, int x, int y) {

	this.shout = shout;
	this.x = x;
	this.y = y;
}
}
