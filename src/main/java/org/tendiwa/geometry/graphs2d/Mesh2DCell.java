package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;

public interface Mesh2DCell {
	Graph2D innerNetwork();

	Cycle2D outerCycle();

	ImmutableSet<Cycle2D> innerCycles();
}
