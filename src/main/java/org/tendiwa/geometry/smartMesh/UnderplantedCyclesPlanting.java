package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.Sets;
import org.tendiwa.geometry.Point2D;

import java.util.Collection;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.tendiwa.collections.Collectors.toLinkedHashSet;

final class UnderplantedCyclesPlanting {
	private final Collection<OrientedCycle> enclosedCycles;
	private final TreeCreator treeCreator;
	private final Random random;
	private final Set<Point2D> deadEnds;

	UnderplantedCyclesPlanting(
		Collection<OrientedCycle> enclosedCycles,
		TreeCreator treeCreator,
		Set<Point2D> deadEnds,
		Random random
	) {
		this.enclosedCycles = enclosedCycles;
		this.treeCreator = treeCreator;
		this.deadEnds = deadEnds;
		this.random = random;
	}

	Set<InnerTree> seedTreesAtUnderconnectedCycles(
	) {
		return enclosedCycles.stream()
			.flatMap(this::missingTrees)
			.collect(toLinkedHashSet());
	}

	private Stream<InnerTree> missingTrees(OrientedCycle cycle) {
		assert enclosedCycles.contains(cycle);
		Set<Point2D> connections = pointsOfContactWithForest(cycle);
		if (connections.size() == 1) {
			Point2D theOnlyConnection = connections.iterator().next();
			assert theOnlyConnection != null;
			assert cycle.graph().vertexSet().contains(theOnlyConnection);
			return Stream.of(
				treeCreator.createTreeOnEnclosedCycle(
					cycle,
					oppositePointOnCycle(cycle, theOnlyConnection)
				)
			);
		} else if (connections.size() == 0) {
			Function<Point2D, Double> getCoordinate = random.nextBoolean() ? Point2D::getX : Point2D::getY;
			Comparator<Point2D> coordinateComparator = (a, b) ->
				(int) Math.signum(getCoordinate.apply(a) - getCoordinate.apply(b));
			Point2D leastPoint = cycle.graph()
				.vertexSet()
				.stream()
				.max(coordinateComparator)
				.get();
			Point2D greatestPoint = cycle.graph()
				.vertexSet()
				.stream()
				.min(coordinateComparator)
				.get();
			return Stream.of(
				treeCreator.createTreeOnEnclosedCycle(cycle, leastPoint),
				treeCreator.createTreeOnEnclosedCycle(cycle, greatestPoint)
			);
		} else {
			return Stream.empty();
		}
	}

	private Set<Point2D> pointsOfContactWithForest(OrientedCycle cycle) {
		return Sets.intersection(
			cycle.graph().vertexSet(),
			deadEnds
		);
	}

	private Point2D oppositePointOnCycle(OrientedCycle cycle, Point2D anyConnection) {
		return cycle.graph()
			.vertexSet()
			.stream()
			.max((a, b) -> {
				double distanceSquaredA = anyConnection.squaredDistanceTo(a);
				double distanceSquaredB = anyConnection.squaredDistanceTo(b);
				return (int) Math.signum(distanceSquaredA - distanceSquaredB);
			}).get();
	}
}
