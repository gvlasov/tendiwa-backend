package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.*;
import org.tendiwa.drawing.extensions.DrawingCell;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.drawing.extensions.DrawingSegment2D;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Cell;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.*;

import java.awt.*;
import java.util.stream.Collectors;

public class EberlyMinimumCycleBasisDemo implements Runnable {
	public static void main(String[] args) {
		Demos.run(EberlyMinimumCycleBasisDemo.class, new DrawingModule());
	}

	@Inject
	TestCanvas canvas;

	/**
	 * Draws example from
	 * <a href="https://docs.google.com/viewer?url=www.geometrictools.com%2FDocumentation%2FMinimalCycleBasis.pdf&embedded=true#:0.page.4">page
	 * 4 of [Eberly 2005], Figure 2.1</a>
	 *
	 * @see org.tendiwa.graphs.MinimumCycleBasis
	 */
	@Override
	public void run() {
		final GraphConstructor<Point2D, Segment2D> constructor =
				new GraphConstructor<>(Segment2D::new)
				.vertex(0, new Point2D(20, 20))
				.vertex(1, new Point2D(30, 50))
				.vertex(2, new Point2D(70, 55))
				.vertex(3, new Point2D(15, 90))
				.vertex(4, new Point2D(85, 90))
				.vertex(5, new Point2D(50, 70))
				.vertex(6, new Point2D(35, 80))
				.vertex(7, new Point2D(100, 50))
				.vertex(8, new Point2D(100, 70))
				.vertex(9, new Point2D(90, 110))
				.vertex(10, new Point2D(110, 109))
				.vertex(11, new Point2D(120, 55))
				.vertex(12, new Point2D(125, 90))
				.vertex(13, new Point2D(150, 50))
				.vertex(14, new Point2D(180, 120))
				.vertex(15, new Point2D(200, 100))
				.vertex(16, new Point2D(220, 110))
				.vertex(17, new Point2D(160, 75))
				.vertex(18, new Point2D(190, 70))
				.vertex(19, new Point2D(220, 50))
				.vertex(20, new Point2D(230, 85))
				.vertex(21, new Point2D(240, 40))
				.vertex(22, new Point2D(230, 130))
				.vertex(23, new Point2D(300, 130))
				.vertex(24, new Point2D(300, 85))
				.vertex(25, new Point2D(265, 90))
				.vertex(26, new Point2D(250, 110))
				.vertex(27, new Point2D(280, 110))
				.cycle(1, 2, 4, 3)
				.path(4, 5, 6)
				.cycle(8, 9, 10)
				.path(2, 7, 11)
				.cycle(11, 12, 13)
				.cycle(12, 13, 18, 19, 20)
				.cycle(19, 21, 20)
				.cycle(20, 24, 23, 22)
				.cycle(25, 26, 27)
				.path(14, 15, 16);
		final SimpleGraph<Point2D, Segment2D> graph = constructor
			.graph();
		canvas.draw(graph, DrawingGraph.basis(Color.green, Color.red, Color.blue));
	}
}
