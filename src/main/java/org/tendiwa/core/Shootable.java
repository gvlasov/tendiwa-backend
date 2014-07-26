package org.tendiwa.core;

/**
 * Implementing this interface allows an {@link ItemType} to be used as ammunition for {@link RangedWeapon} whose
 * {@link
 * RangedWeapon#getAmmunitionType()} is identical to this Shootable's {@link
 * Shootable#getAmmunitionType()}.
 */
public class Shootable {

	public AmmunitionType ammunitionType;

	public AmmunitionType getAmmunitionType() {
		return ammunitionType;
	}
}
