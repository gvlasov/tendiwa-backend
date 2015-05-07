package org.tendiwa.geometry;

import org.junit.Test;
import org.tendiwa.geometry.extensions.PointTrail;

import static org.junit.Assert.*;

public class PolygonTest {

	@Test
	public void clockwiseSimpleRectangle() {
		assertTrue(
			new BasicPolygon(
				new PointTrail(20, 20)
					.moveByX(20)
					.moveByY(20)
					.moveByX(-20)
					.polygon()
			).isClockwise()
		);
	}

	@Test
	public void counterClockwiseSimpleRectangle() {
		assertFalse(
			new BasicPolygon(
				new PointTrail(40, 20)
					.moveByX(-35)
					.moveByY(20)
					.moveByX(35)
					.polygon()
			).isClockwise());
	}

	@Test
	public void clockwiseConcave() {
		assertTrue(
			new BasicPolygon(
				new PointTrail(20, 20)
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
					.polygon()
			).isClockwise()
		);
	}

	@Test
	public void areaOfSquare() {
		double squareSide = 100;
		assertEquals(
			squareSide * squareSide,
			new Square(squareSide).area(),
			Vectors2D.EPSILON
		);
	}

	@Test
	public void areaOfParallelogram() {
		double a = 100;
		assertEquals(
			a * a,
			new Parallelogram(a).area(),
			Vectors2D.EPSILON
		);
	}

	@Test
	public void areaOfTriangle() {
		double a = 100;
		assertEquals(
			a * a / 2,
			new IsoscelesTriangle(a, a).area(),
			Vectors2D.EPSILON
		);
	}
}