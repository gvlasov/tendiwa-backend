package org.tendiwa.demos;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.graphs.algorithms.SameOrPerpendicularSlopeGraphEdgesPerturbations;

import java.awt.Color;
import java.util.List;

public class SameOrPerpendicularSlopeGraphEdgesPerturbationsTest implements Runnable {
	public static void main(String[] args) {
		Demos.run(SameOrPerpendicularSlopeGraphEdgesPerturbationsTest.class);
	}

	@Override
	public void run() {
		List<Point2D> polygon = new PointTrail(20, 20)
			.moveByX(100)
			.moveByY(20)
			.moveByX(-40)
			.moveByY(10)
			.moveByX(20)
			.moveByY(10)
			.moveByX(20)
			.moveByY(10)
			.moveByX(-10)
			.moveByY(10)
			.moveByX(-10)
			.moveByY(-10)
			.moveByX(-10)
			.moveByY(10)
			.moveByX(-10)
			.moveByY(-10)
			.moveBy(-10, -10)
			.moveBy(-10, 10)
			.moveBy(-10, -10)
			.moveBy(10, -10)
			.moveBy(-10, -10)
			.moveBy(-10, 10)
			.moveBy(-10, -10)
			.moveBy(10, -10)
			.moveBy(-5, -1)
			.points();
		UndirectedGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
			.cycleOfVertices(polygon)
			.graph();
		SameOrPerpendicularSlopeGraphEdgesPerturbations.perturb(graph, 1e-4);
		new TestCanvas(1, 500, 400).draw(graph, DrawingGraph.withColor(Color.red));
	}

}