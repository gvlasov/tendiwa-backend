package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.Point2DBounds;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

final class LatticeStartPointsFinder {
	private final Random random;
	private final UndirectedGraph<Point2D, Segment2D> graph;
	private final double interlineSpace;
	private final double interlineSpaceDeviation;
	private Collection<Segment2D> horizontal;
	private Collection<Segment2D> vertical;

	LatticeStartPointsFinder(
		UndirectedGraph<Point2D, Segment2D> graph,
		double interlineSpace,
		double interlineSpaceDeviation,
		Random random
	) {
		if (interlineSpaceDeviation > interlineSpace) {
			throw new IllegalArgumentException(
				"Interline space deviation can't be greater than interlince space"
			);
		}
		this.graph = graph;
		this.interlineSpace = interlineSpace;
		this.interlineSpaceDeviation = interlineSpaceDeviation;
		this.random = new Random(random.nextInt());
	}

	private Collection<Point2D> compute() {
		computeSegmentsOfLattice();
		return null;
	}

	/**
	 * Creates two collections: {@link #vertical} and of {@link #horizontal} segments forming the lattice that
	 * intersects {@link #graph} edges.
	 */
	private void computeSegmentsOfLattice(
	) {
		Point2DBounds bounds = new Point2DBounds(graph.vertexSet());
		double minPartitionSize = interlineSpace - interlineSpaceDeviation;
		int maxHorizontalLines = maxNumberOfPartitions(bounds.maxY - bounds.minY, minPartitionSize);
		int maxVerticalLines = maxNumberOfPartitions(bounds.maxX - bounds.minX, minPartitionSize);

		horizontal = new ArrayList<>(maxHorizontalLines);
		vertical = new ArrayList<>(maxVerticalLines);
		createVerticalSegments(bounds);
		createHorizontalSegments(bounds);
	}

	private void createHorizontalSegments(Point2DBounds bounds) {
		for (double y = bounds.minY; y < bounds.maxY; y += getRandomDelta()) {
			horizontal.add(Segment2D.create(bounds.minX, y, bounds.maxX, y));
		}
	}

	private void createVerticalSegments(Point2DBounds bounds) {
		for (double x = bounds.minX; x < bounds.maxX; x += getRandomDelta()) {
			vertical.add(Segment2D.create(x, bounds.minY, x, bounds.maxY));
		}
	}

	private static int maxNumberOfPartitions(double distance, double minPartitionSize) {
		return (int) Math.ceil(distance / minPartitionSize);
	}

	public double getRandomDelta() {
		return (random.nextDouble() - 0.5) * interlineSpaceDeviation + interlineSpace;
	}
}

