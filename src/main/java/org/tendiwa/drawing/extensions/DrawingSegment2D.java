package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Vector2D;

import java.awt.Color;
import java.awt.geom.Line2D;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public class DrawingSegment2D {
	public static DrawingAlgorithm<Segment2D> withColorThin(final Color color) {
		return (shape, canvas) ->
			canvas.drawShape(
				new Line2D.Double(
					shape.start().x() + 0.5,
					shape.start().y() + 0.5,
					shape.end().x() + 0.5,
					shape.end().y() + 0.5
				),
				color
			);
	}

	public static DrawingAlgorithm<Segment2D> withColorDirected(final Color color, double arrowheadLength) {
		return (shape, canvas) -> {
			Vector2D vector = shape.asVector();
			canvas.draw(shape, DrawingSegment2D.withColorThin(color));
			Segment2D leftHalfarrow = segment2D(
				shape.end(),
				shape.end()
					.add(vector.multiply(-arrowheadLength / vector.magnitude()))
					.add(vector.rotateQuarterClockwise().multiply(arrowheadLength / vector.magnitude()))
			);
			Segment2D rightHalfarrow = segment2D(
				shape.end(),
				shape.end()
					.add(vector.multiply(-arrowheadLength / vector.magnitude()))
					.add(vector.rotateQuarterClockwise().multiply(-arrowheadLength / vector.magnitude()))
			);
			canvas.draw(leftHalfarrow, DrawingSegment2D.withColorThin(color));
			canvas.draw(rightHalfarrow, DrawingSegment2D.withColorThin(color));
		};
	}

}
