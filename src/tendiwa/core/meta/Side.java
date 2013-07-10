package tendiwa.core.meta;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Emum for representing cardinal directions
 *
 */
public enum Side {
	N,E,S,W,NE,NW,SE,SW,ANY_SIDE;
	static {
		Set<Side> sides = new HashSet<Side>();
		sides.add(N);
		sides.add(E);
		sides.add(S);
		sides.add(W);
		EACH_CARDINAL_SIDE = Collections.unmodifiableSet(sides);
		CARDINAL_SIDES = new Side[] {N, E, S, W};
		TAN1 = Math.tan(Math.PI/8);
		TAN2 = Math.tan(Math.PI/8*3);
	}
	/**
	 * Cosines of angles that divide plane on 8 parts.
	 */
	public static double TAN1;
	public static double TAN2;
	public static Set<Side> EACH_CARDINAL_SIDE;
	public static Side[] CARDINAL_SIDES;
	public static Side int2side(int side) {
	/**
	 * Returns SideTest corresponding to number side.
	 * Integer side must be from 1 to 8, 1 is for SideTest.N, 
	 * each next is for the next side clockwise
	 */
		switch (side) {
		case 0:
			return N;
		case 1:
			return NE;
		case 2: 
			return E;
		case 3: 
			return SE;
		case 4:
			return S;
		case 5: 
			return SW;
		case 6:
			return W;
		case 7:
			return NW;
		default:
			throw new Error("Not appropriate side int!");	
		}
	}
	public static int side2int(Side side) {
	/**
	 * Returns int corresponding to SideTest.
	 * 1 is for SideTest.N, each next is for the next side clockwise
	 */
		switch (side) {
		case N:
			return 0;
		case NE:
			return 1;
		case E: 
			return 2;
		case SE: 
			return 3;
		case S:
			return 4;
		case SW: 
			return 5;
		case W:
			return 6;
		case NW:
			return 7;
		default:
			throw new Error("Not appropriate SideTest!");	
		}
	}
	public int side2int() {
	/**
	 * Returns int corresponding to SideTest.
	 * 1 is for SideTest.N, each next is for the next side clockwise
	 */
		switch (this) {
		case N:
			return 0;
		case NE:
			return 1;
		case E: 
			return 2;
		case SE: 
			return 3;
		case S:
			return 4;
		case SW: 
			return 5;
		case W:
			return 6;
		case NW:
			return 7;
		default:
			throw new Error("Not appropriate SideTest!");	
		}
	}
	public Side clockwise() {
		switch (this) {
		case N:
			return E;
		case E:
			return S;
		case S:
			return W;
		case W: 
			return N;
		default:
			throw new Error("Incorrect side "+this);
		}
	}
	public Side counterClockwise() {
		switch (this) {
		case N:
			return W;
		case W:
			return S;
		case S:
			return E;
		case E: 
			return N;
		default:
			throw new Error("Incorrect side "+this);
		}
	}
	public Side opposite() {
		switch (this) {
		case N:
			return S;
		case W:
			return E;
		case S:
			return N;
		case E: 
			return W;
		default:
			throw new Error("Incorrect side "+this);
		}
	}
	public Side ordinalClockwise() {
		switch (this) {
		case N:
			return NE;
		case NE:
			return E;
		case E:
			return SE;
		case SE: 
			return S;
		case S:
			return SW;
		case SW:
			return W;
		case W: 
			return NW;
		case NW:
			return N;
		default:
			throw new Error("Incorrect side "+this);
		}
	}
	public Side ordinalCounterClockwise() {
		switch (this) {
		case N:
			return NW;
		case NE:
			return N;
		case E:
			return NE;
		case SE: 
			return E;
		case S:
			return SE;
		case SW:
			return S;
		case W: 
			return SW;
		case NW:
			return W;
		default:
			throw new Error("Incorrect side "+this);
		}
	}
	public String toString() {
		switch (this) {
		case N:
			return "N";
		case NE:
			return "NE";
		case E:
			return "E";
		case SE:
			return "SE";
		case S:
			return "S";
		case SW:
			return "SW";
		case W:
			return "W";
		case NW:
			return "NW";
		default:
			throw new Error("Unknown side");
		}
	}
	public boolean isVertical() {
		switch (this) {
		case N:
		case S:
			return true;
		case W:
		case E:
			return false;
		default:
			throw new Error("Cannot be horizontal or vertical");
		}
	}
	public boolean isHorizontal() {
		switch (this) {
		case N:
		case S:
			return false;
		case W:
		case E:
			return true;
		default:
			throw new Error("Cannot be horizontal or vertical");
		}
	}
	/**
	 * Checks if this side is cardinal, not ordinal.
	 * @return true if this side is N, E, S or W; false if this side is NE, SE, SW or NW.
	 */
	public boolean isCardinal() {
		return this == N || this == E || this == S || this == W;
	}
	/**
	 * Returns an ordinal direction between two cardinal directions.
	 */
	public static Side getOrdinalDirection(Side side1, Side side2) {
		if (side1 == Side.N) {
			if (side2 == Side.E) {
				return Side.NE;
			} else if (side2 == Side.W) {
				return Side.NW;
			}
		} else if (side1 == Side.E) {
			if (side2 == Side.N) {
				return Side.NE;
			} else if (side2 == Side.S) {
				return Side.SE;
			}
		} else if (side1 == Side.S) {
			if (side2 == Side.E) {
				return Side.SE;
			} else if (side2 == Side.W) {
				return Side.SW;
			}
		} else if (side1 == Side.W) {
			if (side2 == Side.N) {
				return Side.NW;
			} else if (side2 == Side.S) {
				return Side.SW;
			}
		}
		throw new Error("Sides "+side1+" and "+side2+" are not close as cardinal directions");
	}
	/**
	 * Converts shift in cells to a cardinal side.
	 * 
	 * @param dx Shift in cells by x-axis.
	 * @param dy Shift in cells by y-axis.
	 * @return SideTest that corresponds to the given shift.
	 */
	public static Side d2side(int dx, int dy) {
		// I quarter of the cartesian coordinate system where y-axis points down.
		if (dx >= 0 && dy > 0) {
			if (dx/dy > TAN2) {
				return E;
			}
			if (dx/dy < TAN1) {
				return S;
			}
			return SE;
		}
		// II quarter
		if (dx <= 0 && dy > 0) {
			if (dx/dy < -TAN2) {
				return W;
			}
			if (dx/dy > -TAN1) {
				return S;
			}
			return SW;
		}
		// III quarter
		if (dx <= 0 && dy < 0) {
			if (dx/dy > TAN2) {
				return W;
			}
			if (dx/dy < TAN1) {
				return N;
			}
			return NW;
		}
		if (dy == 0) {
			if (dx > 0) {
				return E;
			}
			return W;
		}
		// IV quarter
		if (dx/dy < -TAN2) {
			return E;
		}
		if (dx/dy > -TAN1) {
			return N;
		}
		return NE;
	}
	/**
	 * Converts side to shift in cells by x-axis ([0]) and y-axis ([1]). Works with
	 * both ordinal and cardinal sides.
	 * 
	 * @return
	 */
	public int[] side2d() {
		switch (this) {
		case N:
			return new int[] {0,-1};
		case NE:
			return new int[] {1,-1};
		case E:
			return new int[] {1,0};
		case SE: 
			return new int[] {1,1};
		case S:
			return new int[] {0,1};
		case SW:
			return new int[] {-1,1};
		case W: 
			return new int[] {-1,0};
		case NW:
			return new int[] {-1,-1};
		default:
			throw new Error("Incorrect side "+this);
		}
	}
	/**
	 * Returns a random cardinal side .
	 * @return
	 */
	public static Side getRandomCardinal() {
		int i = Chance.rand(0, 3);
		return CARDINAL_SIDES[i];
	}
	public boolean isOrdinal() {
		return this == NW || this == NE || this == SW || this == SE;
	}
	public Direction getDirection() {
		switch (this) {
			case N:
			case S:
				return Direction.V;
			case E:
			case W:
				return Direction.H;
			default:
				throw new IllegalArgumentException("Method takes only cardinal directions");
		}
	}
}
