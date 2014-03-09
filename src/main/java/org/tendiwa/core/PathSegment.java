package org.tendiwa.core;

public class PathSegment {
private Class<LocationFeature> type;

public Class<? extends LocationFeature> getType() {
	return type;
}
}
