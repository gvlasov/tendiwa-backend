package org.tendiwa.geometry.extensions.polygonRasterization;

import org.junit.Test;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Rectangle2D;
import org.tendiwa.geometry.extensions.PointTrail;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle2D;

public final class CollapsedHorizontalChainsPolygonTest {
	@Test
	public void noRedundantVertices() {
		Rectangle2D rec = rectangle2D(10, 10);
		assertEquals(
			4,
			new CollapsedHorizontalChainsPolygon(rec).size()
		);
	}

	@Test
	public void oneRedundantVertex() {
		Polygon stupidRectangle = new PointTrail(0, 0)
			.moveByX(100)
			.moveByY(100)
			.moveByX(-50)
			.moveByX(-50)
			.polygon();
		assertEquals(
			stupidRectangle.size() - 1,
			new CollapsedHorizontalChainsPolygon(stupidRectangle).size()
		);
	}

}