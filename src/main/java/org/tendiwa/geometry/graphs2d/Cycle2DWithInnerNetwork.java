package org.tendiwa.geometry.graphs2d;

interface Cycle2DWithInnerNetwork extends Cycle2D {
	Graph2D innerNetwork();

	default Graph2D fullGraph() {
		return new UnitedGraph2D(this, innerNetwork());
	}
}
