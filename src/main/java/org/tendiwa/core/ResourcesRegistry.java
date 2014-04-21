package org.tendiwa.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class ResourcesRegistry {
private static ResourcesRegistry ourInstance = new ResourcesRegistry();
private static Collection<LocationFeature> features = new ArrayList<>();
private static ArrayList<LocationDrawer> drawers = new ArrayList<>();

private ResourcesRegistry() {
}

public static ResourcesRegistry getInstance() {
	return ourInstance;
}

public static void registerDrawer(LocationDrawer drawer) {
	drawers.add(drawer);
}

public static void registerFeature(LocationFeature feature) {
	features.add(feature);
}

public static LocationDrawer getLocationDrawerFor(LocationPlace place) {
	Set<LocationFeature> features = place.getFeatures();
	Set<PathSegment> pathSegments = place.getPathSegments();
	for (LocationDrawer drawer : drawers) {
		if (drawer.meetsRequirements(features) && drawer.canHandlePaths(pathSegments)) {
			return drawer;
		}
	}
	throw new LocationException("There is no LocationDrawer suitable for place "+place+" with features "+features);
}
}

