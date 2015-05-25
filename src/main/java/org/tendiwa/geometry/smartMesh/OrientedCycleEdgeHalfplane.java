package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.*;

final class OrientedCycleEdgeHalfplane implements Sector {
	private final Vector2D cw;
	private final Vector2D ccw;

	OrientedCycleEdgeHalfplane(
		MeshedNetworkCycle cycle,
		SplitSegment2D splitSegment,
		boolean inward
	) {
		Point2D cwPoint, ccwPoint;
		if (cycle.isClockwise(splitSegment.originalSegment()) ^ inward) {
			cwPoint = splitSegment.originalStart();
			ccwPoint = splitSegment.originalEnd();
		} else {
			cwPoint = splitSegment.originalEnd();
			ccwPoint = splitSegment.originalStart();
		}
		Point2D rayStart = splitSegment.middlePoint();
		this.cw = cwPoint.subtract(rayStart);
		this.ccw = ccwPoint.subtract(rayStart);
	}

	@Override
	public boolean contains(Vector2D vector) {
		return vector.isBetweenVectors(cw, ccw);
	}
}
