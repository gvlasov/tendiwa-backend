package org.tendiwa.demos;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.HopcroftKarpBipartiteMatching;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.drawing.GraphExplorer;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.math.IntegerPermutationGenerator;
import org.tendiwa.math.JerrumSinclairMarkovChain;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class JerrumSinclairMarkovChainSimulationResultExplorer implements Runnable {
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
			.mapToObj(i -> new Point2D(40, 20 + 20 * i))
			.collect(toList());
		List<Point2D> partition2 = range(0, 11)
			.mapToObj(i -> new Point2D(80, 20 + 20 * i))
			.collect(toList());
		partition1.forEach(underlyingGraph::addVertex);
		partition2.forEach(underlyingGraph::addVertex);
		assert partition1.size() == partition2.size();
		int size = partition1.size();
		int numberOfEdges = 6;
		for (int i = 0; i < size; i++) {
			Point2D vertex = partition1.get(i);
			for (int number : IntegerPermutationGenerator.generateUsingFisherYates(size, numberOfEdges, new Random(i))) {
				Segment2D edge = underlyingGraph.addEdge(
					vertex,
					partition2.get(number)
				);
				assert edge != null;
			}

			System.out.println("Vertex " + i + " has " + underlyingGraph.edgesOf(vertex).size() + " edges");
		}
		Set<Segment2D> matching = new HopcroftKarpBipartiteMatching<>(
			underlyingGraph,
			ImmutableSet.copyOf(partition1),
			ImmutableSet.copyOf(partition2)
		).getMatching();
		int failures = 0, successes = 0;
		for (int i = 0; i < 1000; i++) {
//			try {
				UndirectedGraph<Point2D, Segment2D> matchingGraph = new SimpleGraph<>(PlanarGraphs.getEdgeFactory());
				underlyingGraph.vertexSet().forEach(matchingGraph::addVertex);
				matching.forEach(e -> matchingGraph.addEdge(e.start, e.end, e));
				UndirectedGraph<Point2D, Segment2D> generatedMatching = JerrumSinclairMarkovChain
					.inGraph(underlyingGraph)
					.withInitialMatchingToMutate(matchingGraph)
					.withNumberOfSteps(i)
					.withRandom(new Random(1123123));
				successes++;
//			} catch (RuntimeException e) {
//				failures++;
//			}
		}
		System.out.println("Failures: " + failures + ", successes: " + successes);
//		new GraphExplorer(generatedMatching, 800, 600, 10);
	}
}
