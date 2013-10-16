package tests;

import org.junit.Test;
import tendiwa.core.*;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static tendiwa.core.DSL.*;

public class EhancedRectangleTest {

@Test
public void testRectangleContainingPoints() {
	ArrayList<Point> points = new ArrayList<>();
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

@Test
public void adjacencySegmentTest() {
	RectangleSystemBuilder builder = builder(0)
		.place("one", rectangle(10, 20), atPoint(0, 0))
		.place("two", rectangle(20, 15), near(LAST_RECTANGLE).fromSide(E).align(S).shift(4));
	EnhancedRectangle rec1 = (EnhancedRectangle) builder.getByName("one");
	EnhancedRectangle rec2 = (EnhancedRectangle) builder.getByName("two");
	assertEquals(rec1.getIntersectionSegment(rec2) , new Segment(11, 9, 11, Orientation.VERTICAL));
}

}
