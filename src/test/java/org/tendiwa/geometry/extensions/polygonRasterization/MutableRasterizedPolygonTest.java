package org.tendiwa.geometry.extensions.polygonRasterization;

import org.junit.Test;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.settlements.DrawableCellSet;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawablePolygon;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Rectangle2D;
import org.tendiwa.geometry.extensions.PointTrail;

import java.awt.Color;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle2D;
import static org.tendiwa.geometry.GeometryPrimitives.vector;

public final class MutableRasterizedPolygonTest {
	@Test
	public void rasterized_rectangle() {
		int width = 10;
		Rectangle2D square = rectangle2D(width, width);
		assertEquals(
			square.integerBounds().area(),
			new MutableRasterizedPolygon(square).toSet().size()
		);
	}

	@Test
	public void redundant_consecutive_edges() {
		Polygon polygon = new PointTrail(0, 0)
			.moveBy(20, 20)
			.moveByX(10)
			.moveByX(10)
			.moveBy(20, -20)
			.moveByY(30)
			.moveByX(-10)
			.moveByX(-10)
			.moveByX(-10)
			.moveByX(-10)
			.moveByX(-10)
			.moveByX(-10)
			.polygon();
		assertTrue(!new MutableRasterizedPolygon(polygon).toSet().isEmpty());
	}

	@Test
	public void non_complex_polygon_with_3_same_y_coordinate_vertices() {
		Polygon polygon = new PointTrail(200, 200)
			.moveBy(20, 0)
			.moveBy(0, -40)
			.moveBy(-100, 0)
			.moveBy(0, 60)
			.moveBy(20, 0)
			.moveBy(0, -20)
			.polygon();
		assertTrue(!new MutableRasterizedPolygon(polygon).toSet().isEmpty());
	}

	@Test
	public void complex_polygon_with_all_combinations_of_same_y_coordinate_vertices() {
		Polygon polygon = new PointTrail(200, 200)
			.moveBy(5, -10)
			.moveBy(5, 10)
			.moveByX(10)
			.moveByX(10)
			.moveByY(10)
			.moveByX(50)
			.moveByY(-10)
			.moveByX(10)
			.moveByX(10)
			.moveBy(-30, -10)
			.moveByY(10)
			.moveByX(-10)
			.moveByX(-10)
			.moveByX(-10)
			.moveByY(-10)
			.moveBy(-40, -10)
			.moveBy(-70, 10)
			.moveByY(10)
			.moveByX(10)
			.moveByX(10)
			.moveByX(10)
			.moveBy(5, 10)
			.moveBy(5, -10)
			.moveByX(10)
			.moveByX(10)
//			.moveByX(10)
			.polygon();
		assertTrue(!new MutableRasterizedPolygon(polygon).toSet().isEmpty());
	}

	@Test
	public void thin_rectangle_can_rasterize_to_empty_set() {
		Polygon polygon = rectangle2D(0.5, 0.5, 0.1, 10);
		assertEquals(
			0,
			new MutableRasterizedPolygon(polygon).toSet().size()
		);
	}

	@Test
	public void all_points_of_polygon_have_negative_coordinates() {
		Polygon polygon = rectangle2D(-100, -100, 20, 20);
		assertEquals(
			polygon.integerBounds().area(),
			new MutableRasterizedPolygon(polygon).toSet().size()
		);
	}

	@Test
	public void some_points_of_polygon_have_negative_coordinates() {
		Polygon polygon = rectangle2D(-10, -10, 20, 20);
		assertEquals(
			polygon.integerBounds().area(),
			new MutableRasterizedPolygon(polygon).toSet().size()
		);
	}

	@Test
	public void can_rasterize_to_single_cell() {
		Polygon polygon = rectangle2D(0.5, 0.5, 1, 1);
		assertEquals(
			1,
			new MutableRasterizedPolygon(polygon).toSet().size()
		);
	}
}