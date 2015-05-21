package org.tendiwa.drawing;

import org.tendiwa.drawing.extensions.DrawablePoint2D;
import org.tendiwa.drawing.extensions.DrawableSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.Polyline;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.awt.Color;

public final class DrawableCycleBasis2D implements Drawable {
	private final MinimumCycleBasis basis;
	private final Color cycles;
	private final Color filaments;
	private final Color freeVertices;

	public DrawableCycleBasis2D(
		MinimumCycleBasis basis,
		Color cycles,
		Color filaments,
		Color freeVertices
	) {
		this.basis = basis;
		this.cycles = cycles;
		this.filaments = filaments;
		this.freeVertices = freeVertices;
	}

	@Override
	public void drawIn(Canvas canvas) {
		for (Point2D p : basis.isolatedVertexSet()) {
			canvas.draw(new DrawablePoint2D.Circle(p, freeVertices, 3));
		}
		for (Polyline filament : basis.filamentsSet()) {
			canvas.drawAll(
				filament.toSegments(),
				edge -> new DrawableSegment2D(edge, filaments)
			);
		}
		for (Polygon cycle : basis.minimalCyclesSet()) {
			canvas.drawAll(
				cycle.toSegments(),
				edge -> new DrawableSegment2D(edge, cycles)
			);
		}
	}
}
