package org.tendiwa.demos.geometry;

import com.google.inject.Inject;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.demos.Demos;
import org.tendiwa.drawing.DrawableCycleBasis2D;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingModule;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.graphs.GraphConstructor;

import java.awt.Color;

import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class EberlyMinimumCycleBasisDemo implements Runnable {
	@Inject
	TestCanvas canvas;

	public static void main(String[] args) {
		Demos.run(EberlyMinimumCycleBasisDemo.class, new DrawingModule());
	}

	/**
	 * Draws example from
	 * <a href="https://docs.google.com/viewer?url=www.geometrictools.com%2FDocumentation%2FMinimalCycleBasis.pdf&embedded=true#:0.page.4">page
	 * 4 of [Eberly 2005], Figure 2.1</a>
	 *
	 * @see org.tendiwa.graphs.MinimumCycleBasis
	 */
	@Override
	public void run() {
		TestCanvas.canvas = canvas;
		final GraphConstructor<Point2D, Segment2D> constructor =
			graphConstructor()
				.vertex(0, point2D(20, 20))
				.vertex(1, point2D(30, 50))
				.vertex(2, point2D(70, 55))
				.vertex(3, point2D(15, 90))
				.vertex(4, point2D(85, 90))
				.vertex(5, point2D(50, 70))
				.vertex(6, point2D(35, 80))
				.vertex(7, point2D(100, 50))
				.vertex(8, point2D(100, 70))
				.vertex(9, point2D(90, 110))
				.vertex(10, point2D(110, 109))
				.vertex(11, point2D(120, 55))
				.vertex(12, point2D(125, 90))
				.vertex(13, point2D(150, 50))
				.vertex(14, point2D(180, 120))
				.vertex(15, point2D(200, 100))
				.vertex(16, point2D(220, 110))
				.vertex(17, point2D(160, 75))
				.vertex(18, point2D(190, 70))
				.vertex(19, point2D(220, 50))
				.vertex(20, point2D(230, 85))
				.vertex(21, point2D(240, 40))
				.vertex(22, point2D(230, 130))
				.vertex(23, point2D(300, 130))
				.vertex(24, point2D(300, 85))
				.vertex(25, point2D(265, 90))
				.vertex(26, point2D(250, 110))
				.vertex(27, point2D(280, 110))
				.cycle(1, 2, 4, 3)
				.path(4, 5, 6)
				.cycle(8, 9, 10)
				.path(2, 7, 11)
				.cycle(11, 12, 13)
				.cycle(12, 13, 18, 19, 20)
				.cycle(19, 21, 20)
				.cycle(20, 24, 23, 22)
				.cycle(25, 26, 27)

				.edge(23, 27) // This extra edge screws a cycle up in the original algorithm implementation

				.path(14, 15, 16);
		final SimpleGraph<Point2D, Segment2D> graph = constructor.graph();
//		canvas.draw(graph, DrawingGraph.withAliases(constructor, (p -> p.x), (p -> p.y)));
//		MinimumCycleBasis<Point2D, Segment2D> basis = new MinimumCycleBasis<>(graph, Point2DVertexPositionAdapter.get());
		canvas.draw(
			new DrawableCycleBasis2D(
				PlanarGraphs.minimumCycleBasis(graph),
				Color.green,
				Color.red,
				Color.blue
			)
		);
	}
}
