package tendiwa.geometry;

import com.google.common.collect.ImmutableList;
import tendiwa.core.LocationFeature;

import java.util.*;

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

/**
 * <p>Sets a particular LocationFeature to all EnhancedRectangles a {@code placeable} consists of.</p> <p>If {@code
 * placeable} is an EnhancedRectangle, for example, it will affect a single rectangle — itself. If {@code placeable} is
 * a {@link RectangleSequence}, then LocationFeature will be added to each EnhancedRectangle that RectangleSequence
 * consists of.</p>
 *
 * @param placeable
 * 	A placeable object.
 * @param feature
 * 	A property of Location to set.
 * @return This WorldRectangleBuilder for method chaining.
 */
private WorldRectangleBuilder setLocationFeatures(Placeable placeable, LocationFeature feature) {
	for (EnhancedRectangle rectangle : placeable.getRectangles()) {
		if (!rs.content.contains(rectangle)) {
			throw new IllegalArgumentException("Rectangle " + rectangle + " from argument "+placeable+" is not present in this WorldRectangleBuilder");
		}
		List<LocationFeature> values;
		if (!locationFeatures.containsKey(rectangle)) {
			values = new ArrayList<>();
			locationFeatures.put(rectangle, values);
		} else {
			values = locationFeatures.get(rectangle);
		}
		values.add(feature);
	}
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
public WorldRectangleBuilder findAllRectangles(FindCriteria criteria) {
	foundRectangles = new RectangleSequence();
	for (Placeable placeable : placeables) {
		for (EnhancedRectangle rectangle : placeable.getRectangles()) {
			if (criteria.check(rectangle, rs, this)) {
				foundRectangles.addRectangle(rectangle);
			}
		}
	}
	return this;
}
}
