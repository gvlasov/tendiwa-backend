package org.tendiwa.drawing.extensions;

import com.google.common.collect.Iterators;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.Colors;
import org.tendiwa.drawing.DrawingAlgorithm;
import org.tendiwa.geometry.*;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.graphs.Filament;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;

import java.awt.Color;
import java.util.Iterator;
import java.util.function.Function;

import static org.tendiwa.geometry.GeometryPrimitives.segment2D;

public class DrawingGraph {
	static Iterator<Color> cycleColors = Iterators.cycle(Color.red, Color.blue, Color.green, Color.orange,
		Color.magenta, Color.cyan);

	public static <V, E> DrawingAlgorithm<UndirectedGraph<V, E>> withAliases(
		GraphConstructor<V, E> constructor,
		Function<V, Double> x,
		Function<V, Double> y
	) {
		return (shape, canvas) -> {
			for (E e : shape.edgeSet()) {
				V source = shape.getEdgeSource(e);
				V target = shape.getEdgeTarget(e);
				canvas.drawLine(x.apply(source), y.apply(source), x.apply(target), y.apply(target), Color.RED);
			}
			canvas.drawAll(
				shape.vertexSet(),
				p -> new DrawableCell(
					new BasicCell(
						x.apply(p).intValue(),
						y.apply(p).intValue()
					),
					Color.red
				)
			);
			for (V v : shape.vertexSet()) {
				canvas.drawString(Integer.toString(constructor.aliasOf(v)), x.apply(v) + 5, y.apply(v) + 5,
					Color.BLUE);
			}
		};
	}

	public static DrawingAlgorithm<? super UndirectedGraph<Point2D, Segment2D>> withColor(Color color) {
		return withColorAndVertexSize(color, 6);
	}

	public static DrawingAlgorithm<UndirectedGraph<Point2D, org.tendiwa.geometry.Segment2D>> withColorAndVertexSize(
		final Color color,
		int size
	) {
		return (shape, canvas) -> {
			DrawingAlgorithm<Segment2D> segmentDrawing = DrawingSegment2D.withColorThin(color);
			for (Segment2D e : shape.edgeSet()) {
				Point2D source = shape.getEdgeSource(e);
				Point2D target = shape.getEdgeTarget(e);
				canvas.draw(
					segment2D(source, target),
					segmentDrawing
				);
			}
			canvas.drawAll(
				shape.vertexSet(),
				p -> new DrawablePoint2D.Circle(p, color, size)
			);
		};
	}

	public static DrawingAlgorithm<UndirectedGraph<Point2D, Segment2D>> withColorAndAntialiasing(
		final Color color
	) {
		return (shape, canvas) -> {
			DrawingAlgorithm<Segment2D> how = DrawingSegment2D.withColorThin(color);
			for (org.tendiwa.geometry.Segment2D e : shape.edgeSet()) {
				Point2D source = shape.getEdgeSource(e);
				Point2D target = shape.getEdgeTarget(e);
				Segment2D shape1 = segment2D(source, target);
				canvas.draw(
					shape1,
					how
				);
			}
		};
	}

	public static DrawingAlgorithm<UndirectedGraph<Point2D, Segment2D>> basis(
		Color filamentColor,
		Color cycleColor,
		Color vertexColor
	) {
		return (shape, canvas) -> {
			MinimumCycleBasis<Point2D, Segment2D> mcb = PlanarGraphs.minimumCycleBasis(shape);
			for (Point2D p : mcb.isolatedVertexSet()) {
				canvas.draw(new DrawablePoint2D.Circle(p, vertexColor, 3));
			}
			for (Filament<Point2D, Segment2D> filament : mcb.filamentsSet()) {
				canvas.drawAll(
					filament,
					edge -> new DrawableSegment2D(edge, filamentColor)
				);
			}
			for (MinimalCycle<Point2D, Segment2D> cycle : mcb.minimalCyclesSet()) {
				canvas.drawAll(
					cycle.asEdges(),
					edge -> new DrawableSegment2D(edge, cycleColor)
				);
			}
		};
	}

	public static DrawingAlgorithm<UndirectedGraph<Point2D, org.tendiwa.geometry.Segment2D>> multicoloredEdges() {
		Iterator<Color> colors = Colors.infiniteSequence(i -> new Color(
			(i * 451 + 122) % 255,
			(i * 234 + 200) % 255,
			(i * 123 + 178) % 255
		));
		return (graph, canvas) ->
			graph
				.edgeSet()
				.stream()
				.map(BasicCellSegment::new)
				.forEach(cellSegment -> canvas.draw(
					new DrawableCellSegment(cellSegment, colors.next())
				));
	}
}
