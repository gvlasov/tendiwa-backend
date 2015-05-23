package org.tendiwa.demos.settlements;

import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.geometry.smartMesh.MeshedNetworkBuilder;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.*;

public class FirstCityGenerationDemo {
	public static void main(String[] args) {
		TestCanvas canvas = Demos.createCanvas();
		Graph2D graph =
			graph2D(
				graphConstructor()
					.vertex(0, point2D(110, 110))
					.vertex(1, point2D(130, 110))
					.vertex(2, point2D(150, 130))
					.vertex(3, point2D(150, 170))
					.vertex(4, point2D(110, 170))
					.vertex(5, point2D(130, 190))
					.vertex(6, point2D(171, 113))
					.vertex(7, point2D(200, 124))
					.vertex(8, point2D(209, 155))
					.vertex(9, point2D(184, 187))
					.cycle(0, 1, 2, 3, 4)
					.cycle(3, 5, 9, 8, 7, 6, 1, 2)
					.graph()
			);
		MeshedNetwork network = new MeshedNetworkBuilder(graph)
			.withDefaults()
			.build();
		canvas.draw(
			new DrawableGraph2D.Thin(
				network,
				Color.red
			)
		);
	}
}
