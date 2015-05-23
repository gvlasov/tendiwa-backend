package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.DrawableCycleBasis2D;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.graphs2d.Graph2D;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.*;

public class NestedMinimalCyclesDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(NestedMinimalCyclesDemo.class, new DrawingModule());
	}

	@Override
	public void run() {
		Graph2D graph = graph2D(
			graphConstructor()
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
				.graph()
		);
		canvas.draw(
			new DrawableCycleBasis2D(
				graph.minimumCycleBasis(),
				Color.green,
				Color.red,
				Color.blue
			)
		);
	}
}
