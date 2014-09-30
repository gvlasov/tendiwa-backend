package tests.graph;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.HopcroftKarpBipartiteMatching;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class HopcroftKarpTest {
	@Test
	public void shouldBeDeterministic() {
		UndirectedGraph<Object, DefaultEdge> graph = new SimpleGraph<Object, DefaultEdge>((a, b) -> new DefaultEdge());
		Set<Object> part1 = new HashSet<>();
		Set<Object> part2 = new HashSet<>();
		graph.addVertex("a");
		graph.addVertex("b");
		graph.addVertex("c");
		graph.addVertex("d");
		part1.add("a");
		part1.add("b");
		part1.add("c");
		part1.add("d");
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");
		graph.addVertex("D");
		part2.add("A");
		part2.add("B");
		part2.add("C");
		part2.add("D");
		// Add every possible edge except for a-A, b-B and c-C.
		graph.addEdge("b", "A");
		graph.addEdge("c", "A");
		graph.addEdge("d", "A");
		graph.addEdge("a", "B");
		graph.addEdge("c", "B");
		graph.addEdge("d", "B");
		graph.addEdge("a", "C");
		graph.addEdge("b", "C");
		graph.addEdge("d", "C");
		graph.addEdge("a", "D");
		graph.addEdge("b", "D");
		graph.addEdge("c", "D");
		graph.addEdge("d", "D");
		Set matching = new HopcroftKarpBipartiteMatching<>(graph, part1, part2).getMatching();
		assertTrue(matching.contains(graph.getEdge("d", "C")));
		assertTrue(matching.contains(graph.getEdge("b", "A")));
		assertTrue(matching.contains(graph.getEdge("a", "B")));
		assertTrue(matching.contains(graph.getEdge("c", "D")));
		assertTrue(matching.size() == 4);
	}
}
