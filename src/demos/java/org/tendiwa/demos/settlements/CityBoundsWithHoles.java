package org.tendiwa.demos.settlements;

import org.apache.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.GifBuilder;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingCellSet;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.extensions.DrawingRectangle;
import org.tendiwa.geometry.*;
import org.tendiwa.noise.Noise;
import org.tendiwa.pathfinding.dijkstra.PathTable;
import org.tendiwa.settlements.cityBounds.CityBounds;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

class CityBoundsWithHoles implements Runnable {
	Rectangle worldRec = new Rectangle(0, 0, 400, 400);
	TestCanvas canvas = new TestCanvas(1, worldRec.width, worldRec.height);
//	GifBuilder gifBuilder = new GifBuilder(canvas, 6, Logger.getLogger("cityBoundsWithHoles"));
	int iterations = 55;
	int cityRadius = 100;
	Cell startCell = new Cell(200, 200);


	public static void main(String[] args) {
		Demos.run(CityBoundsWithHoles.class);
	}


	@Override
	public void run() {
		TestCanvas.canvas = canvas;
		AtomicInteger counter = new AtomicInteger(146);
		CellSet water = (x, y) -> Noise.noise(
			((double) x + 700 + 0) / 50,
			((double) y + 200 + 0) / 50,
			5
		) < counter.get();

		for (int i = 0; i < iterations; i++) {
			System.out.println(i);
			UndirectedGraph<Point2D, Segment2D> cityBounds = buildCityBoundsGraph(water);

			canvas.draw(worldRec, DrawingRectangle.withColor(Color.green));
			canvas.draw(water, DrawingCellSet.onWholeCanvasWithColor(Color.blue));
			canvas.draw(cityBounds, DrawingGraph.withColorAndVertexSize(Color.red, 0));

//			gifBuilder.saveFrame();
			canvas.clear();
			counter.decrementAndGet();
		}
//		gifBuilder.saveAnimation(System.getProperty("user.home") + "/cityBoundsWithHolesAnimation.gif");
	}

	private UndirectedGraph<Point2D, Segment2D> buildCityBoundsGraph(CellSet water) {
		BoundedCellSet cityShape = new PathTable(
			startCell.x,
			startCell.y,
			(x, y) -> !water.contains(x, y),
			cityRadius
		).computeFull();
		return CityBounds.create(
			cityShape,
			startCell,
			cityRadius
		);
	}
}
