package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Sector;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class FloodPart {
	private final DirectionDeviation directionDeviation;
	private final NetworkGenerationParameters config;
	private final Deque<Ray> branchEnds;

	FloodPart(
		Ray floodStart,
		DirectionDeviation directionDeviation,
		NetworkGenerationParameters config
	) {
		this.directionDeviation = directionDeviation;
		this.config = config;
		this.branchEnds = queueWithNetworkStart(floodStart);
	}

	private Deque<Ray> queueWithNetworkStart(Ray floodStart) {
		Deque<Ray> deque = new ArrayDeque<>();
		deque.add(floodStart);
		return deque;
	}

	boolean isDepleted() {
		return branchEnds.isEmpty();
	}

	Stream<Ray> pullNextSegmentStarts() {
		Ray baseRay = branchEnds.removeLast();
		return IntStream.range(1, config.roadsFromPoint)
			.mapToObj(i->createIthRay(baseRay, i));
	}
	void pushNewSegmentStart(Ray newSegmentStart) {
		branchEnds.push(newSegmentStart);
	}

	private Ray createIthRay(Ray base, int spokeNum) {
		double undeviatedDirection = base.direction + Math.PI + spokeNum * (Math.PI * 2 / config.roadsFromPoint);
		double newDirection = directionDeviation.deviateDirection(undeviatedDirection);
		return base.changeDirection(newDirection);
	}
}