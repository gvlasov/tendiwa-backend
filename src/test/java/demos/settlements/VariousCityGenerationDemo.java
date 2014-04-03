package demos.settlements;

import demos.Demos;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.City;
import org.tendiwa.settlements.CityBuilder;

import java.awt.*;

public class VariousCityGenerationDemo {
    public static void main(String[] args) {
        TestCanvas canvas = Demos.createCanvas();
        GraphConstructor<Point2D, Line2D> gc = new GraphConstructor<>(Line2D::new)
                .vertex(0, new Point2D(110, 110))
                .vertex(1, new Point2D(130, 110))
                .vertex(2, new Point2D(150, 130))
                .vertex(3, new Point2D(150, 170))
                .vertex(4, new Point2D(110, 170))
                .vertex(5, new Point2D(130, 190))
                .vertex(6, new Point2D(171, 113))
                .vertex(7, new Point2D(200, 124))
                .vertex(8, new Point2D(209, 155))
                .vertex(9, new Point2D(184, 187))
//                .vertex(10, new Point2D(184-28, 187-30))
//                .edge(6, 10)
                .cycle(0, 1, 2, 3, 4)
                .cycle(3, 5, 9, 8, 7, 6, 1, 2);
        canvas.fillBackground(Color.BLACK);
        City city = new CityBuilder(gc.graph(), canvas)
                .withDefaults()
                .withParamDegree(5)
                .build();
        canvas.draw(city, new CityDrawer());
    }
}
