package org.tendiwa.demos.geometry;

import org.tendiwa.demos.Demos;
import org.tendiwa.demos.settlements.DrawableCellSet;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawablePolygon;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.BoundedCellSet;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.extensions.polygonRasterization.MutableRasterizedPolygon;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class PolygonRasterizerDemo implements Runnable {
	TestCanvas canvas = new TestCanvas(3, rectangle(200, 200));

	public static void main(String[] args) {
		Demos.run(PolygonRasterizerDemo.class, new DrawingModule());
	}


	@Override
	public void run() {
		Polygon polygon =
			new PointTrail(20, 20)
				.moveByX(120)
				.moveByY(100)
				.moveByX(-40)
				.moveByY(-50)
				.moveByX(-40)
				.moveByY(50)
				.moveByX(-40)
				.polygon();
		BoundedCellSet rasterizedPolygon =
			new MutableRasterizedPolygon(polygon);
		canvas.draw(
			new DrawableCellSet.Finite(
				rasterizedPolygon,
				Color.red
			)
		);
		canvas.draw(
			new DrawablePolygon.Thin(
				polygon,
				Color.blue
			)
		);
	}
}
