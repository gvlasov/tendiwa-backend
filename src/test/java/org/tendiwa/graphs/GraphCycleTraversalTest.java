package org.tendiwa.graphs;

import com.google.common.collect.Lists;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import java.util.StringJoiner;

import static org.junit.Assert.*;

public class GraphCycleTraversalTest {
	@Test
	public void correctTraversionOrder() {
		UndirectedGraph<Integer, DefaultEdge> graph =
			new GraphConstructor<Integer, DefaultEdge>((a, b) -> new DefaultEdge())
				.cycleOfVertices(Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8))
				.graph();
		StringJoiner currents = new StringJoiner(", ");
		StringJoiner nexts = new StringJoiner(", ");
		GraphCycleTraversal
			.traverse(graph)
			.startingWith(4)
			.stream()
			.forEach(triplet -> {
				currents.add(triplet.current().toString());
				nexts.add(triplet.next().toString());
			});
		boolean variant1 = "4, 5, 6, 7, 8, 0, 1, 2, 3".equals(currents.toString())
			&& "5, 6, 7, 8, 0, 1, 2, 3, 4".equals(nexts.toString());
		boolean variant2 = "4, 3, 2, 1, 0, 8, 7, 6, 5".equals(currents.toString())
			&& "3, 2, 1, 0, 8, 7, 6, 5, 4".equals(nexts.toString());
		assertTrue(variant1 || variant2);
		System.out.println(currents);
		System.out.println(nexts);

	}

}