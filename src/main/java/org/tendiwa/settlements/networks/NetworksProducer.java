package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.collections.IterableToStream;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.Point2DRowComparator;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.graphs.CommonEdgeSplitter;
import org.tendiwa.graphs.Filament;
import org.tendiwa.graphs.MinimalCycle;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.settlements.SettlementGenerationException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class NetworksProducer {
	private final UndirectedGraph<Point2D, Segment2D> originalGraph;
	private final NetworkGenerationParameters parameters;
	private final Random random;
	private final EnclosedCycleDetector enclosedCycleDetector;
	private final MinimumCycleBasis<Point2D, Segment2D> basis;
	private final CommonEdgeSplitter<Point2D, Segment2D> commonEdgeSplitter;
	private final HolderOfSplitCycleEdges holderOfSplitCycleEdges = new HolderOfSplitCycleEdges();
	private final UndirectedGraph<Point2D, Segment2D> fullGraph;

	NetworksProducer(
		UndirectedGraph<Point2D, Segment2D> originalGraph,
		NetworkGenerationParameters parameters,
		Random random
	) {

		this.originalGraph = originalGraph;
		this.parameters = parameters;
		this.random = random;
		this.basis = new MinimumCycleBasis<>(
			originalGraph,
			Point2DVertexPositionAdapter.get()
		);
		if (basis.minimalCyclesSet().isEmpty()) {
			throw new SettlementGenerationException("A City with 0 city networks was made");
		}
		this.enclosedCycleDetector = constructCycleDetector(basis.minimalCyclesSet());
		this.commonEdgeSplitter = new CommonEdgeSplitter<>(
			PlanarGraphs.getEdgeFactory()
		);
		this.fullGraph = PlanarGraphs.copyGraph(originalGraph);
	}

	/**
	 * Creates a stream that generates {@link NetworkWithinCycle}s for each enclosing cycle of the original graph.
	 */
	Stream<NetworkWithinCycle> stream() {
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
				int compare = Double.compare(p1.x, p2.x);
				if (compare == 0) {
					int compare1 = Double.compare(p1.y, p2.y);
					assert compare1 != 0;
					return compare1;
				} else {
					return compare;
				}
			})
			.map(cycle -> new NetworkWithinCycle(
				fullGraph,
				cycle,
				originalGraph,
				filamentEdges,
				enclosedCycleDetector.cyclesEnclosedIn(cycle),
				holderOfSplitCycleEdges,
				commonEdgeSplitter,
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
							a.iterator().next().start,
							b.iterator().next().start
						)
				)
				.collect(Collectors.toList())
		);
	}

	/**
	 * Constructs a graph of low level roads for a {@link NetworkWithinCycle} that resides inside a {@code cycle}.
	 *
	 * @param cycle
	 * 	A MinimalCycle inside which a NetworkWithinCycle resides.
	 * @param filaments
	 * 	All the filaments of {@link org.tendiwa.settlements.networks.SegmentNetworkBuilder#graph}.
	 * @param enclosedCycles
	 * 	All the cycles of {@link org.tendiwa.settlements.networks.SegmentNetworkBuilder#graph}'s MinimalCycleBasis that
	 * 	reside inside other cycles.
	 * @return A graph containing the {@code cycle} and all the {@code filaments}.
	 */
	private static UndirectedGraph<Point2D, Segment2D> constructNetworkOriginalGraph(
		MinimalCycle<Point2D, Segment2D> cycle,
		Set<Filament<Point2D, Segment2D>> filaments,
		Collection<MinimalCycle<Point2D, Segment2D>> enclosedCycles
	) {
		UndirectedGraph<Point2D, Segment2D> graph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
		for (Filament<Point2D, Segment2D> filament : filaments) {
			filament.vertexList().forEach(graph::addVertex);
			for (Segment2D line : filament) {
				graph.addEdge(line.start, line.end, line);
			}
		}
		cycle.vertexList().forEach(graph::addVertex);
		for (Segment2D edge : cycle) {
			graph.addEdge(edge.start, edge.end, edge);
		}
		for (MinimalCycle<Point2D, Segment2D> enclosedCycle : enclosedCycles) {
			enclosedCycle.vertexList().forEach(graph::addVertex);
			// If a cycle is enclosed, all the networks know about that cycle,
			// whether a network encloses that cycle or not. The cycle just won't affect building a network
			// if it is not within that network.
			// TODO: ^^ probably outdated, I rewrote enclosed cycle detection so only a relevant network knows about
			// its enclosed cycles.
			for (Segment2D edge : enclosedCycle) {
				graph.addEdge(edge.start, edge.end, edge);
			}
		}

		return graph;
	}
}
