package org.tendiwa.settlements.networks;

import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;

/**
 * Describes how a line snaps to a vertex or an edge of {@link org.tendiwa.settlements.networks.NetworkWithinCycle}.
 */
final class SnapEvent {
	final Point2D targetNode;
	final SnapEventType eventType;
	/**
	 * If {@code eventType} is {@link org.tendiwa.settlements.networks
	 * .SnapEventType#ROAD_SNAP}, it is A road to be snapped to. Otherwise it is null.
	 */
	final Segment2D road;

	SnapEvent(Point2D targetNode, SnapEventType eventType, Segment2D road) {
		this.targetNode = targetNode;
		this.eventType = eventType;
		this.road = road;
	}

}
