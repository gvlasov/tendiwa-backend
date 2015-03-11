package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import java.util.Set;

final class InnerTree {
	private final Canopy canopy;
	private final Graph2D fullGraph;
	private final SegmentInserter segmentInserter;
	private final NetworkGenerationParameters parameters;
	private final Random random;
	private final Deque<Ray> branchEnds;
	final boolean grown;
	final Point2D root;

	InnerTree(
		Ray start,
		Sector rootNeighborsSector,
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

		this.branchEnds = new ArrayDeque<>();
		propagateFromRoot(start, rootNeighborsSector);
		this.grown = !branchEnds.isEmpty();
		this.root = start.start;
	}

	private void propagateFromRoot(Ray start, Sector rootNeighborsSector) {
		branchEnds.add(start);
		Ray ray = branchEnds.removeLast();
		if (ray == null) {
			return;
		}
		propagateFromRay(start, rootNeighborsSector);
	}


	boolean isDepleted() {
		return branchEnds.isEmpty();
	}

	void propagate() {
		Ray ray = branchEnds.removeLast();
		if (ray == null) {
			return;
		}
		for (int i = 1; i < parameters.roadsFromPoint; i++) {
			propagateFromRay(
				ray.changeDirection(
					deviateDirection(directionOfIthSpoke(ray, i))
				),
				Sector.FULL_CIRCLE
			);
		}
	}

	private void propagateFromRay(Ray ray, Sector allowedSector) {
		SnapEvent nextStep = segmentInserter.tryPlacingSegment(
			ray,
			allowedSector
		);
		if (nextStep.createsNewSegment()) {
			if (nextStep.isTerminal()) {
				Point2D leaf = nextStep.target();
				saveLeafWithPetiole(ray.start, leaf);
				canopy.addLeaf(leaf);
			} else {
				Ray newRay = new Ray(
					nextStep.target(),
					ray.start.angleTo(nextStep.target())
				);
				assert !segmentInserter.isDeadEnd(newRay.start);
				branchEnds.push(newRay);
			}
		}
	}

	private void saveLeafWithPetiole(Point2D start, Point2D end) {
		canopy.addLeafWithPetiole(fullGraph.getEdge(start, end));
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

	Set<Segment2D> leavesWithPetioles() {
		return canopy.leavesWithPetioles();
	}

	boolean isGrown() {
		return grown;
	}

	Point2D root() {
		return root;
	}
}
