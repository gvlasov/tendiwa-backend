package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;

import java.awt.Color;

public class NestedMinimalCyclesDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(NestedMinimalCyclesDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		UndirectedGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
			.vertex(0, new Point2D(20, 20))
			.vertex(1, new Point2D(20, 60))
			.vertex(2, new Point2D(60, 60))
			.vertex(3, new Point2D(60, 20))
			.vertex(4, new Point2D(30, 30))
			.vertex(5, new Point2D(30, 50))
			.vertex(6, new Point2D(50, 50))
			.vertex(7, new Point2D(50, 30))
			.cycle(0, 1, 2, 3)
			.cycle(4, 5, 6, 7)
			.edge(0, 4)
			.graph();
		canvas.draw(graph, DrawingGraph.basis(Color.green, Color.red, Color.blue));
	}
}
