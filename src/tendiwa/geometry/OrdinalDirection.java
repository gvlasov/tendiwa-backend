package tendiwa.geometry;

public enum OrdinalDirection implements Direction {
	NE, SE, SW, NW;

	/**
	 * <p>
	 * Returns an int corresponding to SideTest.
	 * </p>
	 * <ul>
	 * <li>1 is {@link OrdinalDirection.NE}</li>
	 * <li>3 is {@link OrdinalDirection.SE}</li>
	 * <li>5 is {@link OrdinalDirection.SW}</li>
	 * <li>7 is {@link OrdinalDirection.NW}</li>
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
	 * @see Directoin#counterClockwise()
	 * @see OrdinalDirection#clockwiseOrdinal()
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
	 * Returns next CardinalDirection that is counterclockwise from this
	 * OrdinalDirection.
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
				return new int[] {
					1, -1
				};
			case SE:
				return new int[] {
					1, 1
				};
			case SW:
				return new int[] {
					-1, 1
				};
			case NW:
			default:
				return new int[] {
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
}
