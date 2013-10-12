package tendiwa.geometry;

import tendiwa.core.LocationFeature;
import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.PathSegment;

import java.util.HashSet;
import java.util.Set;

public class LocationPlace {
private Set<PathSegment> pathSegments = new HashSet<>();
private Set<LocationFeature> features = new HashSet<>();
private final EnhancedRectangle rectangle;


LocationPlace(int x, int y, int width, int height) {
	rectangle = new EnhancedRectangle(x, y, width, height);
}

LocationPlace(EnhancedRectangle r) {
	rectangle = new EnhancedRectangle(r);
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

public EnhancedRectangle getRectangle() {
	return rectangle;
}
}
