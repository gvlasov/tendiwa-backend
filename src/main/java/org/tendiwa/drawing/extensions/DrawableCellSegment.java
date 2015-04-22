package org.tendiwa.drawing.extensions;

import org.tendiwa.core.meta.Cell;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.CellSegment;
import org.tendiwa.geometry.CellSegment_Wr;

import java.awt.Color;

public final class DrawableCellSegment extends CellSegment_Wr implements Drawable {
	private final Color color;

	public DrawableCellSegment(
		CellSegment cellSegment,
		Color color
	) {
		super(cellSegment);
		this.color = color;
	}

	@Override
	public void drawIn(DrawableInto canvas) {
		for (Cell cell : asList()) {
			canvas.draw(new DrawableCell(cell, color));
		}
	}
}
