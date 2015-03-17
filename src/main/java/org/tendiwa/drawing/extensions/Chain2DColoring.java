package org.tendiwa.drawing.extensions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ChromaticNumber;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.tendiwa.collections.IterableToStream;
import org.tendiwa.collections.StreamIterable;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Chain2D;

import java.awt.Color;
import java.util.*;
import java.util.stream.Stream;

public final class Chain2DColoring {
	private final Set<Chain2D> streets;
	private final List<Color> colors;

	private Chain2DColoring(Set<Chain2D> streets, List<Color> colors) {
		this.colors = colors;
		this.streets = streets;
	}

	public static ImmutableMap<Chain2D, Color> compute(
		Set<Chain2D> streets,
		List<Color> colors
	) {
		return new Chain2DColoring(streets, colors).map();
	}

	private ImmutableMap<Chain2D, Color> map() {
		ImmutableMap.Builder<Chain2D, Color> builder = ImmutableMap.builder();
		colorIdsToStreets(streets)
			.entrySet()
			.stream()
			.map(ColorGroup::new)
			.flatMap(ColorGroup::colorings)
			.forEach(coloring -> builder.put(coloring.chain, coloring.color));
		return builder.build();
	}

	private static Map<Integer, Set<Chain2D>> colorIdsToStreets(Set<Chain2D> streets) {
		return ChromaticNumber.findGreedyColoredGroups(
			computeIntersectionsGraph(streets)
		);
	}

	private static UndirectedGraph<Chain2D, DefaultEdge> computeIntersectionsGraph(Set<Chain2D> streets) {
		UndirectedGraph<Chain2D, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		streets.forEach(graph::addVertex);
		commonPoints(streets)
			.asMap()
			.values()
			.stream()
			.map(Chain2DColoring::pairsOfIntersectingStreets)
			.flatMap(IterableToStream::stream)
			.forEach(
				combination ->
					graph.addEdge(
						combination.getValue(0),
						combination.getValue(1)
					)
			);
		return graph;
	}

	private static Multimap<Point2D, Chain2D> commonPoints(Set<Chain2D> streets) {
		Multimap<Point2D, Chain2D> pointsToStreets = HashMultimap.create();
		for (Chain2D street : streets) {
			for (Point2D point : new StreamIterable<>(street.asPointStream())) {
				pointsToStreets.put(point, street);
			}
		}
		return pointsToStreets;
	}

	private static Generator<Chain2D> pairsOfIntersectingStreets(Collection<Chain2D> intersectingStreets) {
		return Factory.createSimpleCombinationGenerator(
			Factory.createVector(intersectingStreets),
			2
		);
	}

	private final class ColorGroup {
		private final Color color;
		private final Collection<Chain2D> members;

		private ColorGroup(Map.Entry<Integer, Set<Chain2D>> group) {
			this.color = colors.get(group.getKey());
			this.members = group.getValue();
		}

		private Stream<ChainColoring> colorings() {
			return members.stream()
				.map(member -> new ChainColoring(member, color));
		}
	}

	private static class ChainColoring {
		private final Chain2D chain;
		private final Color color;

		public ChainColoring(Chain2D chain2D, Color color) {
			this.chain = chain2D;
			this.color = color;
		}
	}
}
