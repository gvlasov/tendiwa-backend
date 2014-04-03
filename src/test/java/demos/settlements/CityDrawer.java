package demos.settlements;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.settlements.City;

import java.awt.*;

public class CityDrawer extends DrawingAlgorithm<City> {

    @Override
    public void draw(City city) {
        city.getCells().stream()
                .forEach(c -> c.secondaryRoadNetwork().edgeSet().stream()
                        .forEach(line ->
                                drawLine(
                                        line.start.x,
                                        line.start.y,
                                        line.end.x,
                                        line.end.y,
                                        Color.GREEN
                                )
                        ));
        for (Line2D roadSegment : city.getLowLevelRoadGraph().edgeSet()) {
            drawLine(
                    roadSegment.start.x,
                    roadSegment.start.y,
                    roadSegment.end.x,
                    roadSegment.end.y,
                    Color.RED
            );
        }
    }

    public CityDrawer() {

    }
}
