package org.tendiwa.geometry.extensions;

import org.junit.Test;
import org.tendiwa.geometry.BasicCell;
import org.tendiwa.geometry.FiniteCellSet;
import org.tendiwa.geometry.extensions.intershapeNetwork.IntershapeNetwork;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.rectangle;

public class IntershapeNetworkTest {
	@Test
	public void network() {
		List<FiniteCellSet> shapeExitSets = asList(
			FiniteCellSet.of(new BasicCell(1, 1)),
			FiniteCellSet.of(new BasicCell(4, 9)),
			FiniteCellSet.of(new BasicCell(8, 8)),
			FiniteCellSet.of(new BasicCell(5, 5), new BasicCell(5, 6))
		);
		int numberOfEdges = IntershapeNetwork
			.withShapeExits(shapeExitSets)
			.withWalkableCells(
				(x, y) -> rectangle(10, 10).contains(x, y)
			)
			.edgeSet()
			.size();
		assertTrue(String.valueOf(numberOfEdges), numberOfEdges == 3);
	}
}
