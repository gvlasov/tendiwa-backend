package org.tendiwa.core;

public enum Orientation {
	HORIZONTAL, VERTICAL;

	public boolean isVertical() {
		return this == VERTICAL;
	}

	public boolean isHorizontal() {
		return this == HORIZONTAL;
	}

	/**
	 * Returns HORIZONTAL, if this is VERTICAL, or returns VERTICAL, if this is
	 * HORIZONTAL.
	 *
	 * @return
	 */
	public Orientation reverted() {
		if (this == HORIZONTAL) {
			return VERTICAL;
		}
		return HORIZONTAL;
	}
}
