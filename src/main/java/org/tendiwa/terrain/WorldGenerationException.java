package org.tendiwa.terrain;

/**
 * An Exception thrown when something goes wrong with world generation.
 */
public class WorldGenerationException extends RuntimeException {
	public WorldGenerationException(String message) {
		super(message);
	}
}
