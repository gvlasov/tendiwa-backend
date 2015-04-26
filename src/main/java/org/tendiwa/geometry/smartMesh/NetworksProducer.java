package org.tendiwa.geometry.smartMesh;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.collections.IterableToStream;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.Point2DRowComparator;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;
import org.tendiwa.settlements.SettlementGenerationException;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.tendiwa.collections.Collectors.toLinkedHashSet;

final class NetworksProducer {
	private final NetworkGenerationParameters parameters;
	private final Random random;
	private final EnclosedCycleDetector enclosedCycleDetector;
	private final MinimumCycleBasis<Point2D, Segment2D> basis;
	private final FullNetwork fullNetwork;
	private final SplitOriginalMesh splitOriginalMesh;

	NetworksProducer(
		UndirectedGraph<Point2D, Segment2D> originalGraph,
		NetworkGenerationParameters parameters,
		Random random
	) {
		this.parameters = parameters;
		this.random = random;
		this.basis = PlanarGraphs.minimumCycleBasis(originalGraph);
		if (basis.minimalCyclesSet().isEmpty()) {
			throw new SettlementGenerationException("A City with 0 city networks was made");
		}
		this.enclosedCycleDetector = constructCycleDetector(basis.minimalCyclesSet());

		this.fullNetwork = new FullNetwork(originalGraph);

		this.splitOriginalMesh = new SplitOriginalMesh(originalGraph);
		fullNetwork.addNetworkPart(splitOriginalMesh);
	}

	/**
	 * Creates a segmentStream that generates {@link OriginalMeshCell}s for each enclosing cycle of the original graph.
	 */
	Stream<OriginalMeshCell> stream() {
		LinkedHashSet<Segment2D> filamentEdges = basis.filamentsSet().stream()
			.flatMap(IterableToStream::stream)
			.collect(Collectors.toCollection(LinkedHashSet::new));
		// Sort cycles to get a fixed order of iteration (so a City will be reproducible with the same seed).
		return basis.minimalCyclesSet().stream()
			.filter(cycle1 -> !enclosedCycleDetector.isEnclosed(cycle1))
				// TODO: Maybe a LinkedHashSet will do here?
			.sorted((o1, o2) -> {
				Point2D p1 = o1.vertexList().get(0);
				Point2D p2 = o2.vertexList().get(0);
				int compare = Double.compare(p1.x(), p2.x());
				if (compare == 0) {
					int compare1 = Double.compare(p1.y(), p2.y());
					assert compare1 != 0;
					return compare1;
				} else {
					return compare;
				}
			})
			.map(cycle -> new OriginalMeshCell(
				fullNetwork,
				splitOriginalMesh.createCycleNetworkPart(cycle),
				enclosedCycleDetector.cyclesEnclosedIn(cycle).stream()
					.map(splitOriginalMesh::createCycleNetworkPart)
					.collect(toLinkedHashSet()),
				parameters,
				random
			));
	}

	private EnclosedCycleDetector constructCycleDetector(Set<MinimalCycle<Point2D, Segment2D>> cycles) {
		return new EnclosedCycleDetector(
			cycles
				.stream()
					// TODO: Do we really need this sorting here?
				.sorted((a, b) ->
						Point2DRowComparator.getInstance().compare(
							a.asEdges().iterator().next().start(),
							b.asEdges().iterator().next().start()
						)
				)
				.collect(Collectors.toList())
		);
	}

	MutableGraph2D fullGraph() {
		return fullNetwork.graph();
	}

	MutableGraph2D fullCycleGraph() {
		return splitOriginalMesh.graph();
	}
}