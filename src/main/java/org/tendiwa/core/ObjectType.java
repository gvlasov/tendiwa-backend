package org.tendiwa.core;

/**
 * Describes a certain type of inanimate objects that are too large to be treated as {@link Item}s:
 * trees, furniture, wall segments etc.
 */
public interface ObjectType extends TypePlaceableInCell, Resourceable {

	public String name();

	@Override
	public default String getResourceName() {
		return name();
	}
}
