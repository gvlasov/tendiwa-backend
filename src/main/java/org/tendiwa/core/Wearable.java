package org.tendiwa.core;

import java.util.Collection;
import java.util.LinkedList;

public class Wearable {
	public Collection<ApparelSlot> slots = new LinkedList<>();

	public void addSlot(ApparelSlot slot) {
		slots.add(slot);
	}

	public Collection<ApparelSlot> getSlots() {
		return slots;
	}
}
