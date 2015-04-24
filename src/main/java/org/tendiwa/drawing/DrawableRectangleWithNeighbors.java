package org.tendiwa.drawing;

import org.tendiwa.demos.DrawableRectangle;
import org.tendiwa.settlements.utils.RectangleWithNeighbors;
import org.tendiwa.settlements.utils.RectangleWithNeighbors_Wr;

import java.awt.Color;

public final class DrawableRectangleWithNeighbors extends RectangleWithNeighbors_Wr implements Drawable {
	private final Color mainFill;
	private final Color mainBorder;
	private final Color neighborFill;
	private final Color neighborBorder;

	public DrawableRectangleWithNeighbors(
		RectangleWithNeighbors rectangles,
		Color mainFill,
		Color mainBorder,
		Color neighborFill,
		Color neighborBorder
	) {
		super(rectangles);
		this.mainFill = mainFill;
		this.mainBorder = mainBorder;
		this.neighborFill = neighborFill;
		this.neighborBorder = neighborBorder;
	}

	@Override
	public void drawIn(Canvas canvas) {
		canvas.draw(
			new DrawableRectangle(mainRectangle(), mainBorder)
		);
		canvas.draw(
			new DrawableRectangle(mainRectangle().shrink(1), mainFill)
		);
		canvas.drawAll(
			neighbors(),
			r -> new DrawableRectangle(r, neighborFill)
		);
		canvas.drawAll(
			neighbors(),
			r -> new DrawableRectangle(r.shrink(1), neighborBorder)
		);
	}
}
