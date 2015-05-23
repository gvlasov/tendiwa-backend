package org.tendiwa.geometry.extensions;

import org.junit.Test;
import org.tendiwa.demos.Demos;
import org.tendiwa.demos.settlements.DrawableCellSet;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableCell;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.geometry.CellSet;
import org.tendiwa.geometry.CellsBounds;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;

import java.awt.Color;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.tendiwa.collections.Collectors.toImmutableSet;
import static org.tendiwa.geometry.GeometryPrimitives.*;

public class ShapeFromOutlineTest {
	@Test
	public void shouldFind10000Cells() {

		Graph2D graph =
			graph2D(
				graphConstructor()
					.vertex(0, point2D(0, 0))
					.vertex(1, point2D(99, 0))
					.vertex(2, point2D(99, 99))
					.vertex(3, point2D(0, 99))
					.cycle(0, 1, 2, 3)
					.graph()
			);
		assertEquals(10000, ShapeFromOutline.from(graph).toSet().size());
	}

	@Test
	public void shouldFind19Cells() throws Exception {
		/*
		This is the graph:
         O####O
          ##..#
            O.#
             ##
             ##
              O
         */
		Graph2D graph = graph2D(
			graphConstructor()
				.vertex(0, point2D(0, 0))
				.vertex(1, point2D(5, 0))
				.vertex(2, point2D(5, 5))
				.vertex(3, point2D(3, 2))
				.cycle(0, 1, 2, 3)
				.graph()
		);
		int numOfShapeCells = ShapeFromOutline.from(graph).toSet().size();
		TestCanvas canvas = new TestCanvas(10, rectangle(12, 12));
		canvas.draw(
			new DrawableCell(
				cell(0, 0),
				Color.green
			)
		);
		canvas.draw(
			new DrawableCell(
				cell(1, 0),
				Color.green
			)
		);
		canvas.draw(
			new DrawableGraph2D.Thin(
				graph,
				Color.blue
			)
		);
		canvas
			.draw(
				new DrawableCellSet(
					ShapeFromOutline.from(graph),
					Color.red
				)
			);
		Demos.sleepIndefinitely();
		assertEquals(19, numOfShapeCells);
	}

	@Test
	public void shouldFindZeroInnerCells() {
		/*
		This is the graph:
        O###O
        O###O
        It has no cells within graph shape (inner cells) that are not on graph edges.
         */
		Graph2D graph =
			graph2D(
				graphConstructor()
					.vertex(0, point2D(0, 0))
					.vertex(1, point2D(4, 0))
					.vertex(2, point2D(4, 1))
					.vertex(3, point2D(0, 1))
					.cycle(0, 1, 2, 3)
					.graph()
			);
		CellSet shape = ShapeFromOutline.from(graph);
		CellSet outlineCells = graph
			.edgeSet()
			.stream()
			.map(Segment2D.toCellList())
			.flatMap(Collection::stream)
			.distinct()
			.collect(CellSet.toCellSet());
		CachedCellSet innerCells = new CachedCellSet(
			(x, y) -> shape.contains(x, y) && !outlineCells.contains(x, y),
			new CellsBounds(
				graph.vertexSet().stream().map(Point2D::toCell).collect(toImmutableSet())
			)
		);
		assertEquals(0, innerCells.toSet().size());
	}
}
