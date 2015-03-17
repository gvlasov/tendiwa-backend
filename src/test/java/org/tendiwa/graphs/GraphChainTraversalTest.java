package org.tendiwa.graphs;

import com.google.common.collect.Lists;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PointTrail;

import java.awt.Color;
import java.util.List;
import java.util.StringJoiner;

import static org.junit.Assert.*;

public class GraphChainTraversalTest {
	@Test
	public void correctCycleTraversalOrder() {
		UndirectedGraph<Integer, DefaultEdge> graph =
			new GraphConstructor<Integer, DefaultEdge>((a, b) -> new DefaultEdge())
				.cycleOfVertices(Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8))
				.graph();
		StringJoiner currents = new StringJoiner(", ");
		StringJoiner nexts = new StringJoiner(", ");
		GraphChainTraversal
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
	}

	@Test
	public void traversalWithEndsOfDegree1() {
		List<Point2D> trail = new PointTrail(50, 50)
			.moveByX(50)
			.moveByY(50)
			.moveByX(50)
			.moveByY(-50)
			.points();
		UndirectedGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
			.chain(trail)
			.graph();
		new TestCanvas(3, 200, 200).draw(graph, DrawingGraph.withColorAndAntialiasing(Color.red));
		Point2D startVertex = new Point2D(50, 50);
		long verticesTraversed = GraphChainTraversal
			.traverse(graph)
			.startingWith(startVertex)
			.until(triplet -> graph.degreeOf(triplet.current()) == 1 && !triplet.current().equals(startVertex))
			.stream()
			.count();
		assertEquals(trail.size(), verticesTraversed);
	}

	@Test
	public void threeVerticesInLoop() {
		UndirectedGraph<Integer, DefaultEdge> graph =
			new GraphConstructor<Integer, DefaultEdge>((a, b) -> new DefaultEdge())
				.cycleOfVertices(Lists.newArrayList(1, 2, 3))
				.graph();
		long verticesTraversed = GraphChainTraversal.traverse(graph)
			.startingWith(1)
			.awayFrom(3)
			.past(2)
			.until(x -> false)
			.stream()
			.count();
		assertEquals(3, verticesTraversed);
	}

	@Test
	public void twoVertexChain() {
		UndirectedGraph<String, DefaultEdge> graph =
			new GraphConstructor<String, DefaultEdge>((a, b) -> new DefaultEdge())
				.vertex(0, "hello")
				.vertex(1, "goodbye")
				.edge(0, 1)
				.graph();
		long verticesTraversed = GraphChainTraversal.traverse(graph)
			.startingWith("hello")
			.stream()
			.count();
		assertEquals(2, verticesTraversed);
	}

	@Test
	public void chainBetweenTwoRadialShapes() {
		UndirectedGraph<String, DefaultEdge> graph =
			new GraphConstructor<String, DefaultEdge>((a, b) -> new DefaultEdge())
				.vertex(1, "cat")
				.vertex(2, "dog")
				.vertex(3, "cow")
				.vertex(4, "human")

				.vertex(11, "asimo")
				.vertex(12, "roomba")
				.vertex(13, "AT-AT")
				.vertex(14, "Mr. Roboto")

				.vertex(100, "animal").withEdgesTo(1, 2, 3, 4)
				.vertex(300, "robot").withEdgesTo(11, 12, 13, 14)

				.vertex(200, "cyborg")
				.edge(4, 200)
				.edge(11, 200)

				.graph();
		long verticesTraversed = GraphChainTraversal.traverse(graph)
			.startingWith("animal")
			.past("human")
			.until(triplet -> triplet.current().equals("robot"))
			.stream()
			.peek(System.out::println)
			.count();
		assertEquals(5, verticesTraversed);
	}
}