package org.tendiwa.geometry.extensions;

import org.jgrapht.UndirectedGraph;
import org.junit.Test;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;

import java.awt.Color;
import java.util.List;

public class SameSlopeGraphEdgesPerturbationsTest {
	@Test
	public void perturb() {
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
		SameSlopeGraphEdgesPerturbations.perturb(graph, 1e-4);
		new TestCanvas(1, 500, 400).draw(graph, DrawingGraph.withColor(Color.red));
		Demos.sleepIndefinitely();

	}

	public static void main(String[] args) {
		new SameSlopeGraphEdgesPerturbationsTest().perturb();
	}

}