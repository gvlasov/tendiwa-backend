package org.tendiwa.settlements.networks;

import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.Point2D;

/**
 * Describes how a line snaps to a vertex or an edge of {@link org.tendiwa.settlements.networks.NetworkWithinCycle}.
 */
final class SnapEvent {
	public final Point2D targetNode;
	public final SnapEventType eventType;
	public final Segment2D road;

	SnapEvent(Point2D targetNode, SnapEventType eventType, Segment2D road) {
		this.targetNode = targetNode;
		this.eventType = eventType;
		this.road = road;
	}

}
