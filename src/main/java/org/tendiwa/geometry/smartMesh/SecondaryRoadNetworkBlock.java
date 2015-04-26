package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polygon;
import org.tendiwa.geometry.extensions.PolygonSegments;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton;
import org.tendiwa.settlements.BasicDividableLinkedPolygon;
import org.tendiwa.settlements.DividableLinkedPolygon;
import org.tendiwa.settlements.LinkedPolygon;

import java.util.List;
import java.util.stream.Collectors;

public final class SecondaryRoadNetworkBlock extends LinkedPolygon {
	private final List<Point2D> outline;

	SecondaryRoadNetworkBlock(Polygon outline) {
		super(outline);
		assert outline.size() > 2;
		assert !ShamosHoeyAlgorithm.areIntersected(PolygonSegments.toSegments(outline));
		this.outline = ImmutableList.copyOf(outline);
	}

	public List<DividableLinkedPolygon> shrinkToRegions(double depth) {
		return
			new SuseikaStraightSkeleton(outline)
				.cap(depth)
				.stream()
				.map(BasicDividableLinkedPolygon::new)
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Block from " + outline.get(0);
	}
}
