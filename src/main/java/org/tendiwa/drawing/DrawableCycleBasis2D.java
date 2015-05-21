package org.tendiwa.drawing;

import org.tendiwa.drawing.extensions.DrawablePoint2D;
import org.tendiwa.drawing.extensions.DrawableSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.Filament;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.awt.Color;

public final class DrawableCycleBasis2D implements Drawable {
	private final MinimumCycleBasis<Point2D, Segment2D> basis;
	private final Color cycles;
	private final Color filaments;
	private final Color freeVertices;

	public DrawableCycleBasis2D(
		MinimumCycleBasis<Point2D, Segment2D> basis,
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
		for (Filament filament : basis.filamentsSet()) {
			canvas.drawAll(
				filament,
				edge -> new DrawableSegment2D(edge, filaments)
			);
		}
		for (MinimalCycle cycle : basis.minimalCyclesSet()) {
			canvas.drawAll(
				cycle.asEdges(),
				edge -> new DrawableSegment2D(edge, cycles)
			);
		}
	}
}
