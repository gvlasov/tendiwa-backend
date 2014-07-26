package org.tendiwa.core;

/**
 * Implementing this interface allows {@link ItemType} to be used as a ranged weapon that can shoot {@link Shootable}
 * items whose {@link Shootable#getAmmunitionType()} is identical to this RangedWeapon's {@link
 * RangedWeapon#getAmmunitionType()}.
 */
public class RangedWeapon {
	public AmmunitionType ammunitionType;

	public AmmunitionType getAmmunitionType() {
		return ammunitionType;
	}
}
