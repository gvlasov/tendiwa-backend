package org.tendiwa.geometry.extensions;

import org.tendiwa.collections.Collectors;
import org.tendiwa.geometry.BasicPolygon;
import org.tendiwa.geometry.ConstructedPolygon;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public abstract class WobblingPolygon extends ConstructedPolygon {

	private final int parts;

	public WobblingPolygon(int expectedSize, int parts) {
		super(expectedSize);
		this.parts = parts;
	}

	protected final int numberOfParts() {
		return parts;
	}

	protected Point2D wobble(Point2D p, int part) {
		double angle = Math.PI * 2 / (parts / (this.indexOf(p) % 6 + 1)) * part;
		return point2D(
			p.x() + Math.cos(angle) * 6,
			p.y() + Math.sin(angle) * 6
		);
	}

	public final Polygon wobbled(int i) {
		return
			new BasicPolygon(
				stream()
					.map(point -> wobble(point, i))
					.collect(Collectors.toImmutableList())
			);
	}

	public final Stream<Polygon> polygonStream() {
		return IntStream.range(0, parts)
			.mapToObj(this::wobbled);
	}
}
