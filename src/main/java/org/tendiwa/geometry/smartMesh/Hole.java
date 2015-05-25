package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.extensions.RandomCoordinateComparator;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.graphs2d.BasicSplittableCycle2D;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.tendiwa.collections.Collectors.toImmutableSet;

public final class Hole extends BasicSplittableCycle2D implements MeshedNetworkCycle {
	Hole(Polygon polygon) {
		super(polygon);
	}

	Stream<FloodStart> missingTreesStream(Graph2D fullGraph) {
		Set<Point2D> connections = pointsOfContactWithFullGraph(fullGraph);
		if (connections.size() == 1) {
			Point2D theOnlyConnection = connections.iterator().next();
			return createMissingFloodStart(theOnlyConnection);
		} else if (connections.size() == 0) {
			return create2OppositeMissingFloodStarts();
		} else {
			return Stream.empty();
		}
	}

	private Set<Point2D> pointsOfContactWithFullGraph(Graph2D fullGraph) {
		return vertexSet()
			.stream()
			.filter(v -> fullGraph.degreeOf(v) > 2)
			.collect(toImmutableSet());
	}

	private FloodStart createFlood(Point2D root) {
		return new FloodStart(
			deviatedAngleBisector(root, false),
			new OrientedCycleSector(this, root, false),
			Optional.empty()
		);
	}


	private Stream<FloodStart> createMissingFloodStart(Point2D oppositePoint) {
		assert oppositePoint != null;
		assert vertexSet().contains(oppositePoint);
		return Stream.of(
			createFlood(
				findOppositePointOnCycle(oppositePoint)
			)
		);
	}

	private Stream<FloodStart> create2OppositeMissingFloodStarts() {
		Comparator<Point2D> coordinateComparator = new RandomCoordinateComparator(new Random());
		Point2D leastPoint =
			vertexSet()
				.stream()
				.max(coordinateComparator)
				.get();
		Point2D greatestPoint =
			vertexSet()
				.stream()
				.min(coordinateComparator)
				.get();
		return Stream.of(
			createFlood(leastPoint),
			createFlood(greatestPoint)
		);
	}

	private Point2D findOppositePointOnCycle(Point2D point) {
		return vertexSet()
			.stream()
			.max((a, b) -> {
				double distanceSquaredA = point.squaredDistanceTo(a);
				double distanceSquaredB = point.squaredDistanceTo(b);
				return (int) Math.signum(distanceSquaredA - distanceSquaredB);
			})
			.get();
	}
}
