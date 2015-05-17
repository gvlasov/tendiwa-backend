package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;

import java.util.Random;
import java.util.stream.Stream;

final class NetworksProducer {
	private final NetworkGenerationParameters parameters;
	private final Random random;
	private final FullGraph fullGraph;
	private final SplitOriginalGraph splitOriginalGraph;

	NetworksProducer(
		UndirectedGraph<Point2D, Segment2D> originalGraph,
		NetworkGenerationParameters parameters,
		Random random
	) {
		this.parameters = parameters;
		this.random = random;
		this.fullGraph = new FullGraph(originalGraph);
		this.splitOriginalGraph = new SplitOriginalGraph(originalGraph);
		fullGraph.registerSubgraph(splitOriginalGraph);
		ImmutableSet<OrientedCycle> allCycles = splitOriginalGraph.cycles();
		splitOriginalGraph.cycles()
			.stream()
			.map(cycle -> new CycleWithInnerCycles(cycle, allCycles))
			.map(cycle ->
					new IncrementingSubgraph(
						fullGraph,
						cycle,
						parameters,
						random
					)
			);
	}

	/**
	 * Creates a segmentStream that generates {@link IncrementingSubgraph}s for each enclosing cycle of the original graph.
	 */
	Stream<IncrementingSubgraph> stream() {
		// Sort cycles to get a fixed order of iteration (so a City will be reproducible with the same seed).
	}
}