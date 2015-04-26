package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.geometry.BasicCircle;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Point2D_Wr;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

public final class DrawablePoint2D extends Point2D_Wr implements Drawable {

	private final Color color;

	public DrawablePoint2D(Point2D point, Color color) {
		super(point);
		this.color = color;
	}

	@Override
	public void drawIn(Canvas canvas) {
		canvas.drawCell(toCell(), color);
	}

	public static final class Circle extends Point2D_Wr implements Drawable {
		private final Color color;
		private final double radius;

		public Circle(Point2D center, Color color, double radius) {
			super(center);
			this.color = color;
			this.radius = radius;
		}

		@Override
		public void drawIn(Canvas canvas) {
			canvas.drawCircle(new BasicCircle(this, radius), color);
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
		public void drawIn(Canvas canvas) {
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
