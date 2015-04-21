package org.tendiwa.geometry;

final class BasicSector implements Sector {
	private final Vector2D cw;
	private final Vector2D ccw;

	BasicSector(Vector2D cw, Vector2D ccw) {
		this.cw = cw;
		this.ccw = ccw;
	}

	@Override
	public boolean contains(Vector2D vector) {
		return vector.isBetweenVectors(cw, ccw);
	}
}
