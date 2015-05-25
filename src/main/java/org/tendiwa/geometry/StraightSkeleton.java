package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;

import java.util.List;
import java.util.Set;

public interface StraightSkeleton {
	UndirectedGraph<Point2D, Segment2D> graph();

	ImmutableSet<Polygon> cap(double depth);

	Set<StraightSkeletonFace> faces();

	List<Segment2D> originalEdges();

	/**
	 * Depth at which {@link #cap(double)} of a StraightSkeleton becomes empty (doesn't produce any shrinked polygon
	 * because depth is too large).
	 *
	 * @return
	 */
	default double vanishDepth() {
		double maxDepth = 0;
		for (StraightSkeletonFace face : faces()) {
			Segment2D front = face.front();
			for (Point2D point : face) {
				double distance = point.distanceToLine(front);
				if (distance > maxDepth) {
					maxDepth = distance;
				}
			}
		}
		return maxDepth;
	}
}
