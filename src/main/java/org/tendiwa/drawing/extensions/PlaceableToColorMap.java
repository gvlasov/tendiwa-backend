package org.tendiwa.drawing.extensions;

import org.tendiwa.core.TypePlaceableInCell;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public abstract class PlaceableToColorMap {
	Map<TypePlaceableInCell, Color> colors = new HashMap<>();

	protected final void setColor(TypePlaceableInCell type, Color color) {
		colors.put(type, color);
	}

	public final Color get(TypePlaceableInCell type) {
		return colors.get(type);
	}

	public final boolean containsKey(TypePlaceableInCell type) {
		return colors.containsKey(type);
	}
}

