package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Rectangle2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.graphs2d.Graph2D_Wr;

import static org.tendiwa.core.OrdinalDirection.*;
import static org.tendiwa.geometry.GeometryPrimitives.*;

public final class GraphWith2CyclesAnd3Filaments extends Graph2D_Wr {

	GraphWith2CyclesAnd3Filaments() {
		super(createGraph());
	}

	private static Graph2D createGraph() {
		double d = 10;
		Rectangle2D square = rectangle2D(d, d);
		Rectangle2D secondSquare = square
			.translate(vector(square.width() * 2, 0));
		Segment2D connectingFilament = segment2D(
			square.corner(NW),
			secondSquare.corner(NE)
		);
		Segment2D filament1 = segment2D(
			square.corner(SW),
			square.corner(SW).add(vector(0, d))
		);
		Segment2D filament2 = segment2D(
			secondSquare.corner(SE),
			secondSquare.corner(SE).add(vector(0, d))
		);
		return graph2D(
			graphConstructor()
				.cycleOfVertices(square)
				.cycleOfVertices(secondSquare)
				.edge(connectingFilament.start(), connectingFilament.end())
				.vertex(filament1.end())
				.edge(filament1.start(), filament1.end())
				.vertex(filament2.end())
				.edge(filament2.start(), filament2.end())
				.graph()
		);
	}

}
