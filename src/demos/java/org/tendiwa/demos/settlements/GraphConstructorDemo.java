package org.tendiwa.demos.settlements;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;

import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class GraphConstructorDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(GraphConstructorDemo.class);
	}


	@Override
	public void run() {
		TestCanvas canvas = Demos.createCanvas();
		GraphConstructor<Point2D, Segment2D> gc = graphConstructor()
			.vertex(0, point2D(100, 100))
			.vertex(1, point2D(100, 200))
			.vertex(2, point2D(200, 100))
			.vertex(3, point2D(200, 200)).withEdgesTo(0, 1, 2)

			.vertex(4, point2D(300, 300))
			.edge(4, 5)
			.vertex(5, point2D(400, 400))

			.vertex(6, point2D(500, 300))
			.vertex(7, point2D(600, 300))
			.vertex(8, point2D(600, 400))
			.vertex(9, point2D(500, 400))
			.cycle(6, 7, 8, 9)

			.path(2, 4, 6);
		canvas.draw(
			new DrawableGraph2D.WithAliases(
				gc.graph(),
				gc
			)
		);
	}
}
