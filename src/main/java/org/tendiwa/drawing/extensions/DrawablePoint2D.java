package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.*;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public final class DrawablePoint2D extends Point2D_Wr implements Drawable {

	private final Color color;

	public DrawablePoint2D(Point2D point, Color color) {
		super(point);
		this.color = color;
	}

	@Override
	public void drawIn(DrawableInto canvas) {
		canvas.drawCell(toCell(), color);
	}

	public static final class Circle extends Point2D_Wr implements Drawable {
		private final Color color;
		private final double diameter;

		public Circle(Point2D center, Color color, double diameter) {
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
				new DrawableSegment2D(
					marker.post(),
					markerColor
				)
			);
			canvas.drawRectangle2D(marker, markerColor);
			canvas.drawString(text, marker.textStart(), textColor);
		}

	}

}
