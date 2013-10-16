package tendiwa.core;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

public class LocationPlace extends EnhancedRectangle {
private final WorldRectangleBuilder worldBuilder;
private Set<PathSegment> pathSegments = new HashSet<>();
private Set<LocationFeature> features = new HashSet<>();

LocationPlace(EnhancedRectangle r, WorldRectangleBuilder worldBuilder) {
	super(r);
	this.worldBuilder = worldBuilder;
}

void addFeature(LocationFeature feature) {
	features.add(feature);
}

public Set<LocationFeature> getFeatures() {
	return features;
}

public Set<PathSegment> getPathSegments() {
	return pathSegments;
}


/**
 * Returns LocationPlaces that are neighbors to this LocationPlace on the world map.
 *
 * @return A collection of neighbor LocationPlaces.
 */
public ImmutableSet<LocationNeighborship> getNeighborships() {
	RectangleSystem rs = worldBuilder.done();
	ImmutableSet.Builder<LocationNeighborship> builder = ImmutableSet.builder();
	for (CardinalDirection dir : CardinalDirection.values()) {
		for (EnhancedRectangle neighbor : rs.getNeighborsFromSide(this, dir)) {
			builder.add(new LocationNeighborship(worldBuilder.rectanglesToPlaces.get(neighbor), dir));
		}
	}
	return builder.build();
}
}
