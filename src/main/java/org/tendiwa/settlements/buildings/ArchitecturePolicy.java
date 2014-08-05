package org.tendiwa.settlements.buildings;

import gnu.trove.map.TObjectDoubleMap;
import org.tendiwa.geometry.Placeable;

import java.util.Collection;

public final class ArchitecturePolicy {
	final int minInstances;
	final int maxInstances;
	final Priority priority;
	final Placeable allowedArea;
	final Collection<Architecture> presence;
	final Collection<Street> onStreet;
	final TObjectDoubleMap<Architecture> closeEnough;

	ArchitecturePolicy(
		int minInstances,
		int maxInstances,
		TObjectDoubleMap<Architecture> closeEnough,
		Priority priority,
		Placeable allowedArea,
		Collection<Architecture> presence,
		Collection<Street> onStreet
	) {
		this.minInstances = minInstances;
		this.maxInstances = maxInstances;
		this.closeEnough = closeEnough;
		this.priority = priority;
		this.allowedArea = allowedArea;
		this.presence = presence;
		this.onStreet = onStreet;
	}

	public static enum Priority {
		LOWEST(0), LOW(1), DEFAULT(2), HIGH(3), HIGHEST(4);
		private final int value;


		Priority(int value) {
			this.value = value;
		}
	}
}
