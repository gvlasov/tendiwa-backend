package org.tendiwa.drawing.extensions;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.Colors;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;

import java.awt.*;
import java.awt.geom.Line2D;
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

    public static DrawingAlgorithm<? super UndirectedGraph<Point2D, org.tendiwa.geometry.Segment2D>> withColor(Color color) {
        return withColorAndVertexSize(color, 6);
    }

    public static DrawingAlgorithm<UndirectedGraph<Point2D, org.tendiwa.geometry.Segment2D>> withColorAndVertexSize(
            final Color color,
            int size
    ) {

        return (shape, canvas) -> {
            DrawingAlgorithm<Cell> pointDrawing = DrawingCell.withColorAndSize(color, size);
            for (org.tendiwa.geometry.Segment2D e : shape.edgeSet()) {
                Point2D source = shape.getEdgeSource(e);
                Point2D target = shape.getEdgeTarget(e);
                canvas.drawLine(
                        source.x,
                        source.y,
                        target.x,
                        target.y,
                        color
                );
            }
            for (Point2D v : shape.vertexSet()) {
//                canvas.draw(new Cell((int) Math.round(v.x), (int) Math.round(v.y)), pointDrawing);
            }
        };
    }
    public static DrawingAlgorithm<UndirectedGraph<Point2D, org.tendiwa.geometry.Segment2D>> withColorAndAntialiasing(
            final Color color
    ) {

        return (shape, canvas) -> {
            for (org.tendiwa.geometry.Segment2D e : shape.edgeSet()) {
                Point2D source = shape.getEdgeSource(e);
                Point2D target = shape.getEdgeTarget(e);
                Line2D.Double shape1 = new Line2D.Double(source.x, source.y, target.x, target.y);
                canvas.drawShape(
                        shape1,
                        color
                );
            }
        };
    }

    public static DrawingAlgorithm<UndirectedGraph<Point2D, org.tendiwa.geometry.Segment2D>> multicoloredEdges() {
        Iterator<Color> colors = Colors.infiniteSequence(i -> new Color(
			(i * 451 + 122) % 255,
			(i * 234 + 200) % 255,
			(i * 123 + 178) % 255
		));
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
