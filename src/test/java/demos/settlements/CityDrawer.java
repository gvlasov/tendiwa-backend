package demos.settlements;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.DrawingLine;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.settlements.City;

import java.awt.*;

public class CityDrawer extends DrawingAlgorithm<City> {

    @Override
    public void draw(City city) {
        city.getCells().stream()
                .forEach(c -> c.secondaryRoadNetwork().edgeSet().stream()
                        .forEach(line ->
                                canvas.draw(line, DrawingLine.withColor(Color.GREEN), canvas.TOP_LAYER)
                        ));
        for (Line2D roadSegment : city.getLowLevelRoadGraph().edgeSet()) {
            canvas.draw(roadSegment, DrawingLine.withColor(Color.RED));
        }
    }

    public CityDrawer() {

    }
}
