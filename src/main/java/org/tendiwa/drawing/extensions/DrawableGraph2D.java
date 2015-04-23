package org.tendiwa.drawing.extensions;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.Drawable;
import org.tendiwa.drawing.DrawableInto;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.Graph2D_Wr;

import java.awt.Color;
import java.util.function.Function;

public final class DrawableGraph2D extends Graph2D_Wr implements Drawable {
	private final Function<Point2D, Drawable> howToDrawVertices;
	private final Function<Segment2D, Drawable> howToDrawEdges;

	public DrawableGraph2D(
		UndirectedGraph<Point2D, Segment2D> graph,
		Function<Point2D, Drawable> howToDrawVertices,
		Function<Segment2D, Drawable> howToDrawEdges
	) {
		super(graph);
		this.howToDrawVertices = howToDrawVertices;
		this.howToDrawEdges = howToDrawEdges;
	}

	@Override
	public void drawIn(DrawableInto canvas) {
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
			UndirectedGraph<Point2D, Segment2D> graph,
			Color color,
			double diameter
		) {
			super(graph);
			this.color = color;
			this.diameter = diameter;
		}

		@Override
		public void drawIn(DrawableInto canvas) {
			canvas.drawAll(
				edgeSet(),
				edge -> new DrawableSegment2D(edge, color)
			);
			canvas.drawAll(
				vertexSet(),
				p -> new DrawablePoint2D.Circle(p, color, diameter)
			);
		}
	}

	public static final class Thin extends Graph2D_Wr implements Drawable {
		private final Color color;

		public Thin(
			UndirectedGraph<Point2D, Segment2D> graph,
			Color color
		) {
			super(graph);
			this.color = color;
		}

		@Override
		public void drawIn(DrawableInto canvas) {
			canvas.drawAll(
				edgeSet(),
				edge -> new DrawableSegment2D.Thin(edge, color)
			);
		}
	}

	public static final class OnlyThinEdges extends Graph2D_Wr implements Drawable {

		private final Color color;

		public OnlyThinEdges(
			UndirectedGraph<Point2D, Segment2D> graph,
			Color color
		) {
			super(graph);
			this.color = color;
		}

		@Override
		public void drawIn(DrawableInto canvas) {
			canvas.drawAll(
				edgeSet(),
				edge -> new DrawableSegment2D.Thin(edge, color)
			);
		}
	}
}
