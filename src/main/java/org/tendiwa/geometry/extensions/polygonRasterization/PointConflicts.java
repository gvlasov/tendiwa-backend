package org.tendiwa.geometry.extensions.polygonRasterization;

import com.google.common.collect.ForwardingCollection;
import lombok.Lazy;

import java.util.Collection;

final class PointConflicts extends ForwardingCollection<PointConflict> {
	private final Collection<MutableRasterizedPolygon> rasterizations;

	public PointConflicts(
		Collection<MutableRasterizedPolygon> rasterizations
	) {
		this.rasterizations = rasterizations;
	}

	@Lazy
	@Override
	protected Collection<PointConflict> delegate() {
		return findConflicts();
	}

	private Collection<PointConflict> findConflicts() {
		return null;
	}
}
