package org.tendiwa.settlements;

import org.jgrapht.UndirectedGraph;
import org.junit.Before;
import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.GraphConstructor;
import org.tendiwa.geometry.smartMesh.SegmentNetworkBuilder;
import org.tendiwa.geometry.smartMesh.Segment2DSmartMesh;
import org.tendiwa.settlements.utils.RoadRejector;

import java.util.Random;

import static org.junit.Assert.*;

public class RoadRejectorTest {

	private Segment2DSmartMesh geometry;
	private UndirectedGraph<Point2D, Segment2D> fullRoadGraph;

	@Before
	public void setUp() {
		UndirectedGraph<Point2D, Segment2D> topology = new GraphConstructor<>(Segment2D::new)
			.vertex(0, new Point2D(50, 50))
			.vertex(1, new Point2D(150, 50))
			.vertex(2, new Point2D(160, 150))
			.vertex(3, new Point2D(50, 150))
			.cycle(0, 1, 2, 3)
			.graph();
		geometry = new SegmentNetworkBuilder(topology)
			.withDefaults()
			.build();
		fullRoadGraph = geometry.getFullRoadGraph();
	}

	/**
	 * Some of roads are rejected ({@code probability == 0.5}).
	 */
	@Test
	public void roadRejectionWithSingleCycleCityGraph() {
		RoadRejector.rejectPartOfNetworksBorders(fullRoadGraph, geometry, 0.5, new Random(1));

	}

	/**
	 * No roads rejected because {@code probability == 0.0}
	 */
	@Test
	public void noRoadRejection() {
		UndirectedGraph<Point2D, Segment2D> unmodifiedGraph = RoadRejector.rejectPartOfNetworksBorders(fullRoadGraph, geometry, 0.0, new Random(1));
		assertTrue(fullRoadGraph.edgeSet().equals(unmodifiedGraph.edgeSet()));
	}

	/**
	 * All roads rejected because {@code probability == 1.0}
	 */
	@Test
	public void allOuterRoadsRejected() {
		UndirectedGraph<Point2D, Segment2D> graphWithoutCycleEdges = RoadRejector.rejectPartOfNetworksBorders(fullRoadGraph, geometry, 1.0, new Random(1));
		assertTrue(
			geometry
				.networks()
				.stream()
				.flatMap(network -> network.cycle().edgeSet().stream())
				.allMatch(e -> !graphWithoutCycleEdges.containsEdge(e))
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void wrongProbability() {
		RoadRejector.rejectPartOfNetworksBorders(geometry.getFullRoadGraph(), geometry, 2.0, new Random(1));
		RoadRejector.rejectPartOfNetworksBorders(geometry.getFullRoadGraph(), geometry, -2.0, new Random(1));
	}

}