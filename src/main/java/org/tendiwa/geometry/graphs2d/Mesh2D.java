package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Point2D;

import java.util.stream.Stream;

public interface Mesh2D extends Graph2D {
	ImmutableSet<Cycle2D> meshCells();

	Cycle2D hull();

	default Stream<Point2D> exits() {
		return hull().vertexSet().stream()
			.filter(v -> this.degreeOf(v) > 2);
	}
}
