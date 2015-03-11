package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Vector2D;

@FunctionalInterface
interface Sector {

	boolean contains(Vector2D vector);

	static Sector FULL_CIRCLE = (v) -> true;
}
