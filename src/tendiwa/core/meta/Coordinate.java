package tendiwa.core.meta;

import java.awt.Point;

import tendiwa.core.CardinalDirection;
import tendiwa.core.Direction;
import tendiwa.core.OrdinalDirection;

public class Coordinate extends Point {
	public static final long serialVersionUID = 1452389451;

	public Coordinate(int x, int y) {
		super(x, y);
	}
	public Coordinate(Coordinate c) {
		super(c.x, c.y);
	}
	public boolean equals(Object o) {
		if (!(o instanceof Coordinate)) {
			return false;
		}
		Coordinate coordinate = (Coordinate) o;
		return coordinate.x == x && coordinate.y == y;
	}
	public String toString() {
		return x + ":" + y;
	}
	public boolean isNear(int x, int y) {
		int ableX = Math.abs(this.x - x);
		int ableY = Math.abs(this.y - y);
		if ((ableX == 1 && ableY == 0) || (ableY == 1 && ableX == 0) || (ableY == 1 && ableX == 1)) {
			return true;
		}
		return false;
	}
	public static boolean isNear(int startX, int startY, int endX, int endY) {
		int ableX = Math.abs(startX - endX);
		int ableY = Math.abs(startY - endY);
		if ((ableX == 1 && ableY == 0) || (ableY == 1 && ableX == 0) || (ableY == 1 && ableX == 1)) {
			return true;
		}
		return false;
	}
	public int distance(int x, int y) {
		return (int) Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2));
	}
	public int distance(Coordinate e) {
		return (int) Math.sqrt(Math.pow(this.x - e.x, 2) + Math.pow(this.y - e.y, 2));
	}
	public Coordinate moveToSide(Direction side, int distance) {
		if (side == CardinalDirection.N) {
			y -= distance;
		} else if (side == CardinalDirection.E) {
			x += distance;
		} else if (side == CardinalDirection.S) {
			y += distance;
		} else if (side == CardinalDirection.W) {
			x -= distance;
		} else if (side == OrdinalDirection.NE) {
			x += distance;
			y -= distance;
		} else if (side == OrdinalDirection.SE) {
			x += distance;
			y += distance;
		} else if (side == OrdinalDirection.SW) {
			x -= distance;
			y += distance;
		} else if (side == OrdinalDirection.NW) {
			x -= distance;
			y -= distance;
		} else {
			throw new IllegalArgumentException();
		}
		return this;
	}
}
