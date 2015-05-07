package org.tendiwa.geometry.smartMesh.algorithms;

import org.junit.Test;
import org.tendiwa.data.FourCyclePenisGraph;
import org.tendiwa.geometry.smartMesh.MeshedNetworkBuilder;

import static org.junit.Assert.*;

public class MeshWithExcludedEdgesTest {
	@Test
	public void excludesEdges() {
		MeshWithExcludedEdges network = new MeshWithExcludedEdges(
			new MeshedNetworkBuilder(
				new FourCyclePenisGraph()
			).withDefaults().build()
		);
		assertTrue(!network.edgeSet().isEmpty());
		assertTrue(
			network.excludedEdges().stream().allMatch(
				excluded -> !network.containsEdge(excluded)
			)
		);
	}

}