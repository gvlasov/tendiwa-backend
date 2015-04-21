package org.tendiwa.geometry;

final class TwoDimensionalWorldConstraintViolation extends SanityException {
	public TwoDimensionalWorldConstraintViolation() {
		super(
			"There are more than 2 dimensions in two dimensional world apparently"
		);
	}
}
