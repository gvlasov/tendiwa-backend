package org.tendiwa.geometry.extensions.straightSkeleton;

import org.junit.Test;
import org.tendiwa.geometry.BasicBisector;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Vector2D;

public class BisectorTest {
	@Test
	public void parallelVectors() throws Exception {
		Vector2D cw = new Point2D(20, 0);
		Vector2D ccw = new Point2D(0, 20);
		assert ccw.makesReflexAngle(cw);
		new BasicBisector(cw, ccw).asInbetweenVector();
	}
}