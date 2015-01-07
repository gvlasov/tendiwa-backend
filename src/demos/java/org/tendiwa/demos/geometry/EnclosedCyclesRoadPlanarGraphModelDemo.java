package org.tendiwa.demos.geometry;

import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.networks.CityGeometryBuilder;
import org.tendiwa.settlements.networks.RoadsPlanarGraphModel;

import java.awt.Color;

public class EnclosedCyclesRoadPlanarGraphModelDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(EnclosedCyclesRoadPlanarGraphModelDemo.class);
	}

	@Override
	public void run() {
		TestCanvas canvas = new TestCanvas(1, 800, 600);
		TestCanvas.canvas = canvas;
		SimpleGraph<Point2D, Segment2D> graph = new GraphConstructor<>(PlanarGraphs.getEdgeFactory())
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

		RoadsPlanarGraphModel city = new CityGeometryBuilder(graph)
			.withDefaults()
			.withRoadSegmentLength(40)
			.withSnapSize(20)
			.withMaxStartPointsPerCycle(1)
			.build();
		canvas.draw(
			city.getFullRoadGraph(),
			DrawingGraph.withColorAndAntialiasing(Color.red)
		);
	}
}
