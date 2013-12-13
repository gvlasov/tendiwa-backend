package org.tendiwa.events;

import tendiwa.core.SoundType;

public class EventSound implements Event {
public final SoundType sound;
public final int x;
public final int y;

public EventSound(SoundType sound, int x, int y) {
	this.sound = sound;
	this.x = x;
	this.y = y;
}
}
