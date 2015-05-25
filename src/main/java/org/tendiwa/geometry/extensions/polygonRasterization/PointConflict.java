package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.geometry.Point2D;

import java.util.Collection;

final class PointConflict {
	private final Point2D point;
	private final Collection<RasterizedPolygon> conflictingPolygons;
	PointConflict(
		Point2D point,
		Collection<RasterizedPolygon> conflictingPolygons
	) {
		this.point = point;
		this.conflictingPolygons = conflictingPolygons;
	}
	void resolve() {

	}
}
