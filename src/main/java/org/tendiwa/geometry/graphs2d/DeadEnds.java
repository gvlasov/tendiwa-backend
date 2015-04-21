package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Segment2D;

import java.util.Set;

final class DeadEnds {
	private final Mesh2D mesh;

	DeadEnds(Mesh2DWithSubmeshes mesh) {
		super();
		this.mesh = mesh;
	}

	public Set<Segment2D> values() {
		return ImmutableSet.of();
	}
}
