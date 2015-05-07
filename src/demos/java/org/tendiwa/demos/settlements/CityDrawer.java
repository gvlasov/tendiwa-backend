package org.tendiwa.demos.settlements;

import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.smartMesh.SmartMeshedNetwork;

import java.awt.Color;

public class CityDrawer implements DrawingAlgorithm<SmartMeshedNetwork> {

	@Override
	public void draw(SmartMeshedNetwork segment2DSmartMesh, Canvas canvas) {
//		Iterator<Color> colors = Iterators.cycle(Color.red, Color.blue, Color.green, Color.orange, Color.cyan, Color.black);
		segment2DSmartMesh.meshes().stream()
			.forEach(mesh -> mesh.edgeSet().stream()
					.forEach(line -> {
//							canvas.drawRasterLine(line.start.toCell(), line.end.toCell(), colors.next());
					})
			);
		segment2DSmartMesh.fullGraph()
			.edgeSet()
			.forEach(e -> canvas.drawRasterLine(e, Color.red));
//		for (Point2D vertex : city.getOriginalGraph().vertexSet()) {
//			canvas.draw(vertex, DrawingPoint2D.withColorAndSize(Color.orange, 8));
//		}
	}
}
