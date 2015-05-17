package org.tendiwa.geometry;

import com.google.common.collect.ImmutableSet;


public final class CrackedPolygon extends Polygon_Wr {
	private final Dimension pieceSize;
	private final double deviance;

	public CrackedPolygon(
		Polygon polygon,
		Dimension pieceSize,
		double deviance
	) {
		super(polygon);
		this.pieceSize = pieceSize;
		this.deviance = deviance;
	}

	public ImmutableSet<Polygon> pieces() {
		return ImmutableSet.of(this);
	}
}
