package org.tendiwa.demos.settlements;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.City;

import java.awt.Color;

import static java.awt.Color.BLACK;

public class CityDrawer implements DrawingAlgorithm<City> {

    @Override
    public void draw(City city, TestCanvas canvas) {
        city.getCells().stream()
                .forEach(c -> c.network().edgeSet().stream()
                        .forEach(line -> canvas.drawLine(line.start.toCell(), line.end.toCell(), Color.red))
                );
        for (Segment2D roadSegment : city.getLowLevelRoadGraph().edgeSet()) {
            canvas.drawLine(roadSegment, Color.RED);
        }
//		for (Point2D vertex : city.getLowLevelRoadGraph().vertexSet()) {
//			canvas.draw(vertex, DrawingPoint2D.withColorAndSize(Color.orange, 8));
//		}
    }
}
