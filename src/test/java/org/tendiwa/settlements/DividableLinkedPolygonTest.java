package org.tendiwa.settlements;

import org.junit.Test;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.extensions.PointTrail;

import java.util.Set;

public class DividableLinkedPolygonTest {
	@Test
	public void splitLineIntersectingVertices() {
		Polygon polygon = new PointTrail(20, 20)
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
			.polygon();
		DividableLinkedPolygon point2Ds = new BasicDividableLinkedPolygon(polygon);
		Set<DividableLinkedPolygon> dividableEnclosedBlocks = point2Ds.subdivideLots(10, 10, 0);

//		TestCanvas canvas = new TestCanvas(1, 800, 600);
//		for (BlockRegion blockRegion : blockRegions) {
//			canvas.draw(blockRegion, DrawingEnclosedBlock.withColor(Color.red));
//		}
//		canvas.draw(point2Ds, DrawingEnclosedBlock.withColor(Color.blue));
//		Demos.sleepIndefinitely();
	}

	@Test
	public void splitLineSingleIntersectingVertices() {
		Polygon points = new PointTrail(20, 20)
			.moveBy(0, 40)
			.moveBy(20, -20)
			.polygon();
		DividableLinkedPolygon block = new BasicDividableLinkedPolygon(points);
		Set<DividableLinkedPolygon> lots = block.subdivideLots(20, 20, 0);
//		TestCanvas canvas = new TestCanvas(1, 800, 600);
//		for (BlockRegion lot : lots) {
//			canvas.draw(lot, DrawingEnclosedBlock.withColor(Color.red));
//		}
//		Demos.sleepIndefinitely();
	}

}