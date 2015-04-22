package org.tendiwa.drawing.extensions;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.Cell_Wr;

import java.awt.Color;

public final class DrawableCell extends Cell_Wr implements Drawable {
	private final Color color;

	public DrawableCell(Cell cell, Color color) {
		super(cell);
		this.color = color;
	}

	@Override
	public void drawIn(DrawableInto canvas) {
		canvas.drawCell(this, color);
	}
}
