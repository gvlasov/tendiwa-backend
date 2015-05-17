package org.tendiwa.data;

import org.tendiwa.graphs.graphs2d.Graph2D_Wr;

import static org.tendiwa.geometry.GeometryPrimitives.graph2D;
import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public final class FourCyclePenisGraph extends Graph2D_Wr {
	public FourCyclePenisGraph() {
		super(
			graph2D(
				graphConstructor()
					.vertex(0, point2D(50, 50))
					.vertex(1, point2D(150, 50))
					.vertex(2, point2D(50, 150))
					.vertex(3, point2D(150, 150))
					.vertex(4, point2D(200, 150))
					.vertex(5, point2D(200, 300))
					.vertex(6, point2D(350, 150))
					.vertex(7, point2D(350, 300))
					.vertex(8, point2D(32, 245))
					.vertex(9, point2D(108, 214))
					.vertex(10, point2D(152, 298))
					.vertex(11, point2D(67, 347))
					.edge(1, 4)
					.cycle(0, 1, 3, 2)
					.cycle(8, 9, 10, 11)
					.edge(10, 5)
					.edge(9, 2)
					.cycle(4, 5, 7, 6)
					.graph()
			)
		);
	}
}
