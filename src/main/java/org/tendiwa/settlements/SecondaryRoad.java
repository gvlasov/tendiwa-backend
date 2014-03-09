package org.tendiwa.settlements;

import org.tendiwa.geometry.Line2D;

public class SecondaryRoad {
public final SecondaryRoadNetworkNode start;
public final SecondaryRoadNetworkNode end;

public SecondaryRoad(SecondaryRoadNetworkNode start, SecondaryRoadNetworkNode end) {
	this.start = start;
	this.end = end;
}

public Line2D toLine() {
	return new Line2D(start.point, end.point);
}
}
