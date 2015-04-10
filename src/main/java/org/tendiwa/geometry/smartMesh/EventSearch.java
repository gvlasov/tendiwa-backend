package org.tendiwa.geometry.smartMesh;

import java.util.Optional;

@FunctionalInterface
/**
 * Searches for the best point to snap to based on a particular condition.
 */
interface EventSearch {
	Optional<PropagationEvent> find();
}
