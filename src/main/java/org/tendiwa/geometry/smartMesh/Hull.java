package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.graphs2d.Graph2D;

public final class Hull extends OrientedCycle {
	Hull(
		Polygon originalMinimalCycle,
		Graph2D splitOriginalGraph
	) {
		super(originalMinimalCycle, splitOriginalGraph);
	}
}
