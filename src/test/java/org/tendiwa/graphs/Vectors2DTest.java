package org.tendiwa.graphs;

import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.Vectors2D.*;

public class Vectors2DTest {
	@Test
	public void testPerpDotProduct() {
		Segment2D a = new Segment2D(
			new Point2D(20, 60),
			new Point2D(60, 60)
		);
		Segment2D b = new Segment2D(
			new Point2D(60, 60),
			new Point2D(80, 80)
		);
		double pdp = perpDotProduct(
			new double[]{a.dx(), a.dy()},
			new double[]{b.dx(), b.dy()}
		);
		System.out.println(pdp);
	}

	@Test
	public void testAngleBetweenVectors() {
		double[] a = new double[]{1, 0};
		double[] b = new double[]{0, 1};
		assertEquals(angleBetweenVectors(a, b, false), Math.PI / 2, 1e-8);
		assertEquals(angleBetweenVectors(b, a, false), Math.PI * 2 - Math.PI / 2, 1e-8);
		assertEquals(angleBetweenVectors(a, b, true), Math.PI * 2 - Math.PI / 2, 1e-8);
		assertEquals(angleBetweenVectors(b, a, true), Math.PI / 2, 1e-8);
		a = new double[]{1, 1};
		b = new double[]{1, 0};
		assertEquals(angleBetweenVectors(a, b, true), Math.PI / 4, 1e-8);
		assertEquals(angleBetweenVectors(a, b, false), Math.PI * 2 - Math.PI / 4, 1e-8);
		a = new double[]{1, 1};
		b = new double[]{-1, -1};
		assertEquals(angleBetweenVectors(a, b, true), Math.PI, 1e-8);
		assertEquals(angleBetweenVectors(a, b, false), Math.PI, 1e-8);
		a = new double[]{-1, 1};
		b = new double[]{-1, -1};
		assertEquals(angleBetweenVectors(a, b, true), Math.PI / 2 * 3, 1e-8);
	}

	@Test
	public void testParallel() {
		double[] a = new double[]{0, -1};
		double[] b = new double[]{1e-5, 4.5678};
		assertFalse(areParallel(a, b));

		a = new double[]{0, -1};
		b = new double[]{0, 4.5678};
		assertTrue(areParallel(a, b));

		a = new double[]{1, 1};
		b = new double[]{-1, -1};
		assertTrue(areParallel(a, b));
	}
}
