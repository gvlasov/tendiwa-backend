package org.tendiwa.settlements;

public class City {
private final RoadGraph primaryRoadNetwork;

public City(RoadGraph roadGraph) {
	primaryRoadNetwork = roadGraph;
}

public RoadGraph getPrimaryRoadNetwork() {
	return primaryRoadNetwork;
}
}
