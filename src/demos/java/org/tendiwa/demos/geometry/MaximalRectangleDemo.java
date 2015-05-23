package org.tendiwa.demos.geometry;

import org.tendiwa.demos.Demos;
import org.tendiwa.demos.DrawableRectangle;
import org.tendiwa.demos.geometry.polygons.ConvexAndReflexAmoeba;
import org.tendiwa.demos.settlements.DrawableCellSet;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.ArrayBackedCellSet;
import org.tendiwa.geometry.extensions.polygonRasterization.MutableRasterizedPolygon;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class MaximalRectangleDemo implements Runnable {

	public static void main(String[] args) {
		Demos.run(MaximalRectangleDemo.class, new DrawingModule());
	}


	@Override
	public void run() {
		TestCanvas canvas = new TestCanvas(1, rectangle(200, 200));
		ArrayBackedCellSet rasterizedPolygon =
			new MutableRasterizedPolygon(
				new ConvexAndReflexAmoeba()
			);
		canvas.draw(
			new DrawableCellSet(
				rasterizedPolygon,
				Color.red
			)
		);
		canvas.draw(
			new DrawableRectangle(
				rasterizedPolygon.maximalRectangle().get(),
				Color.blue
			)
		);
	}
}
