package org.tendiwa.drawing.extensions;

import org.tendiwa.drawing.Canvas;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.graphs.graphs2d.Graph2D_Wr;

import java.awt.Color;
import java.util.function.Function;

public final class DrawableGraph2D extends Graph2D_Wr implements Drawable {
	private final Function<Point2D, Drawable> howToDrawVertices;
	private final Function<Segment2D, Drawable> howToDrawEdges;

	public DrawableGraph2D(
		Graph2D graph,
		Function<Point2D, Drawable> howToDrawVertices,
		Function<Segment2D, Drawable> howToDrawEdges
	) {
		super(graph);
		this.howToDrawVertices = howToDrawVertices;
		this.howToDrawEdges = howToDrawEdges;
	}

	@Override
	public void drawIn(Canvas canvas) {
		canvas.drawAll(
			vertexSet(),
			howToDrawVertices
		);
		canvas.drawAll(
			edgeSet(),
			howToDrawEdges
		);
	}

	public static final class CircleVertices extends Graph2D_Wr implements Drawable {

		private final Color color;
		private final double diameter;

		public CircleVertices(
			Graph2D graph,
			Color color,
			double diameter
		) {
			super(graph);
			this.color = color;
			this.diameter = diameter;
		}

		@Override
		public void drawIn(Canvas canvas) {
			edgeSet().stream()
				.map(edge -> new DrawableSegment2D(edge, color))
				.forEach(drawable -> drawable.drawIn(canvas));
		}
	}

	public static final class Thin extends Graph2D_Wr implements Drawable {
		private final Color color;

		public Thin(
			Graph2D graph,
			Color color
		) {
			super(graph);
			this.color = color;
		}

		@Override
		public void drawIn(Canvas canvas) {
			canvas.drawAll(
				edgeSet(),
				edge -> new DrawableSegment2D.Thin(edge, color)
			);
		}
	}

	public static final class OnlyThinEdges extends Graph2D_Wr implements Drawable {

		private final Color color;

		public OnlyThinEdges(
			Graph2D graph,
			Color color
		) {
			super(graph);
			this.color = color;
		}

		@Override
		public void drawIn(Canvas canvas) {
			canvas.drawAll(
				edgeSet(),
				edge -> new DrawableSegment2D.Thin(edge, color)
			);
		}
	}

	public static final class WithAliases extends Graph2D_Wr implements Drawable {

		private final GraphConstructor<Point2D, Segment2D> graphConstructor;

		public WithAliases(
			Graph2D graph,
			GraphConstructor<Point2D, Segment2D> aliasSource
		) {
			super(graph);
			this.graphConstructor = aliasSource;
		}

		@Override
		public void drawIn(Canvas canvas) {
			canvas.drawAll(
				edgeSet(),
				edge -> new DrawableSegment2D(edge, Color.red)
			);
			canvas.drawAll(
				vertexSet(),
				p -> new DrawablePoint2D(p, Color.red)
			);
			canvas.drawAll(
				vertexSet().stream(),
				vertex ->
					new DrawableText(
						Integer.toString(graphConstructor.aliasOf(vertex)),
						vertex,
						Color.red
					)
			);
		}
	}
}
