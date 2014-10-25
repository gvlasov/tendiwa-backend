package org.tendiwa.settlements;

import org.tendiwa.terrain.WorldGenerationException;

/**
 * An Exception thrown when something goes wrong with generation of cities, villages etc.
 */
public class SettlementGenerationException extends WorldGenerationException {
	public SettlementGenerationException(String s) {
		super(s);
	}
}
