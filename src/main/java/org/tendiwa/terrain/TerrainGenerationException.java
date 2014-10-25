package org.tendiwa.terrain;

/**
 * An Exception thrown when something goes wrong with generation of terrain.
 *
 * @see org.tendiwa.settlements.SettlementGenerationException
 */
public class TerrainGenerationException extends WorldGenerationException {

	public TerrainGenerationException(String message) {
		super(message);
	}
}
