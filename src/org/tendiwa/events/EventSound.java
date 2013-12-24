package org.tendiwa.events;

import org.tendiwa.lexeme.Localizable;
import tendiwa.core.SoundType;

public class EventSound implements Event {
public final SoundType sound;
public final Localizable source;
public final int x;
public final int y;

public EventSound(SoundType sound, Localizable soundSource, int x, int y) {
	this.sound = sound;
	this.x = x;
	this.y = y;
	this.source = soundSource;
}
}
