package org.tendiwa.settlements;

import org.tendiwa.geometry.Point2D;

public class SecondaryRoadNetworkNode {
public final boolean isDeadEnd;
public final Point2D point;

public SecondaryRoadNetworkNode(Point2D point, boolean isDeadEnd) {
	this.point = point;
	this.isDeadEnd = isDeadEnd;
}

@Override
public boolean equals(Object o) {
	if (this == o) return true;
	if (o == null || getClass() != o.getClass()) return false;

	SecondaryRoadNetworkNode that = (SecondaryRoadNetworkNode) o;

	if (isDeadEnd != that.isDeadEnd) return false;
	if (!point.equals(that.point)) return false;

	return true;
}

@Override
public String toString() {
	return "node{" +
		"dead=" + isDeadEnd +
		", p=" + point +
		'}';
}

@Override
public int hashCode() {
	int result = (isDeadEnd ? 1 : 0);
	result = 31 * result + point.hashCode();
	return result;
}
}
