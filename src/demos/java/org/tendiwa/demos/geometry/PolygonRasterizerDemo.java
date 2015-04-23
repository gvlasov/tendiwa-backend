package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.settlements.DrawableCellSet;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.extensions.polygonRasterization.PolygonRasterizer;

import java.awt.Color;
import java.util.List;

public class PolygonRasterizerDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(PolygonRasterizerDemo.class, new DrawingModule());
	}


	@Override
	public void run() {
		List<Point2D> polygon =
			new PointTrail(20, 20)
				.moveBy(40, 0)
				.moveBy(0, 40)
				.moveBy(-40, 0)
				.points();
		BoundedCellSet rasterizedPolygon = PolygonRasterizer.rasterizeToCellSet(polygon);
		canvas.draw(
			new DrawableCellSet.Finite(
				rasterizedPolygon,
				Color.red
			)
		);
	}
}
