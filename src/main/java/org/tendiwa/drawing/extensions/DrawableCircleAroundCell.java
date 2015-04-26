package org.tendiwa.drawing.extensions;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.geometry.BasicCircle;
import org.tendiwa.geometry.Circle;

import java.awt.Color;

final class DrawableCircleAroundCell implements Drawable {
	private final Color color;
	private final Circle circle;

	protected DrawableCircleAroundCell(Cell cell, double radius, Color color) {
		this.circle = new BasicCircle(cell.toPoint(), radius);
		this.color = color;
	}

	@Override
	public void drawIn(Canvas canvas) {
		canvas.drawCircle(circle, color);
	}
}
