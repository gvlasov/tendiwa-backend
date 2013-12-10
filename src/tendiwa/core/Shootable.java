package tendiwa.core;

/**
 * Implementing this interface allows an {@link ItemType} to be used as ammunition for {@link RangedWeapon} whose {@link
 * tendiwa.core.RangedWeapon#getAmmunitionType()} is identical to this Shootable's {@link
 * tendiwa.core.Shootable#getAmmunitionType()}.
 */
public interface Shootable {
public AmmunitionType getAmmunitionType();
}
