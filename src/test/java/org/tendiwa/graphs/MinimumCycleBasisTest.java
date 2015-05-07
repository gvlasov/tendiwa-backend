package org.tendiwa.graphs;

import org.junit.Test;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.graphs2d.Graph2D_Wr;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.graph2D;
import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;

public final class MinimumCycleBasisTest {

	@Test
	public void tShapeIs2Filaments() {
		Graph2D tShape = new TShape();
		assertEquals(
			1,
			tShape.vertexSet()
				.stream()
				.filter(v -> tShape.degreeOf(v) > 2)
				.count()
		);
		assertEquals(
			2,
			new MinimumCycleBasis<>(
				tShape,
				Point2DVertexPositionAdapter.get()
			).filamentsSet().size()
		);
	}

	private static final class TShape extends Graph2D_Wr {

		protected TShape() {
			super(
				graph2D(
					graphConstructor()
						.chain(
							new PointTrail(0, 0)
								.moveBy(10, 10)
								.moveBy(10, 10)
								.moveBy(10, 10)
								.points()
						)
						.chain(
							new PointTrail(10, 10)
								.moveBy(10, -10)
								.points()
						)
						.graph()
				)
			);
		}
	}
}