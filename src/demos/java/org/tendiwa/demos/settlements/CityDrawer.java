package org.tendiwa.demos.settlements;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.CityGeometry;

import java.awt.Color;

public class CityDrawer implements DrawingAlgorithm<CityGeometry> {

    @Override
    public void draw(CityGeometry cityGeometry, TestCanvas canvas) {
        cityGeometry.getCells().stream()
                .forEach(c -> c.network().edgeSet().stream()
                        .forEach(line -> canvas.drawLine(line.start.toCell(), line.end.toCell(), Color.red))
                );
        for (Segment2D roadSegment : cityGeometry.getLowLevelRoadGraph().edgeSet()) {
            canvas.drawLine(roadSegment, Color.RED);
        }
//		for (Point2D vertex : city.getLowLevelRoadGraph().vertexSet()) {
//			canvas.draw(vertex, DrawingPoint2D.withColorAndSize(Color.orange, 8));
//		}
    }
}
