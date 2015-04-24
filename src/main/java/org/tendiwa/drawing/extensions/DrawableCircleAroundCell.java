package org.tendiwa.drawing.extensions;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.geometry.Cell_Wr;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

final class DrawableCircleAroundCell extends Cell_Wr implements Drawable {
	private final Color color;
	private final double diameter;

	protected DrawableCircleAroundCell(Cell cell, Color color, double diameter) {
		super(cell);
		this.color = color;
		this.diameter = diameter;
	}

	@Override
	public void drawIn(Canvas canvas) {
		double halfDiameter = diameter / 2;
		canvas.fillShape(
			new Ellipse2D.Double(
				this.x() - halfDiameter,
				this.y() - halfDiameter,
				diameter,
				diameter
			),
			color
		);
	}
}
