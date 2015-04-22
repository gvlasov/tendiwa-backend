package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.*;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import static org.tendiwa.geometry.GeometryPrimitives.*;

public final class DrawablePoint2D extends Point2D_Wr implements Drawable {

	private final Color color;

	public DrawablePoint2D(Point2D point, Color color) {
		super(point);
		this.color = color;
	}

	@Override
	public void drawIn(DrawableInto canvas) {
	}

	public static final class Circle extends Point2D_Wr implements Drawable {
		private final Color color;
		private final int diameter;

		public Circle(Point2D center, Color color, int diameter) {
			super(center);
			this.color = color;
			this.diameter = diameter;
		}

		@Override
		public void drawIn(DrawableInto canvas) {
			canvas.fillShape(
				new Ellipse2D.Double(
					this.x() - diameter,
					this.y() - diameter,
					diameter,
					diameter
				),
				color
			);
		}
	}
	public static final class Billboard extends Point2D_Wr implements Drawable {

		private final String text;
		private final Color textColor;
		private final Color markerColor;

		public Billboard(
			Point2D point,
			String text,
			Color textColor,
			Color markerColor
		) {
			super(point);
			this.text = text;
			this.textColor = textColor;
			this.markerColor = markerColor;
		}

		@Override
		public void drawIn(DrawableInto canvas) {
			Marker marker = new Marker(this, text, canvas);

			canvas.draw(
				marker.post,
				DrawingSegment2D.withColorThin(markerColor)
			);
			canvas.drawRectangle2D(marker.box, markerColor);
			canvas.drawString(
				text,
				marker.stringStart.x(),
				marker.stringStart.y(),
				textColor
			);
		}
		private static final class Marker {
			final BasicRectangle2D box;
			final Segment2D post;
			final Point2D stringStart;

			public Marker(Point2D point, String text, DrawableInto canvas) {
				double tailHeight = 10. / canvas.getScale();
				double textWidth = ((double) canvas.textWidth(text)) / canvas.getScale();
				double lineHeight = ((double) canvas.textLineHeight()) / canvas.getScale();
				double padding = 2. / canvas.getScale();

				post = segment2D(
					point.x(),
					point.y(),
					point.x(),
					point.y() - tailHeight
				);
				double boxHeight = lineHeight + padding * 2;
				double boxWidth = textWidth + padding * 2;
				box = new BasicRectangle2D(
					post.end().x() - boxWidth / 2,
					post.end().y() - boxHeight,
					boxWidth,
					boxHeight
				);
				stringStart = point2D(
					box.x + padding,
					box.y + boxHeight - padding
				);
			}
		}
	}
}
