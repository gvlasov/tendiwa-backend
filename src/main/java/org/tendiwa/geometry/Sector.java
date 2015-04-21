package org.tendiwa.geometry;

@FunctionalInterface
public interface Sector {

	boolean contains(Vector2D vector);

	static Sector FULL_CIRCLE = (v) -> true;
}
