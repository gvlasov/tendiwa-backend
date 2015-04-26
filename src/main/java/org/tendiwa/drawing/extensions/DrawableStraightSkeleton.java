package org.tendiwa.drawing.extensions;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.StraightSkeleton;

import java.awt.Color;
import java.util.List;

public final class DrawableStraightSkeleton implements Drawable {
	private final StraightSkeleton skeleton;
	private final Color originalEdgesColor;
	private final Color innerEdgesColor;

	public DrawableStraightSkeleton(
		StraightSkeleton skeleton,
		Color originalEdgesColor,
		Color innerEdgesColor
	) {

		this.skeleton = skeleton;
		this.originalEdgesColor = originalEdgesColor;
		this.innerEdgesColor = innerEdgesColor;
	}

	@Override
	public void drawIn(Canvas canvas) {
		UndirectedGraph<Point2D, Segment2D> graph = skeleton.graph();
		List<Segment2D> originalEdges = skeleton.originalEdges();
		canvas.drawAll(
			graph.edgeSet(),
			edge ->
				new DrawableSegment2D.Thin(
					edge,
					originalEdges.contains(edge) ? originalEdgesColor : innerEdgesColor
				)
		);
	}
}
