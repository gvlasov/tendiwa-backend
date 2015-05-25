package org.tendiwa.geometry;

import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ImmutableSet;
import lombok.Lazy;

import java.util.Collection;


public class CrackedPolygon extends ForwardingCollection<Polygon> {
	private final Polygon polygon;
	private final Dimension pieceSize;
	private final double deviance;

	public CrackedPolygon(
		Polygon polygon,
		Dimension pieceSize,
		double deviance
	) {
		this.polygon = polygon;
		this.pieceSize = pieceSize;
		this.deviance = deviance;
	}

	private ImmutableSet<Polygon> pieces() {
		return ImmutableSet.of(polygon);
	}

	@Lazy
	@Override
	protected final Collection<Polygon> delegate() {
		return pieces();
	}
}
