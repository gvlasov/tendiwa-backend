package tests.painting;

import com.google.common.collect.ImmutableCollection;
import com.google.inject.Inject;
import org.jgrapht.alg.cycle.PatonCycleBase;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingCell;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.RoadGraph;
import org.tendiwa.settlements.SampleSelectionStrategy;

import java.awt.*;
import java.util.*;
import java.util.List;

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
			new Point2D(10 + 20, 10 + 20 + 40 + 20),
			new Point2D(71, 13),
			new Point2D(100, 24),
			new Point2D(109, 55),
			new Point2D(84, 87),
		},
		new int[][]{
			new int[]{0, 1},
			new int[]{1, 2},
			new int[]{2, 3},
			new int[]{3, 4},
			new int[]{4, 0},
			new int[]{3, 5},
			new int[]{1, 6},
			new int[]{6, 7},
			new int[]{7, 8},
			new int[]{8, 9},
			new int[]{9, 5},
		}
	);
	City city = new City(
		roadGraph,
		new SampleSelectionStrategy() {
			@Override
			public Point2D selectNextPoint(ImmutableCollection<Point2D> sampleFan) {
				int rand = Chance.rand(0, sampleFan.size() - 1);
				return sampleFan.toArray(new Point2D[sampleFan.size()])[rand];
			}
		},
		10,
		8,
		Math.toRadians(44)
	);
	canvas.draw(
		city,
		new DrawingAlgorithm<City>() {
			private final Color highLevelGraphColor = Color.RED;
			private final Color lowLevelGraphColor = Color.BLUE;
			private final DrawingAlgorithm<Cell> highLevelVertexDrawingAlgorithm =
				DrawingCell.withColorAndSize(
					highLevelGraphColor, 7
				);
			private final DrawingAlgorithm<Cell> lowLevelVertexDrawingAlgorithm =
				DrawingCell.withColorAndSize(
					lowLevelGraphColor, 4
				);

			@Override
			public void draw(City city) {
				for (Line2D edge : city.getHighLevelRoadGraph().edgeSet()) {
					drawLine(
						edge.start.x,
						edge.start.y,
						edge.end.x,
						edge.end.y,
						highLevelGraphColor
					);
				}
				for (Point2D vertex : city.getHighLevelRoadGraph().vertexSet()) {
					canvas.draw(
						new Cell((int) vertex.x, (int) vertex.y),
						highLevelVertexDrawingAlgorithm
					);
				}
				for (Line2D roadSegment : city.getLowLevelRoadGraph().edgeSet()) {
					drawLine(
						roadSegment.start.x,
						roadSegment.start.y,
						roadSegment.end.x,
						roadSegment.end.y,
						lowLevelGraphColor
					);
				}
				for (Point2D vertex : city.getLowLevelRoadGraph().vertexSet()) {
					canvas.draw(
						new Cell((int) vertex.x, (int) vertex.y),
						lowLevelVertexDrawingAlgorithm
					);
				}
			}
		}
	);
	canvas.draw(new PatonCycleBase<>(city.getLowLevelRoadGraph()).findCycleBase().get(1), new DrawingAlgorithm<java.util.List<Point2D>>() {
		@Override
		public void draw(List<Point2D> shape) {
			for (Point2D point : shape) {
				canvas.draw(
					new Cell((int)point.x, (int)point.y),
					DrawingCell.withColorAndSize(Color.GREEN, 8)
				);
			}
		}
	});

	try {
		Thread.sleep(200000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}
}
