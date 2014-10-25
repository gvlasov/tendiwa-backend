package org.tendiwa.demos.settlements;

import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.RoadsPlanarGraphModel;
import org.tendiwa.settlements.CityGeometryBuilder;

public class NetworkWithHoles implements Runnable {
	public static void main(String[] args) {
		Demos.run(NetworkWithHoles.class);
	}

	@Override
	public void run() {
		TestCanvas canvas = new TestCanvas(2, 400, 400);
		SimpleGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
			.cycleOfVertices(new PointTrail(60, 40).moveBy(30, 0).moveBy(0, 30).moveBy(-30, 0).points())
			.cycleOfVertices(new PointTrail(20, 20).moveBy(0, 30).moveBy(30, 0).moveBy(0, -30).points())
			.cycleOfVertices(new PointTrail(10, 10).moveBy(0, 100).moveBy(100, 0).moveBy(0, -100).points())
			.graph();
		RoadsPlanarGraphModel roadsPlanarGraphModel = new CityGeometryBuilder(graph)
			.withDefaults()
			.withMaxStartPointsPerCycle(1)
			.build();
		canvas.draw(roadsPlanarGraphModel, new CityDrawer());

	}
}
