package tendiwa.geometry;

import com.google.common.collect.ImmutableSet;
import tendiwa.core.HorizontalPlane;
import tendiwa.core.LocationFeature;
import tendiwa.core.LocationPlace;

import java.util.*;

public class WorldRectangleBuilder extends RectangleSystemBuilder {

public Map<EnhancedRectangle, List<LocationFeature>> locationFeatures = new HashMap<>();

private HashSet<LocationPlace> locations = new HashSet<>();

WorldRectangleBuilder() {
	super(0);
}
public WorldRectangleBuilder setLocationFeatures(RectanglePointer pointer, Class<? extends LocationFeature> feature) {
	return setLocationFeatures(getRectangleByPointer(pointer), feature);
}
public WorldRectangleBuilder setLocationFeatures(String name, Class<? extends LocationFeature> feature) {
	return setLocationFeatures(getByName(name).getBounds(), feature);
}
public WorldRectangleBuilder setLocationFeatures(int index, Class<? extends LocationFeature> feature) {
	return setLocationFeatures(getByIndex(index), feature);
}
private WorldRectangleBuilder setLocationFeatures(EnhancedRectangle rectangle, Class<? extends LocationFeature> feature) {
	if (!rectangles.contains(rectangle)) {
		throw new IllegalArgumentException("Rectangle "+rectangle+" is not present in this WorldRectangleBuilder");
	}
	return this;
}

@Override
public WorldRectangleBuilder place(Placeable what, Placement where) {
	return (WorldRectangleBuilder) super.place(what, where);
}

@Override
public WorldRectangleBuilder place(String name, Placeable what, Placement where) {
	return (WorldRectangleBuilder) super.place(name, what, where);
}
public Set<LocationPlace> getLocationPlaces() {
	return ImmutableSet.copyOf(locations);
}

}
