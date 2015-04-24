package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;

public final class DrawableText implements Drawable {
	private final String text;
	private final Point2D topLeftCorner;
	private final Color color;

	public DrawableText(String text, Point2D topLeftCorner, Color color) {
		this.text = text;
		this.topLeftCorner = topLeftCorner;
		this.color = color;
	}

	@Override
	public void drawIn(Canvas canvas) {
		canvas.drawString(
			text,
			topLeftCorner,
			color
		);
	}
}
