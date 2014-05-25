package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class DrawingPoint2D {
    public static DrawingAlgorithm<Point2D> withColorAndSize(Color color, double diameter) {
        return (shape, canvas) -> canvas.fillShape(
                new Ellipse2D.Double(
                        shape.x - diameter / 2,
                        shape.y - diameter / 2,
                        diameter,
                        diameter
                ),
                color
        );
    }
}
