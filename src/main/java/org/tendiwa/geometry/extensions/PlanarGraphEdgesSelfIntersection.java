package org.tendiwa.geometry.extensions;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;

public class PlanarGraphEdgesSelfIntersection {
    /**
     * Tests if none of the edges of a planar graph don't overlap.
     *
     * @param graph
     *         A planar graph.
     * @return true if {@code graph} has no two edges that overlap, false if it has at least two edges that overlap.
     */
    public static boolean test(UndirectedGraph<Point2D, Segment2D> graph) {
        Generator<Segment2D> allPairsOfEdges = generateAllPairsOfEdges(graph);
        for (ICombinatoricsVector<Segment2D> pair : allPairsOfEdges) {
            Segment2D e1 = pair.getVector().get(0);
            Segment2D e2 = pair.getVector().get(1);
            if (e1.intersects(e2)) {
                return false;
            }
        }
        return true;
    }

    private static Generator<Segment2D> generateAllPairsOfEdges(UndirectedGraph<Point2D, Segment2D> graph) {
        return Factory.createSimpleCombinationGenerator(Factory.createVector(graph.edgeSet()), 2);
    }

    /**
     * Finds all intersection points between graph's edges.
     *
     * @param graph
     * @return
     */
    public static ImmutableCollection<Point2D> findAllIntersections(
            UndirectedGraph<Point2D, Segment2D> graph
    ) {
        ImmutableSet.Builder<Point2D> builder = ImmutableSet.builder();
        Generator<Segment2D> allPairsOfEdges = generateAllPairsOfEdges(graph);
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
