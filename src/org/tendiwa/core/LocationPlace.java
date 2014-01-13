package org.tendiwa.core;

import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a place where user will drawWorld a location. This class is supposed to hold properties assigned to it at the
 * world generation stage: {@link LocationFeature}, {@link PathSegment}s.
 */
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
			builder.add(
				new LocationNeighborship(worldBuilder.rectanglesToPlaces.get(neighbor),
					dir,
					getCommonSidePiece(neighbor)));
		}
	}
	return builder.build();
}

/**
 * Retuns a rectangle inside this LocationPlace whose one side in a RectangleSidePiece common between this LocationPlace
 * and a place from {@code Neighborship}.
 *
 * @param neighborship
 * 	A neighbor of this LocationPlace to get a common RectangleSidePiece of. Defines one dimension of the resulting
 * 	rectangle.
 * @param anotherDimension Another dimension of the new rectangle.
 * @return A rectangle on border of this Location place that covers all common border segment with a neighbor
 *         LocationPlace.
 */
public EnhancedRectangle getRectangleInFrontOfNeighbor(LocationNeighborship neighborship, int anotherDimension) {
	EnhancedRectangle absoluteCoordinatesRec = getCommonSidePiece(neighborship.getPlace())
		.createRectangle(anotherDimension);
	return new EnhancedRectangle(
		absoluteCoordinatesRec.x-x,
		absoluteCoordinatesRec.y-y,
		absoluteCoordinatesRec.width,
		absoluteCoordinatesRec.height
	);

}
}
