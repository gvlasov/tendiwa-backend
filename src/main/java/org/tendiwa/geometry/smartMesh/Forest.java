package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

final class Forest {
	private final MutableGraph2D treesGraph;
	private final MutableGraph2D fullGraph;
	private final OrientedCycle outerCycle;
	private final Set<OrientedCycle> innerCycles;
	private final NetworkGenerationParameters config;
	private final Random random;
	private final DeadEndSet deadEnds;
	private final Set<Point2D> whereBranchesStuckIntoCycles;

	Forest(
		OrientedCycle outerCycle,
		Set<OrientedCycle> innerCycles,
		NetworkGenerationParameters config,
		Random random
	) {
		this.outerCycle = outerCycle;
		this.innerCycles = innerCycles;
		this.config = config;
		this.random = random;
		this.deadEnds = new DeadEndSet();
		this.whereBranchesStuckIntoCycles = new LinkedHashSet<>();
		fullGraph = new MutableGraph2D();
		treesGraph = new MutableGraph2D();
	}


	MutableGraph2D fullGraph() {
		return fullGraph;
	}

	MutableGraph2D treesGraph() {
		return treesGraph;
	}

	Set<Point2D> whereBranchesStuckIntoCycles() {
		return whereBranchesStuckIntoCycles;
	}

	ImmutableSet<Segment2D> deadEndsOnCycles() {
		return ImmutableSet.copyOf(deadEnds.values());
	}

	PropagationEvent tryPlacingSegment(Ray ray, Sector allowedSector) {
		return null;
	}
}