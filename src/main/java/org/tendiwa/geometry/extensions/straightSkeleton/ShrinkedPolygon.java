package org.tendiwa.geometry.extensions.straightSkeleton;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Polygon;

import java.util.Iterator;
import java.util.stream.Stream;

public final class ShrinkedPolygon implements Iterable<Polygon> {

	private final ImmutableSet<Polygon> shrinkingResult;

	public ShrinkedPolygon(Polygon polygon, double depth) {
		this.shrinkingResult = new SuseikaStraightSkeleton(polygon).cap(depth);
	}

	@Override
	public Iterator<Polygon> iterator() {
		return shrinkingResult.iterator();
	}

	public Stream<Polygon> stream() {
		return shrinkingResult.stream();
	}
}
