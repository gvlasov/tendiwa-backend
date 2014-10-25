package org.tendiwa.core;

/**
 * Enum-like static data structure that stores flags for {@link CharacterType} creation that determine CharacterType's
 * nature, like `humanoid`, `animal` or `robot`. Each aspect is identified by both its name and id — a human readable
 * string and a generated integer.
 */
public enum CharacterAspect {
	HUMANOID, ANIMAL, ROBOT, ELEMENTAL
}
