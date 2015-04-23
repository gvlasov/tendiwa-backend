package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.Chain2D;
import org.tendiwa.geometry.Chain2D_Wr;

import java.awt.Color;

public final class DrawableChain2D {
	private DrawableChain2D() {
		throw new UnsupportedOperationException();
	}

	public static final class Thin extends Chain2D_Wr implements Drawable {

		private final Color color;

		public Thin(Chain2D chain, Color color) {
			super(chain);
			this.color = color;
		}

		@Override
		public void drawIn(DrawableInto canvas) {
			canvas.drawAll(
				asSegmentStream(),
				segment -> new DrawableSegment2D.Thin(segment, color)
			);
		}
	}
}
