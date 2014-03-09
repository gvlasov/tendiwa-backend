package org.tendiwa.core;

import org.tendiwa.lexeme.Localizable;

public class ItemType implements TypePlaceableInCell, Resourceable, Localizable {

private Material material;
private double weight;
private double volume;
private boolean isStackable;
public String name;
Wieldable componentWieldable;
RangedWeapon componentRangedWeapon;
Wearable componentWearable;
Shootable componentShootable;

public void name(String name) {
	this.name = name;
}

public void material(Material material) {
	this.material = material;
}

public void weight(double weight) {
	this.weight = weight;
}

public void volume(double volume) {
	this.volume = volume;
}

public void handedness(Handedness handedness) {
	assert handedness != null;
	if (componentWieldable == null) {
		componentWieldable = new Wieldable();
	}
	componentWieldable.handedness = handedness;
}
public void slots(ApparelSlot... slots) {
	if (componentWearable == null) {
		componentWearable = new Wearable();
	}
	for (ApparelSlot slot : slots) {
		componentWearable.addSlot(slot);
	}
}

public void ammunitionUsed(AmmunitionType type) {
	assert type != null;
	if (componentRangedWeapon == null) {
		componentRangedWeapon = new RangedWeapon();
	}
	componentRangedWeapon.ammunitionType = type;
}
public void ammunitionType(AmmunitionType type) {
	assert type != null;
	if (componentShootable == null) {
		componentShootable = new Shootable();
	}
	componentShootable.ammunitionType = type;
}

public void isStackable(boolean isStackable) {
	this.isStackable = isStackable;
}

public Material getMaterial() {
	return material;
}

public double getWeight() {
	return weight;
}

public double getVolume() {
	return volume;
}

public boolean isStackable() {
	return isStackable;
}

@Override
public String getLocalizationId() {
	return name;
}

@Override
public String getResourceName() {
	return name;
}

}
