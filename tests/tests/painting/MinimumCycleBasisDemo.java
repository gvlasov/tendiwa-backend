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
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.*;

import java.awt.*;
import java.util.stream.Collectors;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class MinimumCycleBasisDemo {
    @Inject
    TestCanvas canvas;

    /**
     * Draws example from
     * <a href="https://docs.google.com/viewer?url=www.geometrictools.com%2FDocumentation%2FMinimalCycleBasis.pdf&embedded=true#:0.page.4">page
     * 4 of [Eberly 2005], Figure 2.1</a>
     *
     * @see org.tendiwa.graphs.MinimumCycleBasis
     */
    @Test
    public void draw() {
        final GraphConstructor<Point2D, DefaultEdge> constructor =
                GraphConstructor.<Point2D>createDefault()
                        .vertex(0, new Point2D(20, 20))
                        .vertex(1, new Point2D(30, 50))
                        .vertex(2, new Point2D(70, 55))
                        .vertex(3, new Point2D(15, 90))
                        .vertex(4, new Point2D(85, 90))
                        .vertex(5, new Point2D(50, 70))
                        .vertex(6, new Point2D(35, 80))
                        .vertex(7, new Point2D(100, 50))
                        .vertex(8, new Point2D(100, 70))
                        .vertex(9, new Point2D(90, 110))
                        .vertex(10, new Point2D(110, 109))
                        .vertex(11, new Point2D(120, 55))
                        .vertex(12, new Point2D(125, 90))
                        .vertex(13, new Point2D(150, 50))
                        .vertex(14, new Point2D(180, 120))
                        .vertex(15, new Point2D(200, 100))
                        .vertex(16, new Point2D(220, 110))
                        .vertex(17, new Point2D(160, 75))
                        .vertex(18, new Point2D(190, 70))
                        .vertex(19, new Point2D(220, 50))
                        .vertex(20, new Point2D(230, 85))
                        .vertex(21, new Point2D(240, 40))
                        .vertex(22, new Point2D(230, 130))
                        .vertex(23, new Point2D(300, 130))
                        .vertex(24, new Point2D(300, 85))
                        .vertex(25, new Point2D(265, 90))
                        .vertex(26, new Point2D(250, 110))
                        .vertex(27, new Point2D(280, 110))
                        .cycle(1, 2, 4, 3)
                        .path(4, 5, 6)
                        .cycle(8, 9, 10)
                        .path(2, 7, 11)
                        .cycle(11, 12, 13)
                        .cycle(12, 13, 18, 19, 20)
                        .cycle(19, 21, 20)
                        .cycle(20, 24, 23, 22)
                        .cycle(25, 26, 27)
                        .path(14, 15, 16);
        final SimpleGraph<Point2D, DefaultEdge> graph = constructor
                .graph();
        canvas.draw(graph, (shape, canvas1) -> {
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
                        filament.vertexList()
                                .stream()
                                .map(constructor::aliasOf)
                                .collect(Collectors.toSet())
                );
                for (DefaultEdge edge : filament) {
                    canvas.draw(new Segment2D(
                            shape.getEdgeSource(edge),
                            shape.getEdgeTarget(edge)
                    ), DrawingLine2D.withColor(Color.GREEN));
                }
            }
            for (MinimalCycle<Point2D, DefaultEdge> cycle : mcb.minimalCyclesSet()) {
                System.out.println("min cycle " +
                        cycle.vertexList()
                                .stream()
                                .map(constructor::aliasOf)
                                .collect(Collectors.toSet())
                );
                for (DefaultEdge edge : cycle) {
                    canvas.draw(new Segment2D(
                            shape.getEdgeSource(edge),
                            shape.getEdgeTarget(edge)
                    ), DrawingLine2D.withColor(Color.RED));
                }
            }
            for (Point2D p : graph.vertexSet()) {
                canvas.drawString(
                        Integer.toString(constructor.aliasOf(p)),
                        p.x + 5,
                        p.y + 5,
                        Color.BLUE
                );
            }
        });
        try {
            Thread.sleep(1000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
