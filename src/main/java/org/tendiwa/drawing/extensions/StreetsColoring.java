package org.tendiwa.drawing.extensions;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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

	public static Map<List<Point2D>, Color> compute(Set<List<Point2D>> streets, Color... colors) {
		UndirectedGraph<List<Point2D>, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		for (List<Point2D> street : streets) {
			graph.addVertex(street);
		}
		Multimap<Point2D, List<Point2D>> verticesToStreets = HashMultimap.create();
		for (List<Point2D> street : streets) {
			for (Point2D vertex : street) {
				verticesToStreets.put(vertex, street);
			}
		}
		for (List<Point2D> street : streets) {
			for (Point2D vertex : street) {
				for (List<Point2D> anotherStreet : verticesToStreets.get(vertex)) {
					if (anotherStreet == street) {
						continue;
					}
					graph.addEdge(street, anotherStreet);
				}

			}

		}
		Map<Integer, Set<List<Point2D>>> colorsToStreets = ChromaticNumber.findGreedyColoredGroups(graph);
		Map<List<Point2D>, Color> answer = new HashMap<>();
		for (Map.Entry<Integer, Set<List<Point2D>>> entry : colorsToStreets.entrySet()) {
			int color = entry.getKey();
			for (List<Point2D> street : entry.getValue()) {
				answer.put(street, colors[color]);
			}
		}
		return answer;

	}
}
