package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.geometry.polygons.ConvexAndReflexAmoeba;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingCellSet;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.drawing.extensions.PieChartTimeProfiler;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.CachedCellSet;
import org.tendiwa.geometry.extensions.polygonRasterization.MutableRasterizationResult;
import org.tendiwa.geometry.extensions.polygonRasterization.PolygonRasterizer;
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
		MutableRasterizationResult rasterizedPolygon = PolygonRasterizer.rasterizeToMutable(polygon);
		chart.saveTime("Rasterization");
		Rectangle largestRectangle = MaximalCellRectangleFinder.compute(
			rasterizedPolygon.bitmap
		).get();
		chart.saveTime("Rectangle search");
		Rectangle bounds = new Rectangle(
			rasterizedPolygon.x,
			rasterizedPolygon.y,
			rasterizedPolygon.width,
			rasterizedPolygon.height
		);
		canvas.draw(
			new CachedCellSet(
				(x, y) -> bounds.contains(x, y)
					&& rasterizedPolygon.bitmap[y - rasterizedPolygon.y][x - rasterizedPolygon.x],
				bounds
			),
			DrawingCellSet.withColor(Color.red)
		);
		canvas.draw(
			Recs.rectangleMovedFromOriginal(largestRectangle, rasterizedPolygon.x, rasterizedPolygon.y),
			DrawingRectangle.withColor(Color.blue)
		);
		chart.draw();
	}
}
