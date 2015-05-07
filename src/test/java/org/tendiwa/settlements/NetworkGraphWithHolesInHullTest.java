package org.tendiwa.settlements;

import org.jgrapht.UndirectedGraph;
import org.junit.Before;
import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.smartMesh.MeshedNetworkBuilder;
import org.tendiwa.geometry.smartMesh.SmartMeshedNetwork;
import org.tendiwa.settlements.utils.NetworkGraphWithHolesInHull;

import java.util.Random;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;
import static org.tendiwa.geometry.GeometryPrimitives.point2D;

public class NetworkGraphWithHolesInHullTest {

	private SmartMeshedNetwork geometry;
	private UndirectedGraph<Point2D, Segment2D> fullRoadGraph;

	@Before
	public void setUp() {
		UndirectedGraph<Point2D, Segment2D> topology = graphConstructor()
			.vertex(0, point2D(50, 50))
			.vertex(1, point2D(150, 50))
			.vertex(2, point2D(160, 150))
			.vertex(3, point2D(50, 150))
			.cycle(0, 1, 2, 3)
			.graph();
		geometry = new MeshedNetworkBuilder(topology)
			.withDefaults()
			.build();
		fullRoadGraph = geometry.fullGraph();
	}

	/**
	 * Some of roads are rejected ({@code probability == 0.5}).
	 */
	@Test
	public void roadRejectionWithSingleCycleCityGraph() {
		NetworkGraphWithHolesInHull.rejectPartOfNetworksBorders(fullRoadGraph, geometry, 0.5, new Random(1));

	}

	/**
	 * No roads rejected because {@code probability == 0.0}
	 */
	@Test
	public void noRoadRejection() {
		UndirectedGraph<Point2D, Segment2D> unmodifiedGraph = NetworkGraphWithHolesInHull.rejectPartOfNetworksBorders(fullRoadGraph, geometry, 0.0, new Random(1));
		assertTrue(fullRoadGraph.edgeSet().equals(unmodifiedGraph.edgeSet()));
	}

	/**
	 * All roads rejected because {@code probability == 1.0}
	 */
	@Test
	public void allOuterRoadsRejected() {
		UndirectedGraph<Point2D, Segment2D> graphWithoutCycleEdges = NetworkGraphWithHolesInHull.rejectPartOfNetworksBorders(fullRoadGraph, geometry, 1.0, new Random(1));
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
		NetworkGraphWithHolesInHull.rejectPartOfNetworksBorders(geometry.fullGraph(), geometry, 2.0, new Random(1));
		NetworkGraphWithHolesInHull.rejectPartOfNetworksBorders(geometry.fullGraph(), geometry, -2.0, new Random(1));
	}

}