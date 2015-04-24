package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.DrawableCycleBasis2D;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class NestedMinimalCyclesDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(NestedMinimalCyclesDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		UndirectedGraph<Point2D, Segment2D> graph = graphConstructor()
			.vertex(0, point2D(20, 20))
			.vertex(1, point2D(20, 60))
			.vertex(2, point2D(60, 60))
			.vertex(3, point2D(60, 20))
			.vertex(4, point2D(30, 30))
			.vertex(5, point2D(30, 50))
			.vertex(6, point2D(50, 50))
			.vertex(7, point2D(50, 30))
			.cycle(0, 1, 2, 3)
			.cycle(4, 5, 6, 7)
			.edge(0, 4)
			.graph();
		canvas.draw(
			new DrawableCycleBasis2D(
				PlanarGraphs.minimumCycleBasis(graph),
				Color.green,
				Color.red,
				Color.blue
			)
		);
	}
}
