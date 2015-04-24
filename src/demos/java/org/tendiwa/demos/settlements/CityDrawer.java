package org.tendiwa.demos.settlements;

import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.smartMesh.SmartMesh2D;
import org.tendiwa.geometry.smartMesh.algorithms.SegmentNetworkAlgorithms;

import java.awt.Color;

public class CityDrawer implements DrawingAlgorithm<SmartMesh2D> {

	@Override
	public void draw(SmartMesh2D segment2DSmartMesh, Canvas canvas) {
//		Iterator<Color> colors = Iterators.cycle(Color.red, Color.blue, Color.green, Color.orange, Color.cyan, Color.black);
		segment2DSmartMesh.networks().stream()
			.forEach(c -> c.network().edgeSet().stream()
					.forEach(line -> {
//							canvas.drawRasterLine(line.start.toCell(), line.end.toCell(), colors.next());
					})
			);
		SegmentNetworkAlgorithms.createFullGraph(segment2DSmartMesh)
			.edgeSet()
			.forEach(e -> canvas.drawRasterLine(e, Color.red));
//		for (Point2D vertex : city.getOriginalGraph().vertexSet()) {
//			canvas.draw(vertex, DrawingPoint2D.withColorAndSize(Color.orange, 8));
//		}
	}
}
