package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Polygon;
import org.tendiwa.graphs.graphs2d.BasicSplittableCycle2D;

public final class Hull extends BasicSplittableCycle2D implements MeshedNetworkCycle {
	Hull(Polygon polygon) {
		super(polygon);
	}
}
