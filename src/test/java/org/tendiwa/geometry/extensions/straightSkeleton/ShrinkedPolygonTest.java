package org.tendiwa.geometry.extensions.straightSkeleton;

import org.junit.Test;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawablePolygon;
import org.tendiwa.geometry.*;

import java.awt.Color;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class ShrinkedPolygonTest {
	@Test
	public void shrinksIntoNonExistence() {
		int sideOfSquare = 10;
		assertEquals(
			0,
			new ShrinkedPolygon(
				new Square(sideOfSquare),
				sideOfSquare
			).stream().count()
		);
	}

	@Test
	public void shrinksIntoSinglePolygon() {
		assertEquals(
			1,
			new ShrinkedPolygon(
				new Parallelogram(100),
				5
			).stream().count()
		);
	}

	@Test
	public void shrinksIntoTwoPolygons() {
		assertEquals(
			2,
			new ShrinkedPolygon(
				new Narrowing(),
				1
			).stream().count()
		);
	}

	@Test
	public void shrinkedPolygonHasSmallerArea() {
		Polygon polygon = new Narrowing();
		double shrinkedArea = new ShrinkedPolygon(polygon, 1)
			.stream()
			.mapToDouble(Polygon::area)
			.sum();
		assertTrue(shrinkedArea < polygon.area());
	}
}