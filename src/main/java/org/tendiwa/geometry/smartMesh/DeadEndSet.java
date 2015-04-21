package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Segment2D;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Aggregate of leaves of depleted branches of {@link FloodPart}s.
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

	void add(Segment2D lastSegmentOfBranch) {
		assert !deadEnds.contains(lastSegmentOfBranch);
		deadEnds.add(lastSegmentOfBranch);
	}

	Set<Segment2D> values() {
		return deadEnds;
	}

	void replaceDeadEndSegment(Segment2D old, Segment2D replacement) {
		assert deadEnds.contains(old);
		deadEnds.remove(old);
		assert !deadEnds.contains(replacement);
		deadEnds.add(replacement);
	}
	void replace(CutSegment2D cutSegment) {
		assert cutSegment.segmentStream().count() == 2;
		assert deadEnds.contains(cutSegment.originalSegment());
		Segment2D replacement = cutSegment
			.segmentStream()
			.skip(1)
			.findFirst()
			.get();
		replaceDeadEndSegment(cutSegment.originalSegment(), replacement);
	}
}
