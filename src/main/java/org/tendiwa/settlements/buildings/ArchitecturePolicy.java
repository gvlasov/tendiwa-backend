package org.tendiwa.settlements.buildings;

import gnu.trove.map.TObjectDoubleMap;
import org.tendiwa.geometry.Placeable;
import org.tendiwa.settlements.streets.Street;

import java.util.Collection;

public final class ArchitecturePolicy {
	final int minInstances;
	final int minInstancesTopBound;
	final int maxInstances;
	final Priority priority;
	final Placeable allowedArea;
	final Collection<Street> onStreet;
	// Dependent
	final TObjectDoubleMap<Architecture> closeEnough;
	// Dependent
	final Collection<Architecture> presence;

	ArchitecturePolicy(
		int minInstances,
		int minInstancesTopBound,
		int maxInstances,
		TObjectDoubleMap<Architecture> closeEnough,
		Priority priority,
		Placeable allowedArea,
		Collection<Architecture> presence,
		Collection<Street> onStreet
	) {
		this.minInstances = minInstances;
		this.minInstancesTopBound = minInstancesTopBound;
		this.maxInstances = maxInstances;
		this.closeEnough = closeEnough;
		this.priority = priority;
		this.allowedArea = allowedArea;
		this.presence = presence;
		this.onStreet = onStreet;
	}

	int getAcualMinInstances(int availableInstances) {
		if (minInstancesTopBound > 0) {
			if (minInstancesTopBound > availableInstances) {
				return availableInstances;
			} else {
				return minInstancesTopBound;
			}
		}
		return minInstances;
	}

	public static enum Priority {
		LOWEST(0), LOW(1), DEFAULT(2), HIGH(3), HIGHEST(4);
		private final int value;


		Priority(int value) {
			this.value = value;
		}
	}
}
