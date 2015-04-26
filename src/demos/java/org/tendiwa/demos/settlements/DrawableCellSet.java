package org.tendiwa.demos.settlements;

import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.geometry.CellSet;
import org.tendiwa.geometry.CellSet_Wr;
import org.tendiwa.geometry.Dimension;
import org.tendiwa.geometry.FiniteCellSet;

import java.awt.Color;

public final class DrawableCellSet extends CellSet_Wr implements Drawable {
	private final Color color;

	public DrawableCellSet(CellSet cells, Color color) {
		super(cells);
		this.color = color;
	}

	@Override
	public void drawIn(Canvas canvas) {
		Dimension canvasSize = canvas.size();
		for (int x = 0; x <= canvasSize.width(); x++) {
			for (int y = 0; y <= canvasSize.height(); y++) {
				if (contains(x, y)) {
					canvas.drawCell(x, y, color);
				}
			}
		}
	}

	public final static class Finite extends FiniteCellSet_Wr implements Drawable {
		private final Color color;

		public Finite(FiniteCellSet cells, Color color) {
			super(cells);
			this.color = color;
		}

		@Override
		public void drawIn(Canvas canvas) {
			forEach(cell -> canvas.drawCell(cell, color));
		}
	}
}
