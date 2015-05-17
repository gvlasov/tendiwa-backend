package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import lombok.Lazy;
import org.tendiwa.geometry.graphs2d.Cycle2D;
import org.tendiwa.geometry.graphs2d.Cycle2D_Wr;
import org.tendiwa.geometry.graphs2d.PerforatedCycle2D;

import java.util.Collection;

import static org.tendiwa.collections.Collectors.toImmutableSet;

public final class CycleWithInnerCycles extends Cycle2D_Wr implements PerforatedCycle2D {
	private final Collection<OrientedCycle> allCycles;

	public CycleWithInnerCycles(
		OrientedCycle enclosingCycle,
		Collection<OrientedCycle> allCycles
	) {
		super(enclosingCycle);
		this.allCycles = allCycles;
	}


	private boolean isCycleInsideEnclosingCycle(OrientedCycle cycle) {
		return this.containsPoint(
			cycle.iterator().next()
		);
	}

	@Lazy
	@Override
	public ImmutableSet<OrientedCycle> holes() {
		return allCycles
			.stream()
			.filter(this::isCycleInsideEnclosingCycle)
			.collect(toImmutableSet());
	}
}
