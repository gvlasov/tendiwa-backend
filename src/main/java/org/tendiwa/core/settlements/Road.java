package org.tendiwa.core.settlements;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.geometry.Rectangle;
import org.tendiwa.geometry.*;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Utils;

public class Road {
final Cell start;
final Cell end;
final Orientation orientation;
int width = 5;

public Road(int startX, int startY, int endX, int endY) {
	if (startX != endX && startY != endY) {
		throw new Error("Inappropriate new road: " + startX + " " + startY + " " + endX + " " + endY);
	}
	start = new Cell(startX, startY);
	end = new Cell(endX, endY);
	orientation = start.getX() == end.getX() ? Orientation.VERTICAL : Orientation.HORIZONTAL;
}

public String toString() {
	return "Road [" + start.getX() + ", " + start.getY() + ", " + end.getX() + ", " + end.getY() + "];";
}

public CardinalDirection getSideOfRectangle(java.awt.Rectangle r) {
	// Get direction of rectangle from which this road is located
	if (orientation.isVertical()) {
		if (this.start.getX() < r.x) {
			return CardinalDirection.W;
		} else if (this.start.getX() >= r.x + r.width) {
			return CardinalDirection.E;
		} else {
			throw new Error("Vertical road " + this + " is inside rectangle " + r);
		}
	} else {
		if (this.start.getY() < r.y) {
			return CardinalDirection.N;
		} else if (this.start.getY() >= r.y + r.height) {
			return CardinalDirection.S;
		} else {
			throw new Error("Horizontal road " + this + " is inside rectangle " + r);
		}
	}
}

public boolean crossesRectangle(Rectangle r) {
	if (orientation.isVertical()) {
		return start.getX() >= r.getX() && start.getX() < r.getX() + r.getWidth();
	} else {
		return start.getY() >= r.getY() && start.getY() < r.getY() + r.getHeight();
	}
}

public boolean isRectangleOverlapsRoad(Rectangle rectangle) {
	Rectangle ra = new Rectangle(rectangle);
	if (orientation.isVertical()) {
		if (Utils.integersRangeIntersection(rectangle.getY(), rectangle.getY() + rectangle.getHeight() - 1, start.getY(), end.getY()) > 0 && ra.distanceToLine(start, end) < width / 2) {
			// If road line and rectangle overlap in y-axis,
			// and road is close enough to rectangle
			return true;
		}
	} else {
		if (Utils.integersRangeIntersection(rectangle.getX(), rectangle.getX() + rectangle.getWidth() - 1, start.getX(), end.getX()) > 0 && ra.distanceToLine(start, end) < width / 2) {
			// Same for x-axis
			return true;
		}
	}
	return false;
}

public boolean isRectangleNearRoad(Rectangle rectangle) {
	/**
	 * Checks if this road goes along one of the borders of a
	 * rectangle
	 */
	Rectangle ra = new Rectangle(rectangle);
	if (orientation.isVertical()) {
		if (Utils.integersRangeIntersection(rectangle.getY(), rectangle.getY() + rectangle.getHeight() - 1, start.getY(), end.getY()) > 0 && ra.distanceToLine(start, end) == width / 2 + 1) {
			// If road line and rectangle overlap in y-axis,
			// and road is close enough to rectangle
			return true;
		}
	} else {
		if (Utils.integersRangeIntersection(rectangle.getX(), rectangle.getX() + rectangle.getWidth() - 1, start.getX(), end.getX()) > 0 && ra.distanceToLine(start, end) == width / 2 + 1) {
			// Same for x-axis
			return true;
		}
	}
	return false;
}
}
