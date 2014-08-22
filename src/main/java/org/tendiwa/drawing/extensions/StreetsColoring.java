package org.tendiwa.drawing.extensions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.codehaus.groovy.transform.ImmutableASTTransformation;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ChromaticNumber;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.tendiwa.geometry.Point2D;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class StreetsColoring {
	private StreetsColoring() {

	}

	public static Map<ImmutableList<Point2D>, Color> compute(Set<ImmutableList<Point2D>> streets, Color... colors) {
		UndirectedGraph<ImmutableList<Point2D>, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		for (ImmutableList<Point2D> street : streets) {
			graph.addVertex(street);
		}
		Multimap<Point2D, ImmutableList<Point2D>> verticesToStreets = HashMultimap.create();
		for (ImmutableList<Point2D> street : streets) {
			for (Point2D vertex : street) {
				verticesToStreets.put(vertex, street);
			}
		}
		for (ImmutableList<Point2D> street : streets) {
			for (Point2D vertex : street) {
				for (ImmutableList<Point2D> anotherStreet : verticesToStreets.get(vertex)) {
					if (anotherStreet == street) {
						continue;
					}
					graph.addEdge(street, anotherStreet);
				}

			}

		}
		Map<Integer, Set<ImmutableList<Point2D>>> colorsToStreets
			= ChromaticNumber.findGreedyColoredGroups(graph);
		Map<ImmutableList<Point2D>, Color> answer = new HashMap<>();
		for (Map.Entry<Integer, Set<ImmutableList<Point2D>>> entry : colorsToStreets.entrySet()) {
			int color = entry.getKey();
			for (ImmutableList<Point2D> street : entry.getValue()) {
				answer.put(street, colors[color]);
			}
		}
		return answer;
	}
}
