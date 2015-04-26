package org.tendiwa.demos.settlements;

import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.geometry.graphs2d.Mesh2D;

import java.awt.Color;

public final class DrawableMesh2D implements Drawable {
	private final Mesh2D mesh;
	private final Color color;

	public DrawableMesh2D(
		Mesh2D mesh,
		Color color
	) {
		this.mesh = mesh;
		this.color = color;
	}

	@Override
	public void drawIn(Canvas canvas) {
		canvas.draw(
			new DrawableGraph2D.Thin(
				mesh.graph(),
				color
			)
		);
	}
}
