package org.tendiwa.core;

public enum OrdinalDirection implements Direction {
	NE, SE, SW, NW;

	/**
	 * <p> Returns an int corresponding to SideTest. </p> <ul> <li>1 is {@link OrdinalDirection#NE}</li> <li>3 is
	 * {@link
	 * OrdinalDirection#SE}</li> <li>5 is {@link OrdinalDirection#SW}</li> <li>7 is {@link OrdinalDirection#NW}</li>
	 * </ul>
	 *
	 * @see {@link CardinalDirection#toInt()}
	 */
	@Override
	public int toInt() {
		switch (this) {
			case NE:
				return 1;
			case SE:
				return 3;
			case SW:
				return 5;
			case NW:
			default:
				return 7;
		}
	}

	/**
	 * Returns next {@link CardinalDirection} that is clockwise from this one.
	 *
	 * @see Direction#counterClockwise()
	 * @see Direction#counterClockwiseQuarter()
	 */
	@Override
	public CardinalDirection clockwise() {
		switch (this) {
			case NE:
				return CardinalDirection.E;
			case SE:
				return CardinalDirection.S;
			case SW:
				return CardinalDirection.W;
			case NW:
			default:
				return CardinalDirection.N;
		}
	}

	@Override
	public OrdinalDirection clockwiseQuarter() {
		switch (this) {
			case NE:
				return SE;
			case SE:
				return SW;
			case SW:
				return NW;
			case NW:
			default:
				return NE;
		}
	}

	/**
	 * Returns next CardinalDirection that is counterclockwise from this OrdinalDirection.
	 *
	 * @see {@link Direction#clockwise()}
	 */
	@Override
	public CardinalDirection counterClockwise() {
		switch (this) {
			case NE:
				return CardinalDirection.N;
			case SE:
				return CardinalDirection.E;
			case SW:
				return CardinalDirection.S;
			case NW:
			default:
				return CardinalDirection.W;
		}
	}

	@Override
	public OrdinalDirection counterClockwiseQuarter() {
		switch (this) {
			case NE:
				return SE;
			case SE:
				return SW;
			case SW:
				return NW;
			case NW:
			default:
				return NE;
		}
	}

	@Override
	public OrdinalDirection opposite() {
		switch (this) {
			case NE:
				return SW;
			case SE:
				return NW;
			case SW:
				return NE;
			case NW:
			default:
				return SE;
		}
	}

	@Override
	public String toString() {
		switch (this) {
			case NE:
				return "NE";
			case SE:
				return "SE";
			case SW:
				return "SW";
			case NW:
			default:
				return "NW";
		}
	}

	@Override
	public int[] side2d() {
		switch (this) {
			case NE:
				return new int[]{
					1, -1
				};
			case SE:
				return new int[]{
					1, 1
				};
			case SW:
				return new int[]{
					-1, 1
				};
			case NW:
			default:
				return new int[]{
					-1, -1
				};
		}
	}

	@Override
	public boolean isOpposite(Direction direction) {
		if (direction == null) {
			throw new NullPointerException();
		}
		switch (this) {
			case NE:
				return direction == SW ? true : false;
			case SE:
				return direction == NW ? true : false;
			case SW:
				return direction == NE ? true : false;
			case NW:
			default:
				return direction == SE ? true : false;
		}
	}

	@Override
	public boolean isPerpendicular(Direction direction) {
		if (this == NE || this == SW) {
			if (direction == SE || direction == NW) {
				return true;
			}
			return false;
		} else {
			assert this == NW || this == SE;
			if (direction == NE || direction == SW) {
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean isCardinal() {
		return false;
	}

	/**
	 * Returns one of two CardinalDirections this OrdinalDirection consists of.
	 *
	 * @param component
	 * 	One CardinalDirecton this OrdinalDirection consists of.
	 * @return Another CardinalDirection this OrdinalDirection consists of.
	 */
	public CardinalDirection anotherComponent(CardinalDirection component) {
		if (this == NE) {
			if (component == CardinalDirection.N) {
				return CardinalDirection.E;
			}
			if (component == CardinalDirection.E) {
				return CardinalDirection.N;
			}
		} else if (this == SE) {
			if (component == CardinalDirection.S) {
				return CardinalDirection.E;
			}
			if (component == CardinalDirection.E) {
				return CardinalDirection.S;
			}
		} else if (this == SW) {
			if (component == CardinalDirection.S) {
				return CardinalDirection.W;
			}
			if (component == CardinalDirection.W) {
				return CardinalDirection.S;
			}
		} else {
			assert this == NW;
			if (component == CardinalDirection.N) {
				return CardinalDirection.W;
			}
			if (component == CardinalDirection.W) {
				return CardinalDirection.N;
			}
		}
		throw new IllegalArgumentException("OrdinalDirection " + this + " doesn't contain component " + component);
	}

	public CardinalDirection[] getComponents() {
		if (this == NE) {
			return new CardinalDirection[]{CardinalDirection.N, CardinalDirection.E};
		} else if (this == NW) {
			return new CardinalDirection[]{CardinalDirection.N, CardinalDirection.W};
		} else if (this == SE) {
			return new CardinalDirection[]{CardinalDirection.S, CardinalDirection.E};
		} else {
			assert this == SW;
			return new CardinalDirection[]{CardinalDirection.S, CardinalDirection.W};
		}
	}
}
