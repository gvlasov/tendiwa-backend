package org.tendiwa.core;

/**
 * Describes a certain ammunitionType of inanimate objects that are large enough to be treated as {@link Item}s: trees,
 * furniture,
 * wall segments
 */
public class ObjectType implements TypePlaceableInCell, Resourceable {

	Usable usableComponent;
	public String name;
	private Passability passability;

	public void name(String name) {
		assert name != null;
		this.name = name;
	}

	public void passability(Passability passability) {
		assert passability != null;
		this.passability = passability;
	}

	public void action(CharacterAbility action) {
		assert action != null;
		if (usableComponent == null) {
			usableComponent = new Usable();
		}
		usableComponent.addAction(action);
	}

	public Passability getPassability() {
		return passability;
	}

	@Override
	public String getResourceName() {
		return name;
	}
}
