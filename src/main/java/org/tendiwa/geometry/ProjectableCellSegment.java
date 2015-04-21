package org.tendiwa.geometry;

import org.tendiwa.core.meta.Range;

import java.util.Optional;

import static org.tendiwa.geometry.GeometryPrimitives.orthoCellSegment;

public final class ProjectableCellSegment extends OrthoCellSegment_Wr {

	public ProjectableCellSegment(OrthoCellSegment segment) {
		super(segment);
	}

	public OrthoCellSegment projectOn(OrthoCellSegment segment) {
		if (orientation() != segment.orientation()) {
			throw new IllegalArgumentException(
				"Segment can be projected only on another segment with the same orientation"
			);
		}
		Optional<Range> commonMaybe = segment.intersection(this);
		if (!commonMaybe.isPresent()) {
			throw new IllegalArgumentException(
				"Segments don't share any space on " + orientation() + " axis"
			);
		}
		Range common = commonMaybe.get();
		int startX = orientation().isHorizontal() ?
			common.min() :
			segment.getX();
		int startY = orientation().isVertical() ?
			common.min() :
			segment.getY();
		return orthoCellSegment(
			startX,
			startY,
			common.length(),
			orientation()
		);
	}

}
