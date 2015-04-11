package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;

import java.util.Random;

final class SegmentPlacement {
	private final AppendableNetworkPart forest;
	private final NetworkGenerationParameters config;
	private final FullNetwork fullNetwork;
	private final Random random;

	SegmentPlacement(
		FullNetwork fullNetwork,
		AppendableNetworkPart forest,
		NetworkGenerationParameters config,
		Random random
	) {
		this.forest = forest;
		this.config = config;
		this.fullNetwork = fullNetwork;
		this.random = random;
	}

	/**
	 * [Kelly figure 42, function placeSegment]
	 * <p>
	 * Tries adding a new segment to the secondary network.
	 */
	PropagationEvent tryPlacingSegment(Ray beginning, Sector allowedSector) {
		double segmentLength = deviatedLength(config.segmentLength);
		PropagationEvent event = new SnapTest(
			config.snapSize,
			beginning.start,
			beginning.placeEnd(segmentLength),
			fullNetwork.graph(),
			allowedSector
		).snap();
		if (event.createsNewSegment()) {
			event.integrateInto(forest);
		}
		Point2D branchPreEnd = beginning.start;
		Point2D branchEnd = event.target();
		if (event.isTerminal() && circuitMakesTightRectangle(branchPreEnd, branchEnd)) {
			removeBranch(branchEnd, branchPreEnd);
		}
		return event;
	}

	private void removeBranch(Point2D branchEnd, Point2D branchPreEnd) {
		throw new UnsupportedOperationException();
	}

	private double deviatedLength(double segmentLength) {
		return segmentLength - config.secondaryNetworkSegmentLengthDeviation / 2 + random.nextDouble() *
			config.secondaryNetworkSegmentLengthDeviation;
	}

	private boolean circuitMakesTightRectangle(Point2D start, Point2D target) {
		throw new UnsupportedOperationException();
	}
}
