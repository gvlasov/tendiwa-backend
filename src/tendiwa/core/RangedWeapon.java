package tendiwa.core;

/**
 * Implementing this interface allows {@link ItemType} to be used as a ranged weapon that can shoot {@link Shootable}
 * items whose {@link tendiwa.core.Shootable#getAmmunitionType()} is identical to this RangedWeapon's {@link
 * tendiwa.core.RangedWeapon#getAmmunitionType()}.
 */
public class RangedWeapon {
public AmmunitionType ammunitionType;
}
