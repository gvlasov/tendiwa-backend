package org.tendiwa.geometry.extensions.straightSkeleton;

import org.junit.Test;
import org.tendiwa.geometry.*;

import static org.junit.Assert.*;

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
			.map(Polygon::area)
			.reduce(Double::sum)
			.get();
		assertTrue(shrinkedArea < polygon.area());
	}
}