package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Segment2D;

public interface PerforatedCycle2D extends Cycle2D {
	ImmutableSet<? extends Cycle2D> holes();

	default boolean anyCycleContainsEdge(Segment2D edge) {
		return hull()
			.containsEdge(edge)
			||
			holes()
				.stream()
				.anyMatch(hole -> hole.containsEdge(edge));
	}
}
