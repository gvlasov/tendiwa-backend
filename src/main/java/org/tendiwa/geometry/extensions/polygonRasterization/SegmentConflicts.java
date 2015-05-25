package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.geometry.Segment2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class SegmentConflicts extends ArrayList<SegmentConflict> {
	private final Collection<MutableRasterizedPolygon> rasterizations;

	SegmentConflicts(
		Collection<MutableRasterizedPolygon> rasterizations
	) {
		this.rasterizations = rasterizations;
		findConflicts();
	}

	private void findConflicts() {
		Map<Segment2D, RasterizedPolygon> foundPolygons
			= new HashMap<>();
		for (RasterizedPolygon rasterizedPolygon : rasterizations) {
			for (Segment2D segment : rasterizedPolygon.polygon().toSegments()) {
				Segment2D unifiedSegment = unifySegment(segment);
				if (foundPolygons.containsKey(unifiedSegment)) {
					RasterizedPolygon previousPolygon
						= foundPolygons.get(segment);
					this.add(
						new SegmentConflict(
							unifiedSegment,
							previousPolygon,
							rasterizedPolygon
						)
					);
				} else {
					foundPolygons.put(
						unifiedSegment,
						rasterizedPolygon
					);
				}
			}
		}
	}

	private Segment2D unifySegment(Segment2D segment) {
		if (segment.startX() < segment.endX()) {
			return segment;
		}
		if (
			segment.startX() == segment.endX()
				&& segment.startY() < segment.endY()
			) {
			return segment;
		}
		return segment.reverse();
	}
}
