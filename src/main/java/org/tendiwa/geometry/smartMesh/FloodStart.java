package org.tendiwa.geometry.smartMesh;


import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Recs2D;
import org.tendiwa.geometry.Sector;
import org.tendiwa.geometry.Segment2D;

import java.util.Optional;

final class FloodStart {
	final Sector rootSector;
	final Optional<Segment2D> holdingSegment;
	final Ray rootRay;

	FloodStart(
		Ray root,
		Sector rootSector,
		Optional<Segment2D> holdingSegment
	) {
		assertPointIsStrictlyInsideSegment(root.start, holdingSegment);
		this.rootSector = rootSector;
		this.holdingSegment = holdingSegment;
		this.rootRay = root;
	}

	private void assertPointIsStrictlyInsideSegment(Point2D point, Optional<Segment2D> holdingSegment) {
		assert !holdingSegment.isPresent()
			|| Recs2D.boundingBox(holdingSegment.get()).strictlyContains(point);
	}
}
