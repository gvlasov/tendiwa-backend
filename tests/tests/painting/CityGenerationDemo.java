package tests.painting;

import com.google.common.collect.ImmutableCollection;
import com.google.inject.Inject;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.core.meta.Chance;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingCell;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.settlements.*;

import java.awt.*;
import java.util.Random;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class CityGenerationDemo {
@Inject
TestCanvas canvas;

@Test
public void draw() {
	RoadGraph roadGraph = new RoadGraph(
		new Point2D[]{
			new Point2D(100 + 10, 100 + 10),
			new Point2D(100 + 10 + 20, 100 + 10),
			new Point2D(100 + 10 + 20 + 20, 100 + 10 + 20),
			new Point2D(100 + 10 + 20 + 20, 100 + 10 + 20 + 40),
			new Point2D(100 + 10, 100 + 10 + 20 + 40),
			new Point2D(100 + 10 + 20, 100 + 10 + 20 + 40 + 20),
			new Point2D(100 + 71, 100 + 13),
			new Point2D(100 + 100, 100 + 24),
			new Point2D(100 + 109, 100 + 55),
			new Point2D(100 + 84, 100 + 87),
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
	canvas.draw(new Integer(0), new DrawingAlgorithm<Integer>() {
		@Override
		public void draw(Integer shape) {
			drawRectangle(new Rectangle(0, 0, canvas.width, canvas.height), Color.BLACK);
		}
	});
	final Random srand = new Random(2);
	City city = new City(
		roadGraph,
		new SampleSelectionStrategy() {
			@Override
			public Point2D selectNextPoint(ImmutableCollection<Point2D> sampleFan) {
				int rand = srand.nextInt(sampleFan.size());
				return sampleFan.toArray(new Point2D[sampleFan.size()])[rand];
			}
		},
		30,
		8,
		Math.toRadians(20),
		new Random(10),
		5,
		1.0,
		10,
		4,
		canvas
	);
	canvas.draw(
		city,
		new DrawingAlgorithm<City>() {
			private final Color highLevelGraphColor = Color.BLUE;
			private final Color lowLevelGraphColor = Color.RED;
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
//					drawLine(
//						edge.start.x,
//						edge.start.y,
//						edge.end.x,
//						edge.end.y,
//						highLevelGraphColor
//					);
				}
				for (Point2D vertex : city.getHighLevelRoadGraph().vertexSet()) {
//					canvas.draw(
//						new Cell((int) vertex.x, (int) vertex.y),
//						highLevelVertexDrawingAlgorithm
//					);
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
//					canvas.draw(
//						new Cell((int) vertex.x, (int) vertex.y),
//						lowLevelVertexDrawingAlgorithm
//					);
				}
				for (CityCell cityCell : city.getCells()) {
					for (SecondaryRoad road : cityCell.roadCycle.edgeSet()) {
						Line2D line = road.toLine();
						if (!road.start.isDeadEnd || !road.end.isDeadEnd) {
							drawLine(
								line.start.x,
								line.start.y,
								line.end.x,
								line.end.y,
								Color.GREEN
							);
						}
					}
//					for (Point2D point : cityCell.roadCycle.vertexSet()) {
//						canvas.draw(new Cell((int) point.x, (int) point.y), lowLevelVertexDrawingAlgorithm);
//					}

				}

			}
		}
	);
//	canvas.draw(new PatonCycleBase<>(city.getLowLevelRoadGraph()).findCycleBase().get(1), new DrawingAlgorithm<java.util.List<Point2D>>() {
//		@Override
//		public void draw(List<Point2D> shape) {
//			for (Point2D point : shape) {
//				canvas.draw(
//					new Cell((int)point.x, (int)point.y),
//					DrawingCell.withColorAndSize(Color.GREEN, 8)
//				);
//			}
//		}
//	});

	try {
		Thread.sleep(200000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}
}
