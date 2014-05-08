package org.tendiwa.drawing;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;

import java.awt.*;
import java.util.Iterator;
import java.util.function.Function;

public class DrawingGraph {
    public static <V, E> DrawingAlgorithm<UndirectedGraph<V, E>> withAliases(
            GraphConstructor<V, E> constructor,
            Function<V, Double> x,
            Function<V, Double> y
    ) {
        return (shape, canvas) -> {
            DrawingAlgorithm<Cell> pointDrawing = DrawingCell.withColorAndSize(Color.RED, 6);
            for (E e : shape.edgeSet()) {
                V source = shape.getEdgeSource(e);
                V target = shape.getEdgeTarget(e);
                canvas.drawLine(x.apply(source), y.apply(source), x.apply(target), y.apply(target), Color.RED);
            }
            for (V v : shape.vertexSet()) {
                canvas.draw(new Cell(
                        x.apply(v).intValue(),
                        y.apply(v).intValue()
                ), pointDrawing);
            }
            for (V v : shape.vertexSet()) {
                canvas.drawString(Integer.toString(constructor.aliasOf(v)), x.apply(v) + 5, y.apply(v) + 5,
                        Color.BLUE);
            }
        };
    }

    public static DrawingAlgorithm<? super UndirectedGraph<Point2D, Line2D>> withColor(Color color) {
        return withColorAndVertexSize(color, 6);
    }

    public static DrawingAlgorithm<UndirectedGraph<Point2D, Line2D>> withColorAndVertexSize(
            final Color color,
            int size
    ) {

        return (shape, canvas) -> {
            DrawingAlgorithm<Cell> pointDrawing = DrawingCell.withColorAndSize(color, size);
            for (Line2D e : shape.edgeSet()) {
                Point2D source = shape.getEdgeSource(e);
                Point2D target = shape.getEdgeTarget(e);
                canvas.drawLine(source.x + ((double) size) / 2, source.y + ((double) size) / 2, target.x,
                        target.y,
                        color);
            }
            for (Point2D v : shape.vertexSet()) {
                canvas.draw(new Cell((int) v.x, (int) v.y), pointDrawing);
            }
        };
    }

    public static DrawingAlgorithm<UndirectedGraph<Point2D, Line2D>> multicoloredEdges() {
        Iterator<Color> colors = Colors.infiniteSequence(i -> new Color(i * 45 % 255, i * 234 * 255, i * 123 % 255));
        return (graph, canvas) -> graph
                .edgeSet()
                .stream()
                .forEach(edge -> canvas.drawLine(
                        edge.start.toCell(),
                        edge.end.toCell(),
                        colors.next()
                ));
    }
}
