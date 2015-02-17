package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.Graph2D;

import javax.annotation.Nullable;
import java.util.*;

final class FloodNetworkTree {
	private final Canopy canopy;
	private final Graph2D fullGraph;
	private final SegmentInserter segmentInserter;
	private final NetworkGenerationParameters parameters;
	private final Random random;
	private final Deque<Ray> leafRays;
	private Set<Segment2D> ends;

	FloodNetworkTree(
		Ray start,
		Canopy canopy,
		Graph2D fullGraph,
		SegmentInserter segmentInserter,
		NetworkGenerationParameters parameters,
		Random random
	) {
		this.canopy = canopy;
		this.fullGraph = fullGraph;
		this.segmentInserter = segmentInserter;
		this.parameters = parameters;
		this.random = random;

		this.ends = new LinkedHashSet<>();
		this.leafRays = new ArrayDeque<>();

		propagateFromRoot(start);
	}

	private void propagateFromRoot(Ray start) {
		Ray ray = getNextLeafRay();
		if (ray == null) {
			return;
		}
		propagateFromRay(start);
	}


	boolean isDepleted() {
		return leafRays.isEmpty();
	}

	void propagate() {
		Ray ray = getNextLeafRay();
		if (ray == null) {
			return;
		}
		for (int i = 1; i < parameters.roadsFromPoint; i++) {
			propagateFromRay(
				ray.changeDirection(
					deviateDirection(directionOfIthSpoke(ray, i))
				)
			);
		}
	}

	@Nullable
	private Ray getNextLeafRay() {
		Ray ray = null;
		while (!leafRays.isEmpty()) {
			ray = leafRays.removeLast();
			if (!canopy.containsLeaf(ray.start)) {
				break;
			}
		}
		return ray;
	}

	private void propagateFromRay(Ray ray) {
		canopy.removeLeaf(ray.start);
		PropagationStep nextStep = segmentInserter.tryPlacingRoad(ray);
		if (nextStep.isTerminal()) {
			saveLeaf(ray.start, nextStep.ray().start);
		} else {
			Ray newRay = nextStep.ray();
			assert !segmentInserter.isDeadEnd(newRay.start);
			leafRays.push(newRay);
			canopy.addLeaf(newRay.start);
		}
	}

	private void saveLeaf(Point2D start, Point2D end) {
		ends.add(fullGraph.getEdge(start, end));
	}

	/**
	 * Start of a segment is a petiole, end of a segment is a leaf.
	 *
	 * @return A set that contains a petiole-leaf segment for each leaf of this tree that can't be grown any further.
	 */
	Set<Segment2D> leavesWithPetioles() {
		return ends;
	}

	private double directionOfIthSpoke(Ray node, int i) {
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

}
