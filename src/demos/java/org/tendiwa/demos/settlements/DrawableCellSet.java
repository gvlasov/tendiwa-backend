package org.tendiwa.demos.settlements;

import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.CellSet;
import org.tendiwa.geometry.CellSet_Wr;

import java.awt.Color;

public final class DrawableCellSet extends CellSet_Wr implements Drawable {
	private final Color color;

	public DrawableCellSet(CellSet cells, Color color) {
		super(cells);
		this.color = color;
	}

	@Override
	public void drawIn(DrawableInto canvas) {
		for (int x = 0; x <= canvas.getWidth(); x++) {
			for (int y = 0; y <= canvas.getHeight(); y++) {
				if (contains(x, y)) {
					canvas.drawCell(x, y, color);
				}
			}
		}
	}
}
