package org.tendiwa.geometry.graphs2d;

import java.util.Set;

interface Mesh2DWithCycles extends Mesh2D {

	Set<Cycle2DWithInnerNetwork> cycles();
}
