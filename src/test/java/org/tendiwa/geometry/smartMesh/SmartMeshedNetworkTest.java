package org.tendiwa.geometry.smartMesh;

import org.junit.Before;
import org.junit.Test;
import org.tendiwa.data.FourCyclePenisGraph;
import org.tendiwa.geometry.PolygonGraph;
import org.tendiwa.geometry.graphs2d.BasicPolylineGraph;
import org.tendiwa.geometry.graphs2d.Graph2D;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.pointTrail;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle2D;

public final class SmartMeshedNetworkTest {

	private MeshedNetwork network;
	private Graph2D originalGraph;

	@Before
	public void createMesh() throws Exception {
		originalGraph = new FourCyclePenisGraph();
		network = new DefaultMeshedNetwork(originalGraph);
	}

	@Test
	public void canProduceAdditionalSegments() throws Exception {
		assertTrue(originalGraph.edgeSet().size() <= network.edgeSet().size());
	}

	@Test
	public void consistsOfCells() throws Exception {
		assertTrue(!network.meshCells().isEmpty());
	}

	@Test
	public void canConsistOfOnlyFilaments() throws Exception {
		assertEquals(
			0,
			new DefaultMeshedNetwork(
				new BasicPolylineGraph(
					pointTrail(0, 0)
						.moveBy(20, 0)
						.moveBy(0, 20)
						.polyline()
				)
			).meshCells().size()
		);
	}


	@Test
	public void canHaveFilaments() throws Exception {
		assertEquals(
			3,
			new DefaultMeshedNetwork(
				new GraphWith2CyclesAnd3Filaments()
			)
				.minimumCycleBasis()
				.filamentsSet()
				.size()
		);
	}

	@Test
	public void canBeWithoutFilaments() throws Exception {
		assertEquals(
			0,
			new DefaultMeshedNetwork(
				new PolygonGraph(
					rectangle2D(10, 10)
				)
			)
				.minimumCycleBasis()
				.filamentsSet()
				.size()
		);
	}
}