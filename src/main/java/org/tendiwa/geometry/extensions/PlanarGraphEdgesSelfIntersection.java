package org.tendiwa.geometry.extensions;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Collection;

public class PlanarGraphEdgesSelfIntersection {
	/**
	 * Tests if none of the edges of a planar graph don't overlap.
	 *
	 * @param edges
	 * 	Edges of a planar graph.
	 * @return true if {@code graph} has no two edges that overlap, false if it has at least two edges that overlap.
	 */
	public static boolean test(Collection<Segment2D> edges) {
		Generator<Segment2D> allPairsOfEdges = generateAllPairsOfEdges(edges);
		for (ICombinatoricsVector<Segment2D> pair : allPairsOfEdges) {
			Segment2D e1 = pair.getVector().get(0);
			Segment2D e2 = pair.getVector().get(1);
			if (e1.intersects(e2)) {
				return false;
			}
		}
		return true;
	}

	private static Generator<Segment2D> generateAllPairsOfEdges(Collection<Segment2D> edges) {
		return Factory.createSimpleCombinationGenerator(Factory.createVector(edges), 2);
	}

	/**
	 * Finds all intersection points between graph's edges.
	 *
	 * @param edges
	 * 	Edges of a planar graph.
	 * @return
	 */
	public static ImmutableCollection<Point2D> findAllIntersections(
		Collection<Segment2D> edges
	) {
		ImmutableSet.Builder<Point2D> builder = ImmutableSet.builder();
		Generator<Segment2D> allPairsOfEdges = generateAllPairsOfEdges(edges);
		for (ICombinatoricsVector<Segment2D> pair : allPairsOfEdges) {
			Segment2D e1 = pair.getVector().get(0);
			Segment2D e2 = pair.getVector().get(1);
			Point2D intersection = e1.intersection(e2);
			if (intersection != null) {
				builder.add(intersection);
			}
		}
		return builder.build();
	}
}
