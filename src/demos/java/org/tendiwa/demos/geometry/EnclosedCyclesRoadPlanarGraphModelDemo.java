package org.tendiwa.demos.geometry;

import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.smartMesh.SegmentNetworkBuilder;
import org.tendiwa.geometry.smartMesh.SmartMesh2D;
import org.tendiwa.graphs.GraphConstructor;

import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class EnclosedCyclesRoadPlanarGraphModelDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(EnclosedCyclesRoadPlanarGraphModelDemo.class);
	}

	@Override
	public void run() {
		Canvas canvas = new TestCanvas(1, rectangle(800, 600));
//		DrawableInto canvas = new MagnifierCanvas(10, 219, 21, 600, 600);
		TestCanvas.canvas = canvas;
		SimpleGraph<Point2D, Segment2D> graph = graphConstructor()
			.cycleOfVertices(
				new PointTrail(20, 20)
					.moveByX(200)
					.moveByY(200)
					.moveByX(-200)
					.points()
			)
			.cycleOfVertices(
				new PointTrail(30, 30)
					.moveByX(40)
					.moveByY(40)
					.moveByX(-40)
					.points()
			)
			.cycleOfVertices(
				new PointTrail(90, 90)
					.moveByX(30)
					.moveByY(30)
					.moveByX(-30)
					.points()
			)
			.cycleOfVertices(
				new PointTrail(30, 90)
					.moveByX(30)
					.moveByY(30)
					.moveByX(-30)
					.points()
			)
			.cycleOfVertices(
				new PointTrail(39, 138)
					.moveByX(30)
					.moveByY(30)
					.moveByX(-30)
					.points()
			)
			.graph();

		SmartMesh2D city = new SegmentNetworkBuilder(graph)
			.withDefaults()
			.withRoadSegmentLength(47)
			.withSnapSize(20)
			.withMaxStartPointsPerCycle(1)
			.build();
//		canvas.draw(
//			city.graph(),
//			DrawingGraph.withColorAndAntialiasing(Color.red)
//		);
	}
}
