package org.tendiwa.demos;

import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.Rectangle_Wr;

import java.awt.Color;

public final class DrawableRectangle extends Rectangle_Wr implements Drawable {
	private final Color color;

	public DrawableRectangle(Rectangle rectangle, Color color) {
		super(rectangle);
		this.color = color;
	}

	@Override
	public void drawIn(DrawableInto canvas) {
		canvas.drawRectangle(this, color);
	}

	public static final class Chequerwise extends Rectangle_Wr implements Drawable {

		private final Color color1;
		private final Color color2;

		public Chequerwise(Rectangle rectangle, Color color1, Color color2) {
			super(rectangle);
			this.color1 = color1;
			this.color2 = color2;
		}

		@Override
		public void drawIn(DrawableInto canvas) {
			for (int i = this.x(); i < this.x() + this.width() - 1; i++) {
				for (int j = this.y(); j < this.y() + this.width() - 1; j++) {
					canvas.drawCell(i, j, (i + j) % 2 == 1 ? color1 : color2);
				}
			}
		}
	}

	public static final class Outlined extends Rectangle_Wr implements Drawable {
		private final Color background;
		private final Color outline;

		public Outlined(Rectangle rectangle, Color background, Color outline) {
			super(rectangle);
			this.background = background;
			this.outline = outline;
		}

		@Override
		public void drawIn(DrawableInto canvas) {
			canvas.drawRectangle(this, background);
			canvas.drawLine(
				this.x(),
				this.y(),
				this.x() + this.width() - 1,
				this.y(),
				outline
			);
			canvas.drawLine(
				this.x(),
				this.y(),
				this.x(),
				this.y() + this.height() - 1,
				outline
			);
			canvas.drawLine(
				this.x() + this.width() - 1,
				this.y(),
				this.x() + this.width() - 1,
				this.y() + this.height() - 1,
				outline
			);
			canvas.drawLine(
				this.x(),
				this.y() + this.height() - 1,
				this.x() + this.width() - 1,
				this.y() + this.height() - 1,
				outline
			);
		}
	}
}
