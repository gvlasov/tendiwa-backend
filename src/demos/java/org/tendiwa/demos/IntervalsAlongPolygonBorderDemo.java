package org.tendiwa.demos;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import org.tendiwa.data.FourCyclePenisGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawablePoint2D;
import org.tendiwa.drawing.extensions.DrawablePolygon;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.graphs.MinimalCycle;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.tendiwa.geometry.GeometryPrimitives.polygon;

public class IntervalsAlongPolygonBorderDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(IntervalsAlongPolygonBorderDemo.class);
	}

	@Override
	public void run() {
		TestCanvas.canvas = canvas;
		Iterator<MinimalCycle<Point2D, Segment2D>> iterator =
			PlanarGraphs.minimumCycleBasis(new FourCyclePenisGraph())
				.minimalCyclesSet().iterator();
		Polygon polygon = polygon(iterator.next().vertexList());
		Map<Segment2D, List<Point2D>> points = IntervalsAlongPolygonBorder.compute(
			polygon,
			25,
			6,
			BasicSegment2D::new,
			new Random(0)
		);
		canvas.draw(new DrawablePolygon(polygon, Color.red));
		Iterator<Color> colors = Iterators.cycle(Color.red, Color.black, Color.blue, Color.orange, Color.cyan);
		for (List<Point2D> pointes : points.values()) {
			canvas.drawAll(
				pointes,
				p -> new DrawablePoint2D.Circle(p, colors.next(), 5)
			);
		}
	}
}
