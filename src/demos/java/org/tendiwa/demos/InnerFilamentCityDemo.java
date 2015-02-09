package org.tendiwa.demos;

import org.tendiwa.demos.settlements.CityDrawer;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.settlements.networks.SegmentNetworkBuilder;
import org.tendiwa.settlements.networks.SegmentNetwork;

public class InnerFilamentCityDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(InnerFilamentCityDemo.class);
	}

	@Override
	public void run() {
		GraphConstructor<Point2D, Segment2D> gc = new GraphConstructor<>(Segment2D::new)
			.vertex(0, new Point2D(200, 100))
			.vertex(1, new Point2D(400, 100))
			.vertex(2, new Point2D(500, 200))
			.vertex(3, new Point2D(500, 400))
			.vertex(4, new Point2D(400, 500))
			.vertex(5, new Point2D(200, 500))
			.vertex(6, new Point2D(100, 400))
			.vertex(7, new Point2D(100, 200))
			.vertex(8, new Point2D(300, 200))
			.vertex(9, new Point2D(400, 450))
			.cycle(0, 1, 2, 3, 4, 5, 6, 7)
			.edge(0, 8)
			.edge(6, 9);
		TestCanvas canvas = Demos.createCanvas();
		SegmentNetwork segmentNetwork = new SegmentNetworkBuilder(gc.graph())
			.withDefaults()
			.withRoadSegmentLength(50)
			.build();
		canvas.draw(segmentNetwork, new CityDrawer());
	}
}
