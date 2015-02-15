package org.tendiwa.settlements.networks;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.ShreddedSegment2D;

import java.util.*;
import java.util.stream.Collectors;

public class CycleWithStartingPoints {

	private final FullNetwork fullNetwork;
	private final NetworkGenerationParameters networkGenerationParameters;

	public CycleWithStartingPoints(
		FullNetwork fullNetwork,
		NetworkGenerationParameters networkGenerationParameters
	) {
		this.fullNetwork = fullNetwork;
		this.networkGenerationParameters = networkGenerationParameters;
	}

	Set<Point2D> snapAndInsertStartingPoints(Map<Segment2D, List<Point2D>> pointsOnPolygonBorder) {
		snapStartingPoints(pointsOnPolygonBorder);

		pointsOnPolygonBorder.entrySet()
			.stream()
			.map(e -> new ShreddedSegment2D(e.getKey(), e.getValue()))
			.forEach(fullNetwork::splitEdge);

		return pointsOnPolygonBorder.values()
			.stream()
			.flatMap(Collection::stream)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	void snapStartingPoints(Map<Segment2D, List<Point2D>> pointsOnPolygonBorder) {
		for (Map.Entry<Segment2D, List<Point2D>> entry : pointsOnPolygonBorder.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i++) {
				Point2D originalPoint = entry.getValue().get(i);
				Point2D snappedPoint = snapToSegmentEnd(originalPoint, entry.getKey());
				if (snappedPoint != originalPoint) {
					entry.getValue().set(i, snappedPoint);
				}
			}
		}
	}

	/**
	 * If {@code startingPoint} can be snapped to one or both ends of {@code edge}, returns the closest of ends.
	 * Otherwise returns {@code startingPoint}.
	 *
	 * @param startingPoint
	 * 	A point to snap.
	 * @param edge
	 * 	An edge to whose ends to snap.
	 * @return Closest snappable end of {@code edge} or {@code startingPoint} if it is not close enough to either of
	 * edges.
	 */
	Point2D snapToSegmentEnd(Point2D startingPoint, Segment2D edge) {
		double toStart = startingPoint.squaredDistanceTo(edge.start);
		double toEnd = startingPoint.squaredDistanceTo(edge.end);
		// TODO: snapSize should be squared here?
		if (toStart < toEnd) {
			if (toStart < networkGenerationParameters.snapSize) {
				return edge.start;
			}
		} else {
			if (toEnd < networkGenerationParameters.snapSize) {
				return edge.end;
			}
		}
		return startingPoint;
	}
}