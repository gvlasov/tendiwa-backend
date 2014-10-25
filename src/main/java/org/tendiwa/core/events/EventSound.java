package org.tendiwa.core.events;

import org.tendiwa.core.SoundType;
import org.tendiwa.core.observation.Event;
import org.tendiwa.lexeme.Localizable;

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
