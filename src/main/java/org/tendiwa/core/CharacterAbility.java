package org.tendiwa.core;

public class CharacterAbility<T extends ActionTargetType> implements Resourceable {
	public String name;
	private T action;

	public void name(String name) {
		this.name = name;
	}

	public void action(T action) {
		this.action = action;
	}

	public ActionTargetType getAction() {
		return action;
	}

	@Override
	public String getResourceName() {
		return name;
	}
}
