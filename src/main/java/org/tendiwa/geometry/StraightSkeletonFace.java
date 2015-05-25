package org.tendiwa.geometry;

/**
 * A {@link Polygon} formed by {@link StraightSkeleton}'s edges and edges of the original skeletonized polygon.
 * <p>
 * Has exactly 1 edge from the set of original polygon's edges.
 */
public interface StraightSkeletonFace extends Polygon {
	/**
	 * A segment of face that it shared with the skeletonized polygon.
	 *
	 * @return
	 */
	Segment2D front();
}
