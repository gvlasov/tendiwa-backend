package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

import java.util.HashSet;
import java.util.Set;

/**
 * Aggregate of current leaves of undepleted {@link org.tendiwa.geometry.smartMesh.FloodNetworkTree}s.
 * <p>
 * This is a <a href="http://en.wikipedia.org/wiki/Mediator_pattern">Mediator</a> that helps FloodNetworkTrees remove
 * leaves already terminated by another FloodNetworkTree.
 */
final class Canopy {
	private final Set<Point2D> canopy = new HashSet<>();

	public boolean containsLeaf(Point2D leaf) {
		return canopy.contains(leaf);
	}

	public void addLeaf(Point2D leaf) {
		boolean added = canopy.add(leaf);
		assert added;
	}

	public void removeLeaf(Point2D leaf) {
		boolean removed = canopy.remove(leaf);
		assert removed;
	}
}
