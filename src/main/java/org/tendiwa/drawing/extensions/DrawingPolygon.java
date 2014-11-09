package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.List;

public class DrawingPolygon {
	public static DrawingAlgorithm<List<Point2D>> withColor(Color color) {
		return (polygon, canvas) -> {
			int size = polygon.size();
			for (int i = 0; i < size; i++) {
				canvas.drawLine(
					polygon.get(i).toCell(),
					polygon.get(i + 1 == size ? 0 : i + 1).toCell(),
					color
				);
			}
		};
	}

	public static DrawingAlgorithm<List<Point2D>> verticesAndEdges(Color vertices, Color edges, double vertexSize) {
		return (polygon, canvas) -> {
			DrawingAlgorithm<Point2D> drawingVertices = DrawingPoint2D.withColorAndSize(vertices, vertexSize);
			int size = polygon.size();
			for (int i = 0; i < size; i++) {
				canvas.drawLine(
					polygon.get(i).toCell(),
					polygon.get(i + 1 == size ? 0 : i + 1).toCell(),
					edges
				);
			}
			for (int i = 0; i < size; i++) {
				canvas.draw(polygon.get(i), drawingVertices);
			}
		};
	}
}
