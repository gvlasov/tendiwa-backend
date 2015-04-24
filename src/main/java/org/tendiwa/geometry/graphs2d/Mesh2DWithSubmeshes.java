package org.tendiwa.geometry.graphs2d;

import java.util.Collection;

interface Mesh2DWithSubmeshes extends Mesh2D {
	Collection<Cycle2DWithInnerNetwork> cells();
}
