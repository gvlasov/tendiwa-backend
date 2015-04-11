package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Segment2D;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Aggregate of leaves of depleted branches of {@link InnerTree}s.
 * <p>
 * This is a <a href="http://en.wikipedia.org/wiki/Mediator_pattern">Mediator</a> that helps FloodNetworkTrees remove
 * leaves already terminated by another FloodNetworkTree.
 */
final class DeadEndSet {
	private Set<Segment2D> deadEnds;

	DeadEndSet() {
		this.deadEnds = new LinkedHashSet<>();
	}

	boolean hasDeadEndSegment(Segment2D segment) {
		return deadEnds.contains(segment);
	}

	void addDeadEndSegment(Segment2D lastSegmentOfBranch) {
		assert !deadEnds.contains(lastSegmentOfBranch);
		deadEnds.add(lastSegmentOfBranch);
	}

	/**
	 * Start of a segment is a petiole, end of a segment is a leaf.
	 *
	 * @return A set that contains a petiole-leaf segment for each leaf of this tree that can't be grown any further.
	 */
	Set<Segment2D> values() {
		return deadEnds;
	}

	void replaceDeadEndSegment(Segment2D old, Segment2D replacement) {
		assert deadEnds.contains(old);
		deadEnds.remove(old);
		assert !deadEnds.contains(replacement);
		deadEnds.add(replacement);
	}
}
