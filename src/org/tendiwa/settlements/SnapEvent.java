package org.tendiwa.settlements;

import org.tendiwa.geometry.Line2D;
import org.tendiwa.geometry.Point2D;

class SnapEvent {
public final Point2D targetPoint;
public final SnapEventType eventType;
public final Line2D road;

SnapEvent(Point2D targetPoint, SnapEventType eventType, Line2D road) {
	this.targetPoint = targetPoint;
	this.eventType = eventType;
	this.road = road;
}

}
