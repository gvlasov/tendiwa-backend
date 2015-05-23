package org.tendiwa.geometry.extensions.polygonRasterization;

import org.tendiwa.collections.SuccessiveTuples;
import org.tendiwa.geometry.ConstructedPolygon;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;

/**
 * Creates a copy of another polygon, but without redundant vertices.
 * <p>
 * A redundant vertex is one that has the same y-coordinate as both its neighbors.
 */
final class CollapsedHorizontalChainsPolygon extends ConstructedPolygon {

	CollapsedHorizontalChainsPolygon(Polygon polygon) {
		super(polygon.size());
		SuccessiveTuples.forEachLooped(
			polygon,
			(a, b, c) -> {
				if (!haveSameYCoord(a, b, c)) {
					CollapsedHorizontalChainsPolygon.this.add(b);
				}
			}
		);
	}

	private boolean haveSameYCoord(Point2D a, Point2D b, Point2D c) {
		return a.y() == b.y() && b.y() == c.y();
	}
}
