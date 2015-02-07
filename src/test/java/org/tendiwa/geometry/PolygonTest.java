package org.tendiwa.geometry;

import org.junit.Test;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPolygon;
import org.tendiwa.geometry.extensions.PointTrail;

import java.awt.Color;
import java.util.List;

import static org.junit.Assert.*;

public class PolygonTest {

	@Test
	public void isClockwiseSimpleRectangle() {
		List<Point2D> points = new PointTrail(20, 20)
			.moveByX(20)
			.moveByY(20)
			.moveByX(-20)
			.points();
		assertTrue(new Polygon(points).isClockwise());
		points = new PointTrail(40, 20)
			.moveByX(-35)
			.moveByY(20)
			.moveByX(35)
			.points();
		assertFalse(new Polygon(points).isClockwise());
	}
	@Test
	public void isClockwiseConcave() {
		List<Point2D> points = new PointTrail(20, 20)
			.moveByX(100)
			.moveByY(10)
			.moveByX(-90)
			.moveByY(15)
			.moveByX(70)
			.moveByY(5)
			.moveByX(-70)
			.moveByY(5)
			.moveByX(120)
			.moveByY(-40)
			.moveByX(20)
			.moveByY(50)
			.moveByX(-145)
			.points();
		assertTrue(new Polygon(points).isClockwise());
	}
}