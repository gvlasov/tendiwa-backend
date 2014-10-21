package org.tendiwa.terrain;

/**
 * Indicates a problem that is known to be present given some kind of input, but it is yet unknown by developer how
 * to resolve this issue. If you as an API user encounter such exception, you best move would be to just use another
 * seed for world generation, where a dataset that causes this exception doesn't get produced.
 */
public class KnownWorldGenerationException extends WorldGenerationException {
	public KnownWorldGenerationException(String message) {
		super(message);
	}
}
