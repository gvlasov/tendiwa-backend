package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Segment2D_Wr;

import java.awt.Color;

public final class DrawableSegment2D extends Segment2D_Wr implements Drawable {
	private final Color color;

	public DrawableSegment2D(
		Segment2D segment,
		Color color
	) {
		super(segment);
		this.color = color;
	}

	@Override
	public void drawIn(DrawableInto canvas) {
		canvas.drawRasterLine(this, color);
	}
}
