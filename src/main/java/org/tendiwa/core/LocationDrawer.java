package org.tendiwa.core;

import java.util.Set;

public interface LocationDrawer {
boolean meetsRequirements(Set<LocationFeature> features);

void draw(Location location, LocationPlace place);

boolean canHandlePaths(Set<PathSegment> paths);
}
