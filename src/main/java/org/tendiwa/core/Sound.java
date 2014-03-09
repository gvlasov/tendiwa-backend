package org.tendiwa.core;

/**
 * Represents a single sound that is, unlike {@link SoundSource} heard by
 * characters at certain moment.
 * 
 * @see SoundSource.
 * @author suseika
 * 
 */
public class Sound {
	public SoundType type;
	public int x;
	public int y;

	public Sound(int x, int y, SoundType type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

}
