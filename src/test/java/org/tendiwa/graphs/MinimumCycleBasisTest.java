package org.tendiwa.graphs;

import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.PolygonGraph;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.graphs2d.BasicPolylineGraph;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.PolylineGraph2D;
import org.tendiwa.graphs.graphs2d.ConstructedGraph2D;
import org.tendiwa.graphs.graphs2d.Graph2D_Wr;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.*;

public final class MinimumCycleBasisTest {

	@Test
	public void t_shape_is_2_filaments() {
		assertEquals(
			2,
			new TShape()
				.minimumCycleBasis()
				.filamentsSet()
				.size()
		);
	}

	@Test
	public void just_a_single_cycle() {
		MinimumCycleBasis basis = new PolygonGraph(
			rectangle2D(20, 13)
		)
			.minimumCycleBasis();
		assertEquals(
			1,
			basis.minimalCyclesSet().size()
		);
		assertEquals(
			0,
			basis.filamentsSet().size()
		);
		assertEquals(
			0,
			basis.isolatedVertexSet().size()
		);

	}

	@Test
	public void just_a_single_filament() {
		MinimumCycleBasis basis = new BasicPolylineGraph(
			new PointTrail(0, 0)
				.moveByX(10)
				.moveByY(10)
				.moveByX(10)
				.polyline()
		)
			.minimumCycleBasis();
		assertEquals(
			0,
			basis.isolatedVertexSet().size()
		);
		assertEquals(
			1,
			basis.filamentsSet().size()
		);
		assertEquals(
			0,
			basis.minimalCyclesSet().size()
		);
	}

	@Test
	public void two_cycles() {
		MinimumCycleBasis basis = new TwoCycles().minimumCycleBasis();
		assertEquals(
			0,
			basis.isolatedVertexSet().size()
		);
		assertEquals(
			0,
			basis.filamentsSet().size()
		);
		assertEquals(
			2,
			basis.minimalCyclesSet().size()
		);
	}

	/**
	 * Constructs the example from
	 * <a href="https://docs.google.com/viewer?url=www.geometrictools.com%2FDocumentation%2FMinimalCycleBasis.pdf&embedded=true#:0.page.4">page
	 * 4 of [Eberly 2005], Figure 2.1</a>
	 *
	 * @see org.tendiwa.graphs.MinimumCycleBasis
	 */
	@Test
	public void eberly_example_minimum_cycle_basis() {
		Graph2D graph2D = graph2D(
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

				.path(14, 15, 16)
				.graph()
		);
		MinimumCycleBasis basis = graph2D.minimumCycleBasis();
		assertEquals(
			2,
			basis.isolatedVertexSet().size()
		);
		assertEquals(
			4,
			basis.filamentsSet().size()
		);
		assertEquals(
			7,
			basis.minimalCyclesSet().size()
		);
	}

	private static final class TShape extends ConstructedGraph2D {

		protected TShape() {
			addPolyline(
				new PointTrail(0, 0)
					.moveBy(10, 10)
					.moveBy(10, 10)
					.moveBy(10, 10)
					.polyline()
			);
			addPolyline(
				new PointTrail(10, 10)
					.moveBy(10, -10)
					.polyline()
			);
		}
	}
}