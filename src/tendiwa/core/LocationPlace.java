package tendiwa.core;

import tendiwa.geometry.EnhancedRectangle;
import tendiwa.geometry.PathSegment;

import java.util.HashSet;
import java.util.Set;

public class LocationPlace extends EnhancedRectangle {
private Set<PathSegment> pathSegments = new HashSet<>();
private Set<LocationFeature> features = new HashSet<>();


public LocationPlace(int x, int y, int width, int height) {
	super(x, y, width, height);
}

public Set<LocationFeature> getFeatures() {
	return features;
}

public Set<PathSegment> getPathSegments() {
	return pathSegments;
}
}
