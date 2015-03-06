package org.tendiwa.geometry.extensions.straightSkeleton;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Optional;

public interface Penetrable {
	void add(Point2D poll, Point2D poll1);

	Optional<Point2D> obtainItersectionPoint(Segment2D intersected, Segment2D intersecting);
	double depth();
}
