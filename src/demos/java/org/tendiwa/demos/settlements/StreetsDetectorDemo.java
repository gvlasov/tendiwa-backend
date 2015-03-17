package org.tendiwa.demos.settlements;

import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingChain;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.geometry.Chain2D;
import org.tendiwa.settlements.utils.streetsDetector.DetectedStreets;

import java.awt.Color;
import java.util.Iterator;

public class StreetsDetectorDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(StreetsDetectorDemo.class);
	}

	@Override
	public void run() {
		SimpleGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
			.vertex(0, new Point2D(20, 20))
			.vertex(1, new Point2D(40, 20))
			.vertex(2, new Point2D(60, 20))
			.vertex(3, new Point2D(80, 20))
			.vertex(4, new Point2D(100, 20))
			.vertex(10, new Point2D(20, 40))
			.vertex(11, new Point2D(40, 40))
			.vertex(12, new Point2D(60, 40))
			.vertex(13, new Point2D(80, 40))
			.vertex(14, new Point2D(100, 40))
			.vertex(20, new Point2D(20, 60))
			.vertex(21, new Point2D(40, 60))
			.vertex(22, new Point2D(60, 60))
			.vertex(23, new Point2D(80, 60))
			.vertex(24, new Point2D(100, 60))
			.path(0, 11, 22)
			.path(10, 11, 2)
			.edge(11, 12)
			.edge(11, 20)
			.graph();
		Iterator<Color> colors = Iterators.cycle(
			Color.red, Color.blue, Color.green,
			Color.cyan, Color.magenta, Color.orange
		);
		DetectedStreets.toChain2DStream(graph)
			.forEach(street -> drawStreet(street, colors.next()));
	}

	private void drawStreet(Chain2D street, Color color) {
		canvas.draw(street, DrawingChain.withColorThin(color));
	}
}
