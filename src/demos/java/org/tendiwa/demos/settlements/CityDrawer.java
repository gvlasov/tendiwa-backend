package org.tendiwa.demos.settlements;

import com.google.common.collect.Iterators;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.PathGeometry;

import java.awt.Color;
import java.util.Iterator;

public class CityDrawer implements DrawingAlgorithm<PathGeometry> {

    @Override
    public void draw(PathGeometry pathGeometry, TestCanvas canvas) {
		Iterator<Color> colors = Iterators.cycle(Color.red, Color.blue, Color.green, Color.orange, Color.cyan, Color.black);
        pathGeometry.getNetworks().stream()
                .forEach(c -> c.network().edgeSet().stream()
                        .forEach(line -> {
//							canvas.drawLine(line.start.toCell(), line.end.toCell(), colors.next());
						})
                );
        for (Segment2D roadSegment : pathGeometry.getLowLevelRoadGraph().edgeSet()) {
//            canvas.drawLine(roadSegment, colors.next());
//			System.out.println(1);
		}
		pathGeometry.getFullRoadGraph().edgeSet().forEach(e->canvas.drawLine(e, colors.next()));
//		for (Point2D vertex : city.getLowLevelRoadGraph().vertexSet()) {
//			canvas.draw(vertex, DrawingPoint2D.withColorAndSize(Color.orange, 8));
//		}
    }
}
