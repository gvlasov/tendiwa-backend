package org.tendiwa.geometry.extensions.straightSkeleton;

import org.junit.Assert;
import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.PointTrail;

import java.util.List;

public class CycleExtraVerticesRemoverTest {
	@Test
	public void severalConsecutiveExtraVertices() {
		List<Point2D> points = new PointTrail(20, 50)
			.moveBy(20, 0)
			.moveBy(20, 0)
			.moveBy(20, 0)
			.moveBy(20, 0)
			.moveBy(20, 20)
			.points();
		List<Point2D> necessaryPoints = CycleExtraVerticesRemover.removeVerticesOnLineBetweenNeighbors(points);
		Assert.assertEquals(necessaryPoints.size(), 3);
	}
}
