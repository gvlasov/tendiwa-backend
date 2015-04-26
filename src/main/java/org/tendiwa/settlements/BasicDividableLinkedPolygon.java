package org.tendiwa.settlements;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Polygon;

public final class BasicDividableLinkedPolygon extends LinkedPolygon implements DividableLinkedPolygon {

	public BasicDividableLinkedPolygon(Polygon outline) {
		super(outline);
	}

	@Override
	public ImmutableSet<DividableLinkedPolygon> subdivideLots(
		double lotWidth,
		double lotDepth,
		double lotDeviance
	) {
		return ImmutableSet.of(this);
	}
}
