package org.tendiwa.demos.settlements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.inject.Inject;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingChain;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.utils.StreetsDetector;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
		Set<ImmutableList<Point2D>> streets = StreetsDetector.detectStreets(graph);
		Iterator<Color> colors = Iterators.cycle(Color.red, Color.blue, Color.green, Color.cyan, Color.magenta, Color.orange);
		for (List<Point2D> street : streets) {
			canvas.draw(street, DrawingChain.withColor(colors.next()));
		}
	}
}
