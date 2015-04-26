package org.tendiwa.geometry.extensions.straightSkeleton;

import org.junit.Test;
import org.tendiwa.geometry.BasicBisector;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Vector2D;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class BisectorTest {
	@Test
	public void parallelVectors() throws Exception {
		Vector2D cw = point2D(20, 0);
		Vector2D ccw = point2D(0, 20);
		assert ccw.makesReflexAngle(cw);
		new BasicBisector(cw, ccw).asInbetweenVector();
	}
}