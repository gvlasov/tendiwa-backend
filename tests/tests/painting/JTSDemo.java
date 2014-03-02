package tests.painting;

import com.google.inject.Inject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.tendiwa.drawing.DrawingModule;
import org.tendiwa.drawing.TestCanvas;

@RunWith(JukitoRunner.class)
@UseModules(DrawingModule.class)
public class JTSDemo {
@Inject
TestCanvas canvas;
@Inject
GeometryFactory factory;
@Test
public void draw() {
	Coordinate[] coords = new Coordinate[] {
		new Coordinate(10, 20),
		new Coordinate(10+30, 20+5),
		new Coordinate(10+30-15, 20+5+20),
		new Coordinate(10+30-15+100, 20+5+20+100),
		new Coordinate(10, 20),
	};
	LinearRing ring = factory.createLinearRing(coords);
	Polygon polygon = new Polygon(ring, null, factory);
	canvas.draw(polygon);
	try {
		Thread.sleep(200000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}
}
