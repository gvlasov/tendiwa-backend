package org.tendiwa.settlements;

import org.junit.Before;
import org.junit.Test;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.geometry.smartMesh.MeshedNetworkBuilder;
import org.tendiwa.settlements.utils.NetworkGraphWithHolesInHull;

import java.util.Random;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.*;

public class NetworkGraphWithHolesInHullTest {

	private MeshedNetwork network;
	private Graph2D fullRoadGraph;

	@Before
	public void setUp() {
		network = new MeshedNetworkBuilder(
			graph2D(
				graphConstructor()
					.vertex(0, point2D(50, 50))
					.vertex(1, point2D(150, 50))
					.vertex(2, point2D(160, 150))
					.vertex(3, point2D(50, 150))
					.cycle(0, 1, 2, 3)
					.graph()
			)
		)
			.withDefaults()
			.build();
		fullRoadGraph = network;
	}

	/**
	 * Some of roads are rejected ({@code probability == 0.5}).
	 */
	@Test
	public void roadRejectionWithSingleCycleCityGraph() {
		new NetworkGraphWithHolesInHull(
			network,
			0.5,
			new Random(1)
		);
	}

	/**
	 * No roads rejected because {@code probability == 0.0}
	 */
	@Test
	public void noRoadRejection() {
		assertTrue(
			new NetworkGraphWithHolesInHull(
				network,
				0.0,
				new Random(1)
			)
				.edgeSet()
				.equals(fullRoadGraph.edgeSet())
		);
	}

	/**
	 * All roads rejected because {@code probability == 1.0}
	 */
	@Test
	public void allOuterRoadsRejected() {
		Graph2D graphWithoutCycleEdges =
			new NetworkGraphWithHolesInHull(
				network,
				1.0,
				new Random(1)
			);
		assertTrue(
			network
				.outerHull()
				.edgeSet()
				.stream()
				.allMatch(e -> !graphWithoutCycleEdges.containsEdge(e))
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tooBigProbability() {
		new NetworkGraphWithHolesInHull(
			network,
			2.0,
			new Random(1)
		);
	}

	@Test(expected = IllegalArgumentException.class)
	public void tooSmallProbability() {
		new NetworkGraphWithHolesInHull(
			network,
			-2.0,
			new Random(1)
		);
	}
}