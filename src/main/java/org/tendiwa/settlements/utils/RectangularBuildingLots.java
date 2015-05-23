package org.tendiwa.settlements.utils;

import org.tendiwa.collections.Collectors;
import org.tendiwa.geometry.CrackedPolygon;
import org.tendiwa.geometry.extensions.daveedvMaxRec.RectangleCoverage;
import org.tendiwa.geometry.extensions.straightSkeleton.ShrinkedPolygon;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;

import java.util.Set;

import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public final class RectangularBuildingLots {
	private RectangularBuildingLots() {
		throw new UnsupportedOperationException();
	}

	public static Set<RectangleWithNeighbors> placeInside(MeshedNetwork network) {
		return network.meshCells()
			.stream()
			.flatMap(b -> new ShrinkedPolygon(b, 3.3).stream())
			.flatMap(b -> new CrackedPolygon(b, rectangle(16, 16), 0.5).pieces().stream())
			.map(rasterized -> new RectangleCoverage(rasterized, 21).rectangles())
			.filter(list -> !list.isEmpty())
			.map(list -> new BasicRectangleWithNeighbors(list.get(0), list.subList(1, list.size())))
			.collect(Collectors.toImmutableSet());
	}
}
