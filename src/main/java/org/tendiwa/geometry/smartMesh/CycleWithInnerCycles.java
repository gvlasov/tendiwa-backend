package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import lombok.Lazy;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.graphs2d.PerforatedCycle2D;

import java.util.Collection;

import static org.tendiwa.collections.Collectors.toImmutableSet;

/**
 * A cycle that encloses other cycles.
 * <p>
 * Has only one level of nesting, i.e. enclosed cycles don't track cycles enclosed in them.
 */
public final class CycleWithInnerCycles implements PerforatedCycle2D {
	private final Polygon enclosingPolygon;
	private final Collection<Polygon> allPolygons;

	public CycleWithInnerCycles(
		Polygon outerPolygon,
		Collection<Polygon> allPolygons
	) {
		assert allPolygons.contains(outerPolygon);
		this.enclosingPolygon = outerPolygon;
		this.allPolygons = allPolygons;
	}

	private boolean isCycleInsideEnclosingCycle(Polygon cycle) {
		return enclosingPolygon.containsPoint(
			cycle.iterator().next()
		);
	}

	@Lazy
	@Override
	public Hull hull() {
		return new Hull(enclosingPolygon);
	}

	@Lazy
	@Override
	public ImmutableSet<Hole> holes() {
		return allPolygons
			.stream()
			.filter(cycle -> cycle != enclosingPolygon)
			.filter(this::isCycleInsideEnclosingCycle)
			.map(Hole::new)
			.collect(toImmutableSet());
	}
}
