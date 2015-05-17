package org.tendiwa.demos;

import org.tendiwa.demos.settlements.DrawableMesh2D;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.geometry.smartMesh.MeshedNetworkBuilder;
import org.tendiwa.geometry.smartMesh.SmartMeshedNetwork;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class InnerFilamentCityDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(InnerFilamentCityDemo.class);
	}

	@Override
	public void run() {
		GraphConstructor<Point2D, Segment2D> gc =
			graphConstructor()
				.vertex(0, point2D(200, 100))
				.vertex(1, point2D(400, 100))
				.vertex(2, point2D(500, 200))
				.vertex(3, point2D(500, 400))
				.vertex(4, point2D(400, 500))
				.vertex(5, point2D(200, 500))
				.vertex(6, point2D(100, 400))
				.vertex(7, point2D(100, 200))
				.vertex(8, point2D(300, 200))
				.vertex(9, point2D(400, 450))
				.cycle(0, 1, 2, 3, 4, 5, 6, 7)
				.edge(0, 8)
				.edge(6, 9);
		TestCanvas canvas = Demos.createCanvas();
		MeshedNetwork network = new MeshedNetworkBuilder(gc.graph())
			.withDefaults()
			.withRoadSegmentLength(50)
			.build();
		canvas.draw(
			new DrawableGraph2D.Thin(
				network.fullGraph(),
				Color.red
			)
		);
	}
}
