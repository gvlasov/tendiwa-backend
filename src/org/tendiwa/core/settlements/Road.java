package org.tendiwa.core.settlements;

import org.tendiwa.core.CardinalDirection;
import org.tendiwa.core.EnhancedPoint;
import org.tendiwa.core.Orientation;
import org.tendiwa.core.meta.Utils;
import org.tendiwa.geometry.EnhancedRectangle;

import java.awt.*;

public class Road {
final EnhancedPoint start;
final EnhancedPoint end;
final Orientation orientation;
int width = 5;

public Road(int startX, int startY, int endX, int endY) {
	if (startX != endX && startY != endY) {
		throw new Error("Inappropriate new road: " + startX + " " + startY + " " + endX + " " + endY);
	}
	start = new EnhancedPoint(startX, startY);
	end = new EnhancedPoint(endX, endY);
	orientation = start.x == end.x ? Orientation.VERTICAL : Orientation.HORIZONTAL;
}

public String toString() {
	return "Road [" + start.x + ", " + start.y + ", " + end.x + ", " + end.y + "];";
}

public CardinalDirection getSideOfRectangle(Rectangle r) {
	// Get direction of rectangle from which this road is located
	if (orientation.isVertical()) {
		if (this.start.x < r.x) {
			return CardinalDirection.W;
		} else if (this.start.x >= r.x + r.width) {
			return CardinalDirection.E;
		} else {
			throw new Error("Vertical road " + this + " is inside rectangle " + r);
		}
	} else {
		if (this.start.y < r.y) {
			return CardinalDirection.N;
		} else if (this.start.y >= r.y + r.height) {
			return CardinalDirection.S;
		} else {
			throw new Error("Horizontal road " + this + " is inside rectangle " + r);
		}
	}
}

public boolean crossesRectangle(EnhancedRectangle r) {
	if (orientation.isVertical()) {
		return start.x >= r.getX() && start.x < r.getX() + r.getWidth();
	} else {
		return start.y >= r.getY() && start.y < r.getY() + r.getHeight();
	}
}

public boolean isRectangleOverlapsRoad(EnhancedRectangle rectangle) {
	EnhancedRectangle ra = new EnhancedRectangle(rectangle);
	if (orientation.isVertical()) {
		if (Utils.integersRangeIntersection(rectangle.getY(), rectangle.getY() + rectangle.getHeight() - 1, start.y, end.y) > 0 && ra.distanceToLine(start, end) < width / 2) {
			// If road line and rectangle overlap in y-axis,
			// and road is close enough to rectangle
			return true;
		}
	} else {
		if (Utils.integersRangeIntersection(rectangle.getX(), rectangle.getX() + rectangle.getWidth() - 1, start.x, end.x) > 0 && ra.distanceToLine(start, end) < width / 2) {
			// Same for x-axis
			return true;
		}
	}
	return false;
}

public boolean isRectangleNearRoad(EnhancedRectangle rectangle) {
	/**
	 * Checks if this road goes along one of the borders of a
	 * rectangle
	 */
	EnhancedRectangle ra = new EnhancedRectangle(rectangle);
	if (orientation.isVertical()) {
		if (Utils.integersRangeIntersection(rectangle.getY(), rectangle.getY() + rectangle.getHeight() - 1, start.y, end.y) > 0 && ra.distanceToLine(start, end) == width / 2 + 1) {
			// If road line and rectangle overlap in y-axis,
			// and road is close enough to rectangle
			return true;
		}
	} else {
		if (Utils.integersRangeIntersection(rectangle.getX(), rectangle.getX() + rectangle.getWidth() - 1, start.x, end.x) > 0 && ra.distanceToLine(start, end) == width / 2 + 1) {
			// Same for x-axis
			return true;
		}
	}
	return false;
}
}
