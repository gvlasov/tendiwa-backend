package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.geometry.Segment2D;

final class SegmentConflict {
	private final Segment2D commonSegment;
	private final RasterizedPolygon polygon1;
	private final RasterizedPolygon polygon2;

	SegmentConflict(
		Segment2D commonSegment,
		RasterizedPolygon polygon1,
		RasterizedPolygon polygon2
	) {
		assert polygon1.polygon().toSegments().contains(commonSegment)
			|| polygon1.polygon().toSegments().contains(commonSegment.reverse())
			&& polygon2.polygon().toSegments().contains(commonSegment)
			|| polygon2.polygon().toSegments().contains(commonSegment.reverse());
		this.commonSegment = commonSegment;
		this.polygon1 = polygon1;
		this.polygon2 = polygon2;
	}

	void resolve() {

	}
}
