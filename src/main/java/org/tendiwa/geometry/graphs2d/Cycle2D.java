package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Polygon;

public interface Cycle2D extends Polygon, Mesh2D {
	@Override
	default ImmutableSet<Cycle2D> meshCells() {
		return ImmutableSet.of(this);
	}

	@Override
	default Cycle2D hull() {
		return this;
	}
}
