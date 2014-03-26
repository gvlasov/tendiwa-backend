package tests.graph;

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
import org.tendiwa.graphs.Filament;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.graphs.VertexPositionAdapter;

import java.awt.*;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class MinimumCycleBasisDemo {
    @Inject
    TestCanvas canvas;

    @Test
    public void draw() {
        final SimpleGraph<Point2D, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        Point2D p0 = new Point2D(90, 205);
        Point2D p1 = new Point2D(116, 205);
        Point2D p2 = new Point2D(211, 163);
        Point2D p3 = new Point2D(250, 221);
        Point2D p4 = new Point2D(298, 139);
        Point2D p5 = new Point2D(298, 221);
        graph.addVertex(new Point2D(120, 230));
        graph.addVertex(p0);
        graph.addVertex(p1);
        graph.addVertex(p2);
        graph.addVertex(p3);
        graph.addVertex(p4);
        graph.addVertex(p5);
        graph.addEdge(p0, p1);
        graph.addEdge(p1, p2);
        graph.addEdge(p2, p3);
        graph.addEdge(p3, p4);
        graph.addEdge(p4, p2);
        graph.addEdge(p3, p5);
        graph.addEdge(p4, p5);
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
                    System.out.println("filament");
                    for (DefaultEdge edge : filament) {
                        canvas.draw(new Line2D(
                                shape.getEdgeSource(edge),
                                shape.getEdgeTarget(edge)
                        ), DrawingLine.withColor(Color.GREEN));
                    }
                }
                for (MinimalCycle<Point2D, DefaultEdge> cycle : mcb.minimalCyclesSet()) {
                    System.out.println("min cycle");
                    for (DefaultEdge edge : cycle) {
                        canvas.draw(new Line2D(
                                shape.getEdgeSource(edge),
                                shape.getEdgeTarget(edge)
                        ), DrawingLine.withColor(Color.RED));
                    }
                }
            }
        });
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
