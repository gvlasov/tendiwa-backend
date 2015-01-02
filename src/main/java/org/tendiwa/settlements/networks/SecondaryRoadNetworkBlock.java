package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableList;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.extensions.PolygonSegments;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.geometry.extensions.straightSkeleton.SuseikaStraightSkeleton;
import org.tendiwa.settlements.BlockRegion;
import org.tendiwa.settlements.EnclosedBlock;

import java.util.List;
import java.util.stream.Collectors;

public final class SecondaryRoadNetworkBlock extends EnclosedBlock {
	private final List<Point2D> outline;

	SecondaryRoadNetworkBlock(List<Point2D> outline) {
		super(outline);
		assert outline.size() > 2;
		assert !ShamosHoeyAlgorithm.areIntersected(PolygonSegments.toSegments(outline));
		this.outline = ImmutableList.copyOf(outline);
	}

	public List<BlockRegion> shrinkToRegions(double depth, int seed) {
		return new SuseikaStraightSkeleton(outline).cap(depth)
			.stream()
			.map(polygon -> new BlockRegion(polygon, seed))
			.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Block from " + outline.get(0);
	}
}
