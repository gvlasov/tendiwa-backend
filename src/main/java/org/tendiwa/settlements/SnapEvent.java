package org.tendiwa.settlements;

import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

class SnapEvent {
public final Point2D targetNode;
public final SnapEventType eventType;
public final Line2D road;

SnapEvent(Point2D targetNode, SnapEventType eventType, Line2D road) {
	this.targetNode = targetNode;
	this.eventType = eventType;
	this.road = road;
}

}
