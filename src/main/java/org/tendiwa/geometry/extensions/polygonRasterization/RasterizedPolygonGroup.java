package org.tendiwa.geometry.extensions.polygonRasterization;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingList;
import lombok.Lazy;
import org.tendiwa.collections.Collectors;
import org.tendiwa.geometry.OrientedPolygon;

import java.util.Collection;
import java.util.List;

import static org.tendiwa.collections.Collectors.toImmutableList;

/**
 * Rasterizes a group of polygons in such a way that no rasterization results overlap on any cell.
 */
public final class RasterizedPolygonGroup extends ForwardingList<RasterizedPolygon> {
	private final Collection<OrientedPolygon> polygons;

	public RasterizedPolygonGroup(Collection<OrientedPolygon> polygons) {
		this.polygons = polygons;
	}

	private List<RasterizedPolygon> rasterizedPolygons() {
		List<MutableRasterizedPolygon> rasterizations = polygons
			.stream()
			.map(MutableRasterizedPolygon::new)
			.collect(toImmutableList());
		new SegmentConflicts(rasterizations)
			.forEach(SegmentConflict::resolve);
		new PointConflicts(rasterizations)
			.forEach(PointConflict::resolve);
		return rasterizations
			.stream()
			.map(rasterized -> (RasterizedPolygon) rasterized)
			.collect(toImmutableList());

	}


	@Lazy
	@Override
	protected List<RasterizedPolygon> delegate() {
		return rasterizedPolygons();
	}
}
