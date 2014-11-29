package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;

import java.awt.*;
import java.awt.geom.Line2D;

public class DrawingSegment2D {
	public static DrawingAlgorithm<Segment2D> withColorThin(final Color color) {
		return (shape, canvas) ->
			canvas.drawShape(
				new Line2D.Double(
					shape.start.x,
					shape.start.y,
					shape.end.x,
					shape.end.y
				),
				color
			);
	}

	public static DrawingAlgorithm<Segment2D> withColorDirected(final Color color) {
		return (shape, canvas) -> {
			Vector2D vector = shape.asVector();
			canvas.draw(shape, DrawingSegment2D.withColorThin(color));
			Segment2D leftHalfarrow = new Segment2D(
				shape.end,
				shape.end
					.add(vector.multiply(-2 / vector.magnitude()))
					.add(vector.cross().multiply(2 / vector.magnitude()))
			);
			Segment2D rightHalfarrow = new Segment2D(
				shape.end,
				shape.end
					.add(vector.multiply(-2 / vector.magnitude()))
					.add(vector.cross().multiply(-2 / vector.magnitude()))
			);
			canvas.draw(leftHalfarrow, DrawingSegment2D.withColorThin(color));
			canvas.draw(rightHalfarrow, DrawingSegment2D.withColorThin(color));
		};
	}

	public static DrawingAlgorithm<Segment2D> withColor(final Color color) {
		return (shape, canvas) ->
			canvas.drawRasterLine(shape, color);
	}
}
