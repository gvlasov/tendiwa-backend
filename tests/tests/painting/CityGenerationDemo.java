package tests.painting;

import com.google.common.collect.ImmutableCollection;
import com.google.inject.Inject;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingCell;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.graphs.GraphConstructor;
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
        UndirectedGraph<Point2D, Line2D> graph = new GraphConstructor<>(Line2D::new)
                .vertex(0, new Point2D(100 + 10, 100 + 10))
                .vertex(1, new Point2D(100 + 10 + 20, 100 + 10))
                .vertex(2, new Point2D(100 + 10 + 20 + 20, 100 + 10 + 20))
                .vertex(3, new Point2D(100 + 10 + 20 + 20, 100 + 10 + 20 + 40))
                .vertex(4, new Point2D(100 + 10, 100 + 10 + 20 + 40))
                .vertex(5, new Point2D(100 + 10 + 20, 100 + 10 + 20 + 40 + 20))
                .vertex(6, new Point2D(100 + 71, 100 + 13))
                .vertex(7, new Point2D(100 + 100, 100 + 24))
                .vertex(8, new Point2D(100 + 109, 100 + 55))
                .vertex(9, new Point2D(100 + 84, 100 + 87))
                .edge(0, 1)
                .edge(1, 2)
                .edge(2, 3)
                .edge(3, 4)
                .edge(4, 0)
                .edge(3, 5)
                .edge(1, 6)
                .edge(6, 7)
                .edge(7, 8)
                .edge(8, 9)
                .edge(9, 5)
                .graph();
        RoadGraph roadGraph = new RoadGraph(graph.vertexSet(), graph.edgeSet());
        canvas.draw(0, new DrawingAlgorithm<Integer>() {
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
                3,
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
                            for (SecondaryRoad road : cityCell.secRoadNetwork.edgeSet()) {
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
