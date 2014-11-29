package org.tendiwa.settlements.networks;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.MinimalCycle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Coordinates of {@link org.tendiwa.settlements.networks.RoadsPlanarGraphModel#originalRoadGraph}'s
 * {@link org.tendiwa.graphs.MinimalCycle} vertices sorted in a clockwise or counter-clockwise
 * order.
 */
final class CycleRing {
	private final boolean isCycleClockwise;
	/**
	 * Checks if {@link org.tendiwa.geometry.Segment2D#start} of an edge appears earlier in a {@link #ring} than
	 * {@link org.tendiwa.geometry.Segment2D#end}.
	 *
	 * @return true if {@code edge.start} appears earlier than {@code edge.end}, false otherwise.
	 */
	private final Coordinate[] ring;

	boolean isStartBeforeEndInRing(Segment2D edge) {
		Coordinate start = new Coordinate(edge.start.x, edge.start.y);
		Coordinate end = new Coordinate(edge.end.x, edge.end.y);
		assert start != end;
		for (int i = 0; i < ring.length; i++) {
			if (ring[i].equals(start)) {
				if (ring[i + 1].equals(end)) {
					return true;
				} else {
					assert ring[i == 0 ? ring.length - 2 : i - 1].equals(end);
					return false;
				}
			}
		}
		throw new RuntimeException(start + " is not before or after " + end);
	}

	CycleRing(MinimalCycle<Point2D, Segment2D> originalMinimalCycle) {
		Coordinate[] coordinates = pointListToCoordinateArray(originalMinimalCycle.vertexList());
		// TODO: Are all cycles counter-clockwise? (because of the MCB algorithm)
		if (!CGAlgorithms.isCCW(coordinates)) {
			List<Coordinate> list = Arrays.asList(coordinates);
			Collections.reverse(list);
			ring = list.toArray(new Coordinate[list.size()]);
		} else {
			ring = coordinates;
		}
		isCycleClockwise = false;
	}

	/**
	 * Transforms a list of {@link org.tendiwa.geometry.Point2D}s to an array of {@link
	 * com.vividsolutions.jts.geom.Coordinate}s.
	 *
	 * @param points
	 * 	A list of points.
	 * @return An array of coordinates.
	 */
	private Coordinate[] pointListToCoordinateArray(List<Point2D> points) {
		List<Coordinate> collect = points.stream()
			.map(a -> new Coordinate(a.x, a.y))
			.collect(Collectors.toList());
		collect.add(new Coordinate(points.get(0).x, points.get(0).y));
		return collect.toArray(new Coordinate[points.size()]);
	}

	/**
	 * Multiplier that describes which way the ring goes along one of its edges.
	 *
	 * @param edge
	 * 	An edge with its endpoints being two consecutive nodes of this
	 * 	{@link org.tendiwa.settlements.networks.CycleRing}.
	 * @return -1 or 1
	 */
	public double getDirection(Segment2D edge) {
		// TODO: Is this cycle always counter-clockwise because isCycleClockwise is always false?
		return (isCycleClockwise ? -1 : 1)
			* (isStartBeforeEndInRing(edge) ? 1 : -1);
	}
}
