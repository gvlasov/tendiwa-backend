package tendiwa.core;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class AspectApparel extends Aspect {
private final Set<ApparelSlot> slots;

/**
 * A set of body parts that can't get any more items put on on them if this item is put on.
 *
 * @param slots
 * 	Slots taken up by this piece of apparel.
 */
public AspectApparel(ApparelSlot... slots) {
	super(AspectName.APPAREL);

	ImmutableSet.Builder<ApparelSlot> builder = ImmutableSet.builder();
	for (ApparelSlot slot : slots) {
		builder.add(slot);
	}
	this.slots = builder.build();
}

public Set<ApparelSlot> getSlots() {
	return slots;
}
}
