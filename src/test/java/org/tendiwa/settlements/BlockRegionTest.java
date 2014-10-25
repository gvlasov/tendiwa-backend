package org.tendiwa.settlements;

import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.PointTrail;

import java.util.List;
import java.util.Set;

public class BlockRegionTest {
	@Test
	public void splitLineIntersectingVertices() {
		List<Point2D> points = new PointTrail(20, 20)
			.moveBy(0, 60)
			.moveBy(20, -30)
			.moveBy(10, 30)
			.moveBy(0, -30)
			.moveBy(10, 0)
			.moveBy(0, 30)
			.moveBy(10, -30)
			.moveBy(10, 30)
			.moveBy(10, -30)
			.moveBy(10, 30)
			.moveBy(0, -30)
			.moveBy(10, 0)
			.moveBy(0, 30)
			.moveBy(10, 0)
			.moveBy(0, -30)
			.moveBy(10, 0)
			.moveBy(0, 30)
			.moveBy(10, -30)
			.moveBy(-30, -20)
//			.moveBy(0, 30)
//			.moveBy(10, 0)
//			.moveBy(0, -40)
//			.moveBy(-40, -10)
			.moveBy(-30, 10)
			.moveBy(-30, 10)
			.moveBy(-10, -10)
			.points();
		BlockRegion point2Ds = new BlockRegion(points, 0);
		Set<BlockRegion> blockRegions = point2Ds.subdivideLots(10, 10, 0);

//		TestCanvas canvas = new TestCanvas(1, 800, 600);
//		for (BlockRegion blockRegion : blockRegions) {
//			canvas.draw(blockRegion, DrawingEnclosedBlock.withColor(Color.red));
//		}
//		canvas.draw(point2Ds, DrawingEnclosedBlock.withColor(Color.blue));
//		Demos.sleepIndefinitely();
	}

	@Test
	public void splitLineSingleIntersectingVertices() {
		List<Point2D> points = new PointTrail(20, 20)
			.moveBy(0, 40)
			.moveBy(20, -20)
			.points();
		BlockRegion block = new BlockRegion(points, 0);
		Set<BlockRegion> lots = block.subdivideLots(20, 20, 0);
//		TestCanvas canvas = new TestCanvas(1, 800, 600);
//		for (BlockRegion lot : lots) {
//			canvas.draw(lot, DrawingEnclosedBlock.withColor(Color.red));
//		}
//		Demos.sleepIndefinitely();
	}

}