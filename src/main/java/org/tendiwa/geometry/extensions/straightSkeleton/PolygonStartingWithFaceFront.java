package org.tendiwa.geometry.extensions.straightSkeleton;

import lombok.Lazy;
import org.tendiwa.geometry.*;

import java.util.List;

public final class PolygonStartingWithFaceFront extends BasicPolygon implements StraightSkeletonFace {
	public PolygonStartingWithFaceFront(List<Point2D> points) {
		super(points);
	}

	@Lazy
	@Override
	public Segment2D front() {
		return new BasicSegment2D(get(0), get(size() - 1));
	}

}
