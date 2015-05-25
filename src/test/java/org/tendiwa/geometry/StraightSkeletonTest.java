package org.tendiwa.geometry;

import org.junit.Test;
import org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle2D;

public final class StraightSkeletonTest {
	@Test
	public void vanishDepth() {
		assertEquals(
			10,
			new SuseikaStraightSkeleton(rectangle2D(20, 20))
				.vanishDepth(),
			Vectors2D.EPSILON
		);
	}
}