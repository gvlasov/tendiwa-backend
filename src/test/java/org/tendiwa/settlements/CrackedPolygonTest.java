package org.tendiwa.settlements;

import org.junit.Test;
import org.tendiwa.geometry.CrackedPolygon;
import org.tendiwa.geometry.extensions.PointTrail;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class CrackedPolygonTest {
	@Test
	public void splitLineIntersectingVertices() {
		new CrackedPolygon(
			new PointTrail(20, 20)
				.moveBy(0, 60)
				.moveBy(20, -30)
				.moveBy(10, 30)
				.moveBy(0, -30)
				.moveBy(10, 0)
				.moveBy(0, 30)
				.moveBy(10, -30)
				.moveBy(10, 30)
				.moveBy(10, -30)
				.moveBy(10, 30)
				.moveBy(0, -30)
				.moveBy(10, 0)
				.moveBy(0, 30)
				.moveBy(10, 0)
				.moveBy(0, -30)
				.moveBy(10, 0)
				.moveBy(0, 30)
				.moveBy(10, -30)
				.moveBy(-30, -20)
					//			.moveBy(0, 30)
					//			.moveBy(10, 0)
					//			.moveBy(0, -40)
					//			.moveBy(-40, -10)
				.moveBy(-30, 10)
				.moveBy(-30, 10)
				.moveBy(-10, -10)
				.polygon(),
			rectangle(10, 10),
			0
		).pieces();
	}

	@Test
	public void splitLineSingleIntersectingVertices() {
		new CrackedPolygon(
			new PointTrail(20, 20)
				.moveBy(0, 40)
				.moveBy(20, -20)
				.polygon(),
			rectangle(20, 20),
			0
		).pieces();
	}

}