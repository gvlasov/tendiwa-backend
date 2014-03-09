package org.tendiwa.settlements;

class SnapEvent {
public final SecondaryRoadNetworkNode targetNode;
public final SnapEventType eventType;
public final SecondaryRoad road;

SnapEvent(SecondaryRoadNetworkNode targetNode, SnapEventType eventType, SecondaryRoad road) {
	this.targetNode = targetNode;
	this.eventType = eventType;
	this.road = road;
}

}
