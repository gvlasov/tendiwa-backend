package org.tendiwa.demos.settlements;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.RectanglePolygon;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.Mesh2D;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.geometry.smartMesh.MeshedNetworkBuilder;
import org.tendiwa.geometry.smartMesh.SmartMeshedNetwork;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.*;

public class NetworkWithHoles implements Runnable {
	public static void main(String[] args) {
		Demos.run(NetworkWithHoles.class);
	}

	@Override
	public void run() {
		TestCanvas canvas = new TestCanvas(
			2,
			rectangle(400, 400)
		);
		Polygon outerCycle = new RectanglePolygon(
			rectangle2D(10, 10, 100, 100)
		);
		Polygon innerCycle2 = new RectanglePolygon(
			rectangle2D(60, 40, 30, 30)
		);
		Polygon innerCycle1 = new RectanglePolygon(
			rectangle2D(20, 20, 30, 30)
		);
		UndirectedGraph<Point2D, Segment2D> graph = graphConstructor()
			.cycleOfVertices(outerCycle)
			.cycleOfVertices(innerCycle1)
			.cycleOfVertices(innerCycle2)
			.graph();
		MeshedNetwork segment2DSmartMesh = new MeshedNetworkBuilder(graph)
			.withDefaults()
			.withMaxStartPointsPerCycle(1)
			.build();
		canvas.draw(
			new DrawableGraph2D.Thin(
				segment2DSmartMesh.meshes()
					.stream()
					.map(Mesh2D::hull)
					.collect(Graph2D.toGraph2DUnion()),
				Color.cyan
			)
		);
	}
}
