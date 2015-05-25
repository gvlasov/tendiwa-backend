package org.tendiwa.settlements.utils;

import org.tendiwa.collections.Collectors;
import org.tendiwa.geometry.CrackedPolygon;
import org.tendiwa.geometry.OrientedPolygon;
import org.tendiwa.geometry.extensions.daveedvMaxRec.RectangleCoverage;
import org.tendiwa.geometry.extensions.polygonRasterization.RasterizedPolygonGroup;
import org.tendiwa.geometry.extensions.straightSkeleton.ShrinkedPolygon;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.graphs.graphs2d.BasicOrientedPolygon;

import java.util.Set;

import static org.tendiwa.collections.Collectors.toImmutableList;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public final class RectangularBuildingLots {
	private RectangularBuildingLots() {
		throw new UnsupportedOperationException();
	}

	public static Set<RectangleWithNeighbors> placeInside(MeshedNetwork network) {
		return network.meshCells()
			.stream()
			.flatMap(polygon -> new ShrinkedPolygon(polygon, 3.3).stream())
			.map(shrinked ->
					new CrackedPolygon(
						shrinked,
						rectangle(16, 16),
						0.5
					)
						.stream()
						.map(poly -> (OrientedPolygon) new BasicOrientedPolygon(poly))
						.collect(toImmutableList())
			)
			.flatMap(shards -> new RasterizedPolygonGroup(shards).stream())
			.map(rasterizedPolygon -> new RectangleCoverage(rasterizedPolygon, 21))
			.map(BasicRectangleWithNeighbors::new)
			.collect(Collectors.toImmutableSet());
	}
}
