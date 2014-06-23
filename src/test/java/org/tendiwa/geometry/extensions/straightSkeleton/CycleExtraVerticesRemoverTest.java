package org.tendiwa.geometry.extensions.straightSkeleton;

import org.junit.Assert;
import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.PointTrail;

import java.util.ArrayList;
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
	@Test
	public void extraVerticesAround0Index() {
		List<Point2D> points = new ArrayList<Point2D>() {{
			add(new Point2D(50.90432739632178,91.56203161313043));
			add(new Point2D(79.26153197706417,72.70133427495472));
			add(new Point2D(98.39332248039173,101.46612952993435));
			add(new Point2D(70.0500669002952,120.3175492300903));
			add(new Point2D(59.21137672335236,104.05174463938852));
		} };
		List<Point2D> nececcaryPoints = CycleExtraVerticesRemover.removeVerticesOnLineBetweenNeighbors(points);
		Assert.assertEquals(nececcaryPoints.size(), 4);
	}
}
