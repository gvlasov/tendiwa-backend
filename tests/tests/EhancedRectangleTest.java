package tests;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

import org.junit.Test;

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

}
