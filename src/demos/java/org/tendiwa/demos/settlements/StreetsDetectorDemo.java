package org.tendiwa.demos.settlements;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableChain2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.settlements.utils.streetsDetector.DetectedStreets;

import java.awt.Color;
import java.util.Iterator;

import static org.tendiwa.geometry.GeometryPrimitives.graph2D;
import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class StreetsDetectorDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(StreetsDetectorDemo.class);
	}

	@Override
	public void run() {
		Graph2D graph = graph2D(
			graphConstructor()
			.vertex(0, point2D(20, 20))
			.vertex(1, point2D(40, 20))
			.vertex(2, point2D(60, 20))
			.vertex(3, point2D(80, 20))
			.vertex(4, point2D(100, 20))
			.vertex(10, point2D(20, 40))
			.vertex(11, point2D(40, 40))
			.vertex(12, point2D(60, 40))
			.vertex(13, point2D(80, 40))
			.vertex(14, point2D(100, 40))
			.vertex(20, point2D(20, 60))
			.vertex(21, point2D(40, 60))
			.vertex(22, point2D(60, 60))
			.vertex(23, point2D(80, 60))
			.vertex(24, point2D(100, 60))
			.path(0, 11, 22)
			.path(10, 11, 2)
			.edge(11, 12)
			.edge(11, 20)
			.graph()
		);
		Iterator<Color> colors = Iterators.cycle(
			Color.red, Color.blue, Color.green,
			Color.cyan, Color.magenta, Color.orange
		);
		canvas.drawAll(
			DetectedStreets.toChain2DStream(graph),
			street ->
				new DrawableChain2D.Thin(
					street,
					colors.next()
				)
		);
	}
}
