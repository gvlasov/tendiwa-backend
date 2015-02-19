package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Aggregate of leaves of depleted branches of {@link org.tendiwa.geometry.smartMesh.FloodNetworkTree}s.
 * <p>
 * This is a <a href="http://en.wikipedia.org/wiki/Mediator_pattern">Mediator</a> that helps FloodNetworkTrees remove
 * leaves already terminated by another FloodNetworkTree.
 */
final class Canopy {
	private final Set<Point2D> canopy;
	private Set<Segment2D> leavesWithPetioles;

	Canopy() {
		this.canopy = new HashSet<>();
		this.leavesWithPetioles = new LinkedHashSet<>();
	}

	boolean hasLeaf(Point2D leaf) {
		return canopy.contains(leaf);
	}

	void addLeaf(Point2D leaf) {
		canopy.add(leaf);
	}

	void removeLeaf(Point2D leaf) {
		boolean removed = canopy.remove(leaf);
		assert removed;
	}

	boolean hasLeafWithPetiole(Segment2D segment2D) {
		return leavesWithPetioles.contains(segment2D);
	}

	void addLeafWithPetiole(Segment2D leafWithPetiole) {
		assert !leavesWithPetioles.contains(leafWithPetiole);
		leavesWithPetioles.add(leafWithPetiole);
	}

	/**
	 * Start of a segment is a petiole, end of a segment is a leaf.
	 *
	 * @return A set that contains a petiole-leaf segment for each leaf of this tree that can't be grown any further.
	 */
	Set<Segment2D> leavesWithPetioles() {
		return leavesWithPetioles;
	}

	void replaceLeafWithPetiole(Segment2D old, Segment2D replacement) {
		assert leavesWithPetioles.contains(old);
		leavesWithPetioles.remove(old);
		assert !leavesWithPetioles.contains(replacement);
		leavesWithPetioles.add(replacement);
	}
}
