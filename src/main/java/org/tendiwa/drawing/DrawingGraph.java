package org.tendiwa.drawing;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Cell;
import org.tendiwa.graphs.GraphConstructor;

import java.awt.*;
import java.util.function.Function;

/**
 * Created by suseika on 3/28/14.
 */
public class DrawingGraph {
    public static <V, E> DrawingAlgorithm<UndirectedGraph<V, E>> withAliases(
            GraphConstructor<V, E> constructor,
            Function<V, Double> x,
            Function<V, Double> y
    ) {
        return new DrawingAlgorithm<UndirectedGraph<V, E>>() {
            @Override
            public void draw(UndirectedGraph<V, E> shape) {
                DrawingAlgorithm<Cell> pointDrawing = DrawingCell.withColorAndSize(Color.RED, 6);
                for (E e : shape.edgeSet()) {
                    V source = shape.getEdgeSource(e);
                    V target = shape.getEdgeTarget(e);
                    drawLine(x.apply(source), y.apply(source), x.apply(target), y.apply(target), Color.RED);
                }
                for (V v : shape.vertexSet()) {
                    canvas.draw(new Cell(
                            x.apply(v).intValue(),
                            y.apply(v).intValue()
                    ), pointDrawing);
                }
                for (V v : shape.vertexSet()) {
                    drawString(Integer.toString(constructor.aliasOf(v)), x.apply(v) + 5, y.apply(v) + 5, Color.BLUE);
                }
            }
        };
    }
}
