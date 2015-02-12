package org.tendiwa.demos;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import org.tendiwa.data.FourCyclePenisGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.drawing.extensions.DrawingPolygon;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class IntervalsAlongPolygonBorderDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(IntervalsAlongPolygonBorderDemo.class);
	}

	@Override
	public void run() {
		TestCanvas.canvas = canvas;
		Iterator<MinimalCycle<Point2D, Segment2D>> iterator = PlanarGraphs.minimumCycleBasis(
			FourCyclePenisGraph.create().graph()
		).minimalCyclesSet().iterator();
		List<Point2D> polygon = iterator.next().vertexList();
		Map<Segment2D, List<Point2D>> points = IntervalsAlongPolygonBorder.compute(
			polygon,
			25,
			6,
			Segment2D::new,
			new Random(0)
		);
		canvas.draw(polygon, DrawingPolygon.withColor(Color.red));
		Iterator<Color> colors = Iterators.cycle(Color.red, Color.black, Color.blue, Color.orange, Color.cyan);
		for (List<Point2D> pointes : points.values()) {
			canvas.drawAll(pointes, DrawingPoint2D.withColorAndSize(colors.next(), 5));
		}
	}
}
