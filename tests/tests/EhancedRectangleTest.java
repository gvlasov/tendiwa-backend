package tests;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.junit.Test;

import tendiwa.geometry.Directions;
import tendiwa.geometry.EnhancedRectangle;

public class EhancedRectangleTest {

	@Test
	public void testRectangleContainingPoints() {
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(new Point(1, 12));
		points.add(new Point(12, 14));
		points.add(new Point(23, 29));
		points.add(new Point(23, 13));
		points.add(new Point(22, 0));
		Rectangle r = EnhancedRectangle.rectangleContainingAllPonts(points);
		assertEquals(r, new Rectangle(1, 0, 23, 30));
	}
	@Test
	public void testGrowFromPoint() {
		assertEquals(
			EnhancedRectangle.growFromPoint(0, 0, Directions.SE, 10, 10),
			new Rectangle(0, 0, 10, 10));
		assertEquals(
			EnhancedRectangle.growFromPoint(20, 20, Directions.SW, 10, 10),
			new Rectangle(11, 20, 10, 10));
		assertEquals(
			EnhancedRectangle.growFromPoint(20, 20, Directions.NW, 10, 10),
			new Rectangle(11, 11, 10, 10));
		assertEquals(
			EnhancedRectangle.growFromPoint(20, 20, Directions.NE, 10, 10),
			new Rectangle(20, 11, 10, 10));

	}

}
