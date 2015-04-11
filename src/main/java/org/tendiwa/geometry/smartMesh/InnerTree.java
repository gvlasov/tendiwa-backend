package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

import java.util.ArrayDeque;
import java.util.Deque;

final class InnerTree {
	private final Forest forest;
	private final DirectionDeviation directionDeviation;
	private final NetworkGenerationParameters config;
	private final Deque<Ray> branchEnds;
	final boolean grown;
	final Point2D root;

	InnerTree(
		Ray root,
		Sector rootSector,
		Forest forest,
		DirectionDeviation directionDeviation,
		NetworkGenerationParameters config
	) {
		this.forest = forest;
		this.directionDeviation = directionDeviation;
		this.config = config;

		this.branchEnds = new ArrayDeque<>();
		propagateFromRoot(root, rootSector);
		this.grown = !branchEnds.isEmpty();
		this.root = root.start;
	}

	private void propagateFromRoot(Ray start, Sector rootSector) {
		// TODO: Extract to InnerTreeSeed?
		branchEnds.add(start);
		Ray ray = branchEnds.removeLast();
		if (ray == null) {
			return;
		}
		propagateFromRay(start, rootSector);
	}

	boolean isDepleted() {
		return branchEnds.isEmpty();
	}

	void propagate() {
		Ray ray = branchEnds.removeLast();
		if (ray == null) {
			return;
		}
		for (int i = 1; i < config.roadsFromPoint; i++) {
			double newDirection = directionDeviation
				.deviateDirection(directionOfIthSpoke(ray, i));
			Ray newRay = ray.changeDirection(newDirection);
			propagateFromRay(newRay, Sector.FULL_CIRCLE);
		}
	}

	private void propagateFromRay(Ray ray, Sector allowedSector) {
		PropagationEvent nextStep = forest.tryPlacingSegment(
			ray,
			allowedSector
		);
		if (nextStep.createsNewSegment()) {
			if (nextStep.isTerminal()) {
				addDeadEnd(ray.start, nextStep.target());
			} else {
				pushStepToBranch(ray, nextStep);
			}
		}
	}

	private void addDeadEnd(Point2D petiole, Point2D leaf) {
		deadEndSet.addDeadEndSegment(fullGraph.getEdge(petiole, leaf));
	}

	private void pushStepToBranch(Ray ray, PropagationEvent nextStep) {
		Ray newRay = new Ray(
			nextStep.target(),
			ray.start.angleTo(nextStep.target())
		);
		branchEnds.push(newRay);
	}

	private double directionOfIthSpoke(Ray node, int i) {
		return node.direction + Math.PI + i * (Math.PI * 2 / config.roadsFromPoint);
	}

	boolean isGrown() {
		return grown;
	}

	Point2D root() {
		return root;
	}
}