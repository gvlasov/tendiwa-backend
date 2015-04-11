package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.ShreddedSegment2D;
import org.tendiwa.geometry.extensions.IntervalsAlongPolygonBorder;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.tendiwa.collections.Collectors.toLinkedHashSet;

final class OuterCyclePlanting {
	private final Forest forest;
	private final OrientedCycle outerCycle;
	private final DirectionDeviation directionDeviation;
	private final NetworkGenerationParameters parameters;
	private final Random random;

	OuterCyclePlanting(
		Forest forest,
		OrientedCycle outerCycle,
		DirectionDeviation directionDeviation,
		NetworkGenerationParameters parameters,
		Random random
	) {
		this.forest = forest;
		this.outerCycle = outerCycle;
		this.directionDeviation = directionDeviation;
		this.parameters = parameters;
		this.random = random;
	}

	Set<InnerTree> seeds() {
		Set<ShreddedSegment2D> segmentsWithRootPoints = snapShredsToSegmentEnds(
			pointsAtRegularIntervals(
				outerCycle
			)
		);
		return segmentsWithRootPoints.stream()
			.flatMap(ShreddedSegment2D::pointStream)
			.distinct()
			.map(this::createTreeSeedOnEnclosingCycle)
			.collect(toLinkedHashSet());
	}

	private Map<Segment2D, List<Point2D>> pointsAtRegularIntervals(OrientedCycle cycle) {
		return IntervalsAlongPolygonBorder.compute(
			cycle.vertexList(),
			parameters.segmentLength,
			parameters.secondaryNetworkSegmentLengthDeviation,
			cycle.graph()::getEdge,
			random
		);
	}

	private Set<ShreddedSegment2D> snapShredsToSegmentEnds(Map<Segment2D, List<Point2D>> points) {
		return new CycleWithStartingPoints(parameters).snapStartingPoints(points);
	}

	private InnerTree createTreeSeedOnEnclosingCycle(Point2D root) {
		return new InnerTree(
			outerCycle.deviatedAngleBisector(root, true),
			new OrientedCycleSector(outerCycle, root, true),
			forest,
			directionDeviation,
			parameters
		);
	}
}
