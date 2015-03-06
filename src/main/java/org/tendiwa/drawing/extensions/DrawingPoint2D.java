package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Rectangle2D;
import org.tendiwa.geometry.Segment2D;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class DrawingPoint2D {
	public static DrawingAlgorithm<Point2D> withColorAndSize(Color color, double diameter) {
		return (shape, canvas) -> {
			canvas.fillShape(
				new Ellipse2D.Double(
					shape.x - diameter / 2 + 0.5,
					shape.y - diameter / 2 + 0.5,
					diameter,
					diameter
				),
				color
			);
		};
	}

	public static DrawingAlgorithm<Point2D> withTextMarker(
		String text,
		Color textColor,
		Color markerColor
	) {
		return (point, canvas) -> {
			Marker marker = new Marker(point, text, canvas);

			canvas.draw(
				marker.post,
				DrawingSegment2D.withColorThin(markerColor)
			);
			canvas.drawRectangle2D(marker.box, markerColor);
			canvas.drawString(
				text,
				marker.stringStart.x,
				marker.stringStart.y,
				textColor
			);
		};
	}

	private static final class Marker {
		final Rectangle2D box;
		final Segment2D post;
		final Point2D stringStart;

		public Marker(Point2D point, String text, DrawableInto canvas) {
			double tailHeight = 10. / canvas.getScale();
			double textWidth = ((double) canvas.textWidth(text)) / canvas.getScale();
			double lineHeight = ((double) canvas.textLineHeight()) / canvas.getScale();
			double padding = 2./canvas.getScale();

			post = Segment2D.create(
				point.x,
				point.y,
				point.x,
				point.y - tailHeight
			);
			double boxHeight = lineHeight + padding * 2;
			double boxWidth = textWidth + padding * 2;
			box = new Rectangle2D(
				post.end.x - boxWidth / 2,
				post.end.y - boxHeight,
				boxWidth,
				boxHeight
			);
			stringStart = new Point2D(
				box.x+padding,
				box.y+boxHeight-padding
			);
		}
	}
}
