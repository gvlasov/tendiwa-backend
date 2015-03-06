package org.tendiwa.drawing.extensions;

import org.tendiwa.core.meta.Utils;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import sun.java2d.loops.DrawPolygons;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.List;

public class DrawingPolygon {
	public static DrawingAlgorithm<List<Point2D>> withColor(Color color) {
		return (polygon, canvas) -> {
			int size = polygon.size();
			for (int i = 0; i < size; i++) {
				canvas.drawRasterLine(
					polygon.get(i).toCell(),
					polygon.get(i + 1 == size ? 0 : i + 1).toCell(),
					color
				);
			}
		};
	}

	public static DrawingAlgorithm<List<Point2D>> withColorNonRaster(Color color) {
		return (polygon, canvas) -> {
			DrawingAlgorithm<Segment2D> drawingSegment = DrawingSegment2D.withColorThin(color);
			int size = polygon.size();
			for (int i = 0; i < size; i++) {
				Point2D point1 = polygon.get(i);
				Point2D point2 = polygon.get(Utils.nextIndex(i, size));
				// TODO: draw line
				canvas.draw(
					new Segment2D(point1, point2),
					drawingSegment
				);
			}
		};
	}

	public static DrawingAlgorithm<List<Point2D>> verticesAndEdges(Color vertices, Color edges, double vertexSize) {
		return (polygon, canvas) -> {
			DrawingAlgorithm<Point2D> drawingVertices = DrawingPoint2D.withColorAndSize(vertices, vertexSize);
			int size = polygon.size();
			for (int i = 0; i < size; i++) {
				canvas.drawRasterLine(
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
