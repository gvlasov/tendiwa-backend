package org.tendiwa.geometry.extensions.polygonRasterization;

import org.junit.Test;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.PointTrail;

import java.util.List;

public class PolygonRasterizerTest {

	@Test
	public void nonComplexPolygonWith3SameYCoordinateVertices() {
		List<Point2D> polygon = new PointTrail(200, 200)
			.moveBy(20, 0)
			.moveBy(0, -40)
			.moveBy(-100, 0)
			.moveBy(0, 60)
			.moveBy(20, 0)
			.moveBy(0, -20)
			.points();
		PolygonRasterizer.rasterizeToCellSet(polygon);
	}

	@Test
	public void complexPolygonWithAllCombinationsOfSameYCoordinateVertices() {
		List<Point2D> polygon = new PointTrail(200, 200)
			.moveBy(5, -10)
			.moveBy(5, 10)
			.moveByX(10)
			.moveByX(10)
			.moveByY(10)
			.moveByX(50)
			.moveByY(-10)
			.moveByX(10)
			.moveByX(10)
			.moveBy(-30, -10)
			.moveByY(10)
			.moveByX(-10)
			.moveByX(-10)
			.moveByX(-10)
			.moveByY(-10)
			.moveBy(-40, -10)
			.moveBy(-70, 10)
			.moveByY(10)
			.moveByX(10)
			.moveByX(10)
			.moveByX(10)
			.moveBy(5, 10)
			.moveBy(5, -10)
			.moveByX(10)
			.moveByX(10)
			.moveByX(10)
			.points();
		PolygonRasterizer.rasterizeToCellSet(polygon);
	}

	@Test
	public void rectangle() {
		List<Point2D> polygon = new PointTrail(200, 200)
			.moveByX(100)
			.moveByY(100)
			.moveByX(-100)
			.points();
		PolygonRasterizer.rasterizeToCellSet(polygon);
		new TestCanvas(1, 500, 500);
	}
}