package org.tendiwa.demos.settlements;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.City;

import static java.awt.Color.BLACK;
import static java.awt.Color.GRAY;

public class CityDrawer implements DrawingAlgorithm<City> {

    @Override
    public void draw(City city, TestCanvas canvas) {
        city.getCells().stream()
                .forEach(c -> c.secondaryRoadNetwork().edgeSet().stream()
                        .forEach(line -> canvas.drawLine(line.start.toCell(), line.end.toCell(), BLACK))
                );
        for (Segment2D roadSegment : city.getLowLevelRoadGraph().edgeSet()) {
//            canvas.drawLine(roadSegment, GRAY);
        }
    }
}
