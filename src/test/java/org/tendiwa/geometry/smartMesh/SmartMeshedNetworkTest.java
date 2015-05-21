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
		assertTrue(originalGraph.edgeSet().size() <= network.fullGraph().edgeSet().size());
	}

	@Test
	public void consistsOfCells() throws Exception {
		assertTrue(!network.meshes().isEmpty());
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
			).meshes().size()
		);
	}

	@Test
	public void canConsistOfOneMesh() {
		assertEquals(
			1,
			new DefaultMeshedNetwork(
				new PolygonGraph(
					pointTrail(0, 0)
						.moveByX(20)
						.moveByY(30)
						.moveByX(-40)
						.polygon()
				)
			).meshes().size()
		);
	}

	@Test
	public void canConsistOfMultipleMeshes() {
		assertEquals(
			2,
			new DefaultMeshedNetwork(
				new GraphWith2CyclesAnd3Filaments()
			).meshes().size()
		);
	}


	@Test
	public void canHaveFilaments() throws Exception {
		assertEquals(
			3,
			new DefaultMeshedNetwork(
				new GraphWith2CyclesAnd3Filaments()
			).filaments().size()
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
			).filaments().size()
		);
	}
}