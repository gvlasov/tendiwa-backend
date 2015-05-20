package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.Sets;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.RandomCoordinateComparator;

import java.util.*;
import java.util.stream.Stream;

import static org.tendiwa.collections.Collectors.toLinkedHashSet;

final class FloodFromMissingInnerCycles {
	private final Collection<OrientedCycle> enclosedCycles;
	private final Random random;

	FloodFromMissingInnerCycles(
		Collection<OrientedCycle> enclosedCycles,
		Set<Point2D> deadEnds,
		Random random
	) {
		this.enclosedCycles = enclosedCycles;
		this.random = random;
	}

	Set<FloodStart> floods() {
		return enclosedCycles.stream()
			.flatMap(this::missingTrees)
			.collect(toLinkedHashSet());
	}

	private FloodStart createFlood(OrientedCycle enclosedCycle, Point2D root) {
		return new FloodStart(
			enclosedCycle.deviatedAngleBisector(root, false),
			new OrientedCycleSector(enclosedCycle, root, false),
			Optional.empty()
		);
	}

	private Stream<FloodStart> missingTrees(OrientedCycle cycle) {
		assert enclosedCycles.contains(cycle);
		Set<Point2D> connections = pointsOfContactWithInnerNetwork(cycle);
		if (connections.size() == 1) {
			Point2D theOnlyConnection = connections.iterator().next();
			return createMissingFloodStart(cycle, theOnlyConnection);
		} else if (connections.size() == 0) {
			return create2OppositeMissingFloodStarts(cycle);
		} else {
			return Stream.empty();
		}
	}

	private Stream<FloodStart> createMissingFloodStart(OrientedCycle cycle, Point2D oppositePoint) {
		assert oppositePoint != null;
		assert cycle.vertexSet().contains(oppositePoint);
		return Stream.of(
			createFlood(
				cycle,
				findOppositePointOnCycle(cycle, oppositePoint)
			)
		);
	}

	private Stream<FloodStart> create2OppositeMissingFloodStarts(OrientedCycle cycle) {
		Comparator<Point2D> coordinateComparator = new RandomCoordinateComparator(random);
		Point2D leastPoint = cycle
			.vertexSet()
			.stream()
			.max(coordinateComparator)
			.get();
		Point2D greatestPoint = cycle
			.vertexSet()
			.stream()
			.min(coordinateComparator)
			.get();
		return Stream.of(
			createFlood(cycle, leastPoint),
			createFlood(cycle, greatestPoint)
		);
	}

	private Set<Point2D> pointsOfContactWithInnerNetwork(OrientedCycle cycle) {
		return Sets.intersection(
			cycle.vertexSet(),
			deadEnds
		);
	}

	private Point2D findOppositePointOnCycle(OrientedCycle cycle, Point2D point) {
		return cycle
			.vertexSet()
			.stream()
			.max((a, b) -> {
				double distanceSquaredA = point.squaredDistanceTo(a);
				double distanceSquaredB = point.squaredDistanceTo(b);
				return (int) Math.signum(distanceSquaredA - distanceSquaredB);
			}).get();
	}
}