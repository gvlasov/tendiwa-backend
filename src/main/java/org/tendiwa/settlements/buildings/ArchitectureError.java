package org.tendiwa.settlements.buildings;

import org.tendiwa.terrain.WorldGenerationException;

public class ArchitectureError extends WorldGenerationException {
	public ArchitectureError(String message) {
		super(message);
	}
}
