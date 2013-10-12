package tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import tendiwa.core.LocationFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldRectangleBuilder extends RectangleSystemBuilder {

public Map<EnhancedRectangle, List<LocationFeature>> locationFeatures = new HashMap<>();

WorldRectangleBuilder() {
	super(0);
}

public WorldRectangleBuilder setLocationFeatures(RectanglePointer pointer, LocationFeature feature) {
	return setLocationFeatures(getRectangleByPointer(pointer), feature);
}

public WorldRectangleBuilder setLocationFeatures(String name, LocationFeature feature) {
	return setLocationFeatures(getByName(name).getBounds(), feature);
}

public WorldRectangleBuilder setLocationFeatures(int index, LocationFeature feature) {
	return setLocationFeatures(getByIndex(index), feature);
}

private WorldRectangleBuilder setLocationFeatures(EnhancedRectangle rectangle, LocationFeature feature) {
	if (!rectangles.contains(rectangle)) {
		throw new IllegalArgumentException("Rectangle " + rectangle + " is not present in this WorldRectangleBuilder");
	}
	List<LocationFeature> values;
	if (!locationFeatures.containsKey(rectangle)) {
		values = new ArrayList<>();
		locationFeatures.put(rectangle, values);
	} else {
		values = locationFeatures.get(rectangle);
	}
	values.add(feature);
	return this;
}

public WorldRectangleBuilder place(Placeable what, Placement where) {
	return (WorldRectangleBuilder) super.place(what, where);
}

@Override
public WorldRectangleBuilder place(String name, Placeable what, Placement where) {
	return (WorldRectangleBuilder) super.place(name, what, where);
}

public ImmutableList<LocationPlace> getLocationPlaces() {
	ImmutableList.Builder<LocationPlace> builder = ImmutableList.builder();
	for (EnhancedRectangle r : rectangles) {
		LocationPlace locationPlace = new LocationPlace(r);
		if (locationFeatures.containsKey(r)) {
			for (LocationFeature feature : locationFeatures.get(r)) {
				locationPlace.addFeature(feature);
			}
		}
		builder.add(locationPlace);
	}
	return builder.build();
}
}
