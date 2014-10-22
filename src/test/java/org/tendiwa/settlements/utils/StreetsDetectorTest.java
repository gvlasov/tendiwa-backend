package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import org.jgrapht.UndirectedGraph;
import org.junit.Assert;
import org.junit.Test;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.graphs.GraphConstructor;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StreetsDetectorTest {
	/**
	 * Finds streets in a road grpha with two connectivity components.
	 */
	@Test
	public void twoConnectivityComponents() {
		UndirectedGraph<Point2D, Segment2D> graph = new GraphConstructor<>(Segment2D::new)
			// First connectivity component
			.cycleOfVertices(new PointTrail(40, 40).moveBy(30, 10).moveBy(-20, 20).points())
				// Second connectivity component
			.cycleOfVertices(new PointTrail(240, 40).moveBy(30, 10).moveBy(-20, 20).points())
			.graph();
		Set<ImmutableList<Point2D>> streets = StreetsDetector.detectStreets(graph);
		assertEquals(2, streets.size());
	}

}