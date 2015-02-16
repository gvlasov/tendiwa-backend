package org.tendiwa.geometry.smartMesh;

import java.util.*;

final class FloodNetworkTree {
	private final SegmentInserter segmentInserter;
	private final NetworkGenerationParameters parameters;
	private final Random random;
	private final Deque<DirectionFromPoint> nodeQueue;

	FloodNetworkTree(
		DirectionFromPoint start,
		SegmentInserter segmentInserter,
		NetworkGenerationParameters parameters,
		Random random
	) {
		this.segmentInserter = segmentInserter;
		this.parameters = parameters;
		this.random = random;

		this.nodeQueue = new ArrayDeque<>();
		Optional<DirectionFromPoint> nextHub = segmentInserter.tryPlacingRoad(start);
		if (nextHub.isPresent() && !segmentInserter.isDeadEnd(nextHub.get().point)) {
			nodeQueue.push(nextHub.get());
		}
	}


	boolean isDepleted() {
		return nodeQueue.isEmpty();
	}

	void propagate() {
		DirectionFromPoint node = nodeQueue.removeLast();
		for (int i = 1; i < parameters.roadsFromPoint; i++) {
			double newDirection = deviateDirection(directionOfIthSpoke(node, i));
			Optional<DirectionFromPoint> nextHubMaybe = segmentInserter.tryPlacingRoad(
				node.changeDirection(newDirection)
			);
			if (nextHubMaybe.isPresent()) {
				DirectionFromPoint nextHub = nextHubMaybe.get();
				assert !segmentInserter.isDeadEnd(nextHub.point);
				nodeQueue.push(nextHub);
			}
		}
	}

	private double directionOfIthSpoke(DirectionFromPoint node, int i) {
		return node.direction + Math.PI + i * (Math.PI * 2 / parameters.roadsFromPoint);
	}

	/**
	 * Returns a slightly changed direction.
	 * <p>
	 * If {@link NetworkGenerationParameters#favourAxisAlignedSegments} is true, then
	 * the answer will be tilted towards the closest
	 * {@code Math.PI/2*n} angle.
	 *
	 * @param newDirection
	 * 	Original angle in radians.
	 * @return Slightly changed angle in radians. Answer is not constrained to [0; 2*PI] interval — it may be any
	 * number.
	 */
	private double deviateDirection(double newDirection) {
		double v = random.nextDouble();
		if (parameters.favourAxisAlignedSegments) {
			double closestAxisParallelDirection = Math.round(newDirection / (Math.PI / 2)) * (Math.PI / 2);
			if (Math.abs(closestAxisParallelDirection - newDirection) < parameters.secondaryRoadNetworkDeviationAngle) {
				return closestAxisParallelDirection;
			} else {
				return newDirection +
					parameters.secondaryRoadNetworkDeviationAngle
						* Math.signum(closestAxisParallelDirection - newDirection);
			}
		} else {
			return newDirection
				- parameters.secondaryRoadNetworkDeviationAngle
				+ v * parameters.secondaryRoadNetworkDeviationAngle * 2;
		}
	}

	final class Node {
		final List<Node> descendants;
		final DirectionFromPoint payload;

		Node(DirectionFromPoint payload) {
			this.payload = payload;
			this.descendants = new ArrayList<>(parameters.roadsFromPoint - 1);
		}

		void addDescendant(DirectionFromPoint payload) {
			descendants.add(new Node(payload));
		}
	}
}
