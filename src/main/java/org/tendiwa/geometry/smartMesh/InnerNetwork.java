package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.util.Collection;
import java.util.Random;

final class InnerNetwork implements NetworkPart {
	private final MutableGraph2D graph;
	private final FullNetwork fullNetwork;
	private final OrientedCycle outerCycle;
	private final Collection<OrientedCycle> innerCycles;
	private final NetworkGenerationParameters parameters;
	private final Random random;

	InnerNetwork(
		FullNetwork fullNetwork,
		OrientedCycle outerCycle,
		Collection<OrientedCycle> innerCycles,
		NetworkGenerationParameters parameters,
		Random random
	) {
		this.fullNetwork = fullNetwork;
		this.outerCycle = outerCycle;
		this.innerCycles = innerCycles;
		this.parameters = parameters;
		this.random = random;
		this.graph = new MutableGraph2D();
		new Forester(
			outerCycle,
			innerCycles,
			parameters,
			random
		).
	}

	@Override
	public MutableGraph2D graph() {
		return graph;
	}

	@Override
	public void integrateSplitEdge(CutSegment2D cutSegment) {
		NetworkPart.super.integrateSplitEdge(cutSegment);
		Segment2D original = cutSegment.originalSegment();
		if (deadEndSet.hasDeadEndSegment(original)) {
			assert cutSegment.segmentStream().count() == 2;
			Segment2D replacement = cutSegment
				.segmentStream()
				.skip(1)
				.findFirst()
				.get();
			deadEndSet.replaceDeadEndSegment(original, replacement);
		}
	}
}
