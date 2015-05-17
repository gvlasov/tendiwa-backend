package org.tendiwa.geometry.graphs2d;

import com.google.common.collect.ImmutableSet;

public interface PerforatedCycle2D extends Cycle2D {
	ImmutableSet<? extends Cycle2D> holes();
}
