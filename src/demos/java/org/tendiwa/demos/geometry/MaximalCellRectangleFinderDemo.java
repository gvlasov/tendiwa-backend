package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.geometry.polygons.ConvexAndReflexAmoeba;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingCellSet;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.drawing.extensions.PieChartTimeProfiler;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Recs;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.extensions.PolygonRasterizer;
import org.tendiwa.geometry.extensions.daveedvMaxRec.MaximalCellRectangleFinder;

import java.awt.Color;
import java.util.List;

public class MaximalCellRectangleFinderDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(MaximalCellRectangleFinderDemo.class, new DrawingModule());
	}


	@Override
	public void run() {
		TestCanvas.canvas = canvas;
		List<Point2D> polygon = new ConvexAndReflexAmoeba();
//		List<Point2D> polygon = new PointTrail(20, 20)
//			.moveBy(30, -10)
//			.moveBy(10, 30)
//			.moveBy(-30, 10)
//			.points();
		PieChartTimeProfiler chart = new PieChartTimeProfiler();
		PolygonRasterizer.Result rasterizedPolygon = PolygonRasterizer.rasterize(polygon);
		chart.saveTime("Rasterization");
		Rectangle largestRectangle = MaximalCellRectangleFinder.compute(
			rasterizedPolygon.bitmap
		).get();
		chart.saveTime("Rectangle search");
		canvas.draw(rasterizedPolygon.toCellSet(), DrawingCellSet.withColor(Color.red));
		canvas.draw(
			Recs.rectangleMovedFromOriginal(largestRectangle, rasterizedPolygon.x, rasterizedPolygon.y),
			DrawingRectangle.withColor(Color.blue)
		);
		chart.draw();
	}
}
