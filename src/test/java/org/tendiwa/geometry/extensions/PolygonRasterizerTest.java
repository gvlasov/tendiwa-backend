package org.tendiwa.geometry.extensions;

import org.junit.Test;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingCellSet;
import org.tendiwa.drawing.extensions.DrawingPolygon;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.List;

public class PolygonRasterizerTest {

	@Test
	public void testRasterizeToCellSet() throws Exception {
		List<Point2D> polygon = new PointTrail(200, 200)
			.moveBy(0, -40)
			.moveBy(-100, 0)
			.moveBy(0, 60)
			.moveBy(20, 0)
			.moveBy(0, -20)
			.points();
		new TestCanvas(1, 300, 300).draw(polygon, DrawingPolygon.withColor(Color.RED));
		BoundedCellSet rasterized = PolygonRasterizer.rasterizeToCellSet(polygon);
		new TestCanvas(1, 300, 300).draw(rasterized, DrawingCellSet.withColor(Color.RED));
	}
}