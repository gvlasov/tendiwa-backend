package org.tendiwa.settlements.utils.streetsDetector;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.*;
import java.util.stream.Stream;

final class RadialJointShape {

	private final SortedSet<Joint> sorted;
	private final Collection<Segment2D> bonesAssignedToChains = new HashSet<>();

	public RadialJointShape(
		Point2D jointPoint,
		Set<Segment2D> bones
	) {
		assert bones.stream().allMatch(bone -> bone.oneOfEndsIs(jointPoint));
		this.sorted = allUnorderedPairs(bones);
	}

	private static SortedSet<Joint> allUnorderedPairs(Set<Segment2D> neighborEdges) {
		SortedSet<Joint> sorted = new TreeSet<>();
		List<Segment2D> edges = new ArrayList<>(neighborEdges);
		int size = edges.size();
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				Joint pair = new Joint(edges.get(i), edges.get(j));
				sorted.add(pair);
			}
		}
		return sorted;
	}

	private void makeUsed(Segment2D bone) {
		bonesAssignedToChains.add(bone);
	}

	private boolean anyOfBonesIsAlreadyUsed(Joint joint) {
		return isUsed(joint.bone1) || isUsed(joint.bone2);
	}

	private boolean isUsed(Segment2D bone) {
		return bonesAssignedToChains.contains(bone);
	}

	Stream<Joint> joints() {
		Collection<Joint> joints = new ArrayList<>(sorted.size() / 2);
		for (Joint bestJoint : sorted) {
			if (anyOfBonesIsAlreadyUsed(bestJoint)) {
				continue;
			}
			if (!bestJoint.isAngleTooExtreme()) {
				joints.add(bestJoint);
				makeUsed(bestJoint.bone1);
				makeUsed(bestJoint.bone2);
			}
		}
		return joints.stream();
	}
}
