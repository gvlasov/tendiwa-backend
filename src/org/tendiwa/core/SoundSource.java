package org.tendiwa.core;

/**
 * Represents a prolonged sound that, unlike {@link Sound}, has its duration, or even may be coming out infinitely.
 *
 * @see {@link Sound}
 */
public class SoundSource extends Sound {
public int lifetime;

public SoundSource(int x, int y, SoundType type, int lifetime) {
	super(x, y, type);
	this.lifetime = lifetime;
}
}
