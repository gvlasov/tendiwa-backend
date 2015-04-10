package org.tendiwa.geometry;

/**
 * Represents a pair (width,height).
 * <p>
 * This class is supposed to be widely used to be passed as an argument to a method rather than two distinct ints,
 * to show to code reader that these are width and height, not some special values.
 */
public interface Dimension {
	public int getHeight();

	public int getWidth();
}
