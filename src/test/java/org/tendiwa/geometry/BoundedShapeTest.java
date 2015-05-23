package org.tendiwa.geometry;

import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.graphs2d.Graph2D;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.*;

public final class BoundedShapeTest {

	@Test
	public void rectangleIsBoundedByItself() {
		Rectangle2D rectangle = rectangle2D(10.6, 10.9);
		assertEquals(
			rectangle,
			rectangle.bounds()
		);
	}

	@Test
	public void integerBounds() {
		double width = 1.5;
		double height = 2.5;
		Rectangle2D rectangle = rectangle2D(width, height);
		assertEquals(
			rectangle.integerBounds().area(),
			Math.ceil(width) * Math.ceil(height),
			0.000001
		);
	}

	@Test
	public void triangleBounds() {
		int width = 100;
		int height = 70;
		Polygon triangle = new PointTrail(0, 0)
			.moveByX(width)
			.moveBy(-50, height)
			.polygon();
		assertEquals(
			triangle.bounds().area(),
			width * height,
			0.00001
		);
	}

	@Test
	public void graphBounds() {
		double width = 100;
		double height = 100;
		Graph2D crossGraph =
			graph2D(
				graphConstructor()
					.vertex(1, point2D(0, 0))
					.vertex(2, point2D(width, 0))
					.vertex(3, point2D(width, height))
					.vertex(4, point2D(0, height))
					.vertex(5, point2D(width / 2, height / 2))
					.edge(1, 5)
					.edge(2, 5)
					.edge(3, 5)
					.edge(4, 5)
					.graph()
			);
		assertEquals(
			crossGraph.bounds().area(),
			width * height,
			0.00001
		);

	}
}