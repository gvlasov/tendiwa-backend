package org.tendiwa.demos;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.HopcroftKarpBipartiteMatching;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.GraphExplorer;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.graphs.algorithms.jerrumSinclair.QuasiJerrumSinclairMarkovChain;
import org.tendiwa.math.FisherYatesPermutation;

import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class JerrumSinclairMarkovChainSimulationResultExplorer implements Runnable {

	public static final int NUMBER_OF_MATCHINGS_TO_GENERATE = 8;

	public static void main(String[] args) {
		Demos.run(JerrumSinclairMarkovChainSimulationResultExplorer.class);
	}

	@Override
	public void run() {
		UndirectedGraph<Point2D, Segment2D> underlyingGraph = new SimpleGraph<>(
			PlanarGraphs.getEdgeFactory()
		);
		// Partition 1 vertices
		List<Point2D> partition1 = range(0, 11)
			.mapToObj(i -> point2D(40, 20 + 20 * i))
			.collect(toList());
		List<Point2D> partition2 = range(0, 11)
			.mapToObj(i -> point2D(80, 20 + 20 * i))
			.collect(toList());
		partition1.forEach(underlyingGraph::addVertex);
		partition2.forEach(underlyingGraph::addVertex);
		assert partition1.size() == partition2.size();
		int size = partition1.size();
		int numberOfEdges = 6;
		for (int i = 0; i < size; i++) {
			Point2D vertex = partition1.get(i);
			new FisherYatesPermutation(size, numberOfEdges, new Random(i))
				.stream()
				.forEach(number -> {
					Segment2D edge = underlyingGraph.addEdge(
						vertex,
						partition2.get(number)
					);
					assert edge != null;
				});
		}
		ImmutableSet<Point2D> onePartition = ImmutableSet.copyOf(partition1);
		Set<Segment2D> matching = new HopcroftKarpBipartiteMatching<>(
			underlyingGraph,
			onePartition,
			ImmutableSet.copyOf(partition2)
		).getMatching();
		for (int i = 0; i < NUMBER_OF_MATCHINGS_TO_GENERATE; i++) {
			UndirectedGraph<Point2D, Segment2D> matchingGraph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
			underlyingGraph.vertexSet().forEach(matchingGraph::addVertex);
			matching.forEach(e -> matchingGraph.addEdge(e.start(), e.end(), e));
			UndirectedGraph<Point2D, Segment2D> generatedMatching = QuasiJerrumSinclairMarkovChain
				.inGraph(underlyingGraph)
				.withInitialMatching(matching)
				.withOneOfPartitions(onePartition)
				.withNumberOfSteps(8000)
				.withRandom(new Random(0));
			new GraphExplorer(generatedMatching, 400, 400, 2);
		}
	}
}
