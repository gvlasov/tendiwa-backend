package tests.painting;

import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingCell;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.RoadGraph;

import java.awt.*;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class CityGenerationDemo {
@Inject
TestCanvas canvas;

@Test
public void draw() {
	RoadGraph roadGraph = new RoadGraph(
		new Point2D[]{
			new Point2D(10, 10),
			new Point2D(10 + 20, 10),
			new Point2D(10 + 20 + 20, 10 + 20),
			new Point2D(10 + 20 + 20, 10 + 20 + 40),
			new Point2D(10, 10 + 20 + 40),
			new Point2D(10 + 20, 10 + 20 + 40 +20)
		},
		new int[][]{
			new int[]{0, 1},
			new int[]{1, 2},
			new int[]{2, 3},
			new int[]{3, 4},
			new int[]{4, 0},
			new int[]{3, 5}
		}
	);
	canvas.draw(
		new City(
			roadGraph
		),
		new DrawingAlgorithm<City>() {

			private final DrawingAlgorithm<Cell> cellDrawingAlgorithm = DrawingCell.withColorAndSize(Color.RED, 5);

			@Override
			public void draw(City city) {
				for (Line2D edge : city.getPrimaryRoadNetwork().edgeSet()) {
					drawLine(
						edge.start.x,
						edge.start.y,
						edge.end.x,
						edge.end.y,
						Color.BLUE
					);
				}
				for (Point2D vertex : city.getPrimaryRoadNetwork().vertexSet()) {
					canvas.draw(
						new Cell((int) vertex.x, (int) vertex.y),
						cellDrawingAlgorithm
					);
				}
			}
		}
	);
	try {
		Thread.sleep(200000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}
}
