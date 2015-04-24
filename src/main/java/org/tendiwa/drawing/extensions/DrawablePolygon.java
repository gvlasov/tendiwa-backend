package org.tendiwa.drawing.extensions;

import org.tendiwa.core.meta.Utils;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Polygon_Wr;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public final class DrawablePolygon extends Polygon_Wr implements Drawable {
	private final Color color;

	public DrawablePolygon(Polygon polygon, Color color) {
		super(polygon);
		this.color = color;
	}

	@Override
	public void drawIn(Canvas canvas) {
		int size = size();
		for (int i = 0; i < size; i++) {
			canvas.drawRasterLine(
				get(i).toCell(),
				get(i + 1 == size ? 0 : i + 1).toCell(),
				color
			);
		}
	}

	public static final class Thin extends Polygon_Wr implements Drawable {

		private final Color color;

		public Thin(Polygon polygon, Color color) {
			super(polygon);
			this.color = color;
		}

		@Override
		public void drawIn(Canvas canvas) {
			int size = size();
			for (int i = 0; i < size; i++) {
				canvas.draw(
					new DrawableSegment2D(
						segment2D(
							get(i),
							get(Utils.nextIndex(i, size))
						),
						color
					)
				);
			}
		}
	}
}
