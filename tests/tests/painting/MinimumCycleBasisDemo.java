package tests.painting;

import com.google.inject.Inject;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.*;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.*;

import java.awt.*;
import java.util.stream.Collectors;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class MinimumCycleBasisDemo {
    @Inject
    TestCanvas canvas;

    @Test
    public void draw() {
        final GraphConstructor<Point2D, DefaultEdge> constructor =
                GraphConstructor.<Point2D>create()
                        .vertex(0, new Point2D(20, 20))
                        .vertex(1, new Point2D(30, 50)).withEdges(2)
                        .vertex(2, new Point2D(70, 55)).withEdges(4, 7)
                        .vertex(3, new Point2D(15, 90)).withEdges(1)
                        .vertex(4, new Point2D(85, 90)).withEdges(3, 5)
                        .vertex(5, new Point2D(50, 70)).withEdges(6)
                        .vertex(6, new Point2D(35, 80))
                        .vertex(7, new Point2D(100, 50)).withEdges(11)
                        .vertex(8, new Point2D(100, 70)).withEdges(9)
                        .vertex(9, new Point2D(90, 110)).withEdges(10)
                        .vertex(10, new Point2D(110, 109)).withEdges(8)
                        .vertex(11, new Point2D(120, 55)).withEdges(12, 13)
                        .vertex(12, new Point2D(125, 90)).withEdges(13, 20)
                        .vertex(13, new Point2D(150, 50)).withEdges(18)
                        .vertex(14, new Point2D(180, 120)).withEdges(15)
                        .vertex(15, new Point2D(200, 100)).withEdges(16)
                        .vertex(16, new Point2D(220, 110))
                        .vertex(17, new Point2D(160, 75))
                        .vertex(18, new Point2D(190, 70)).withEdges(19)
                        .vertex(19, new Point2D(220, 50)).withEdges(20, 21)
                        .vertex(20, new Point2D(230, 85)).withEdges(21, 22, 24)
                        .vertex(21, new Point2D(240, 40))
                        .vertex(22, new Point2D(230, 130)).withEdges(23)
                        .vertex(23, new Point2D(300, 130)).withEdges(24)
                        .vertex(24, new Point2D(300, 85))
                        .vertex(25, new Point2D(265, 90)).withEdges(26, 27)
                        .vertex(26, new Point2D(250, 110)).withEdges(27)
                        .vertex(27, new Point2D(280, 110));
        final SimpleGraph<Point2D, DefaultEdge> graph = constructor
                .graph();
        canvas.draw(graph, new DrawingAlgorithm<SimpleGraph<Point2D, DefaultEdge>>() {
            @Override
            public void draw(SimpleGraph<Point2D, DefaultEdge> shape) {
                MinimumCycleBasis<Point2D, DefaultEdge> mcb =
                        new MinimumCycleBasis<>(graph, new VertexPositionAdapter<Point2D>() {
                            @Override
                            public double getX(Point2D vertex) {
                                return vertex.x;
                            }

                            @Override
                            public double getY(Point2D vertex) {
                                return vertex.y;
                            }
                        });
                for (Point2D p : mcb.isolatedVertexSet()) {
                    canvas.draw(new Cell(
                            (int) p.x,
                            (int) p.y
                    ), DrawingCell.withColorAndSize(Color.BLUE, 3));
                }
                for (Filament<Point2D, DefaultEdge> filament : mcb.filamentsSet()) {
                    System.out.println("filament " +
                            filament.queue
                                    .stream()
                                    .map(constructor::aliasOf)
                                    .collect(Collectors.toSet())
                    );
                    for (DefaultEdge edge : filament) {
                        canvas.draw(new Line2D(
                                shape.getEdgeSource(edge),
                                shape.getEdgeTarget(edge)
                        ), DrawingLine.withColor(Color.GREEN));
                    }
                }
                for (MinimalCycle<Point2D, DefaultEdge> cycle : mcb.minimalCyclesSet()) {
                    System.out.println("min cycle " +
                            cycle.cycle
                                    .stream()
                                    .map(constructor::aliasOf)
                                    .collect(Collectors.toSet())
                    );
                    for (DefaultEdge edge : cycle) {
                        canvas.draw(new Line2D(
                                shape.getEdgeSource(edge),
                                shape.getEdgeTarget(edge)
                        ), DrawingLine.withColor(Color.RED));
                    }
                }
                for (Point2D p : graph.vertexSet()) {
                    this.drawString(
                            Integer.toString(constructor.aliasOf(p)),
                            p.x + 5,
                            p.y + 5,
                            Color.BLUE
                    );
                }
            }
        });
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
