package org.tendiwa.graphs;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

public class PlanarGraphEdgesNonOverlapping {
    /**
     * Tests if none of the edges of a planar graph don't overlap.
     *
     * @param graph
     *         A planar graph.
     * @return true if {@code graph} has no two edges that overlap, false if it has at least two edges that overlap.
     */
    public static boolean test(UndirectedGraph<Point2D, Line2D> graph) {
        for (Line2D e1 : graph.edgeSet()) {
            for (Line2D e2 : graph.edgeSet()) {
                if (e1.start.equals(e2.start) || e1.start.equals(e2.end) || e1.end.equals(e2.start) || e1.end.equals(e2.end)) {
                   continue;
                }
                if (java.awt.geom.Line2D.linesIntersect(
                        e1.start.x,
                        e1.start.y,
                        e1.end.x,
                        e1.end.y,
                        e2.start.x,
                        e2.start.y,
                        e2.end.x,
                        e2.end.y

                )) {
                    return false;
                }
            }

        }
        return true;

    }
}
