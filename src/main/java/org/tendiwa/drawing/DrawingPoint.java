package org.tendiwa.drawing;

import org.tendiwa.geometry.Point2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class DrawingPoint {
    public static DrawingAlgorithm<Point2D> withColorAndSize(Color color, double diameter) {
        return new DrawingAlgorithm<Point2D>() {
            @Override
            public void draw(Point2D shape) {
                fillShape(
                        new Ellipse2D.Double(
                                shape.x - diameter / 2,
                                shape.y - diameter / 2,
                                diameter,
                                diameter
                        ),
                        color
                );
            }
        };
    }
}
