package org.tendiwa.settlements.utils;

import org.junit.Test;
import org.tendiwa.geometry.extensions.PointTrail;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.settlements.utils.streetsDetector.DetectedStreets;

import static org.junit.Assert.*;
import static org.tendiwa.geometry.GeometryPrimitives.graph2D;
import static org.tendiwa.geometry.GeometryPrimitives.graphConstructor;

public class DetectedStreetsTest {

	private Graph2D graphWithTwoConnectivityComponents =
		graph2D(
			graphConstructor()
				// First connectivity component
				.cycleOfVertices(new PointTrail(40, 40).moveBy(30, 10).moveBy(-20, 20).points())
					// Second connectivity component
				.cycleOfVertices(new PointTrail(240, 40).moveBy(30, 10).moveBy(-20, 20).points())
				.graph()
		);

	/**
	 * Finds streets in a road graph with two connectivity components.
	 */
	@Test
	public void twoCicrularConnectivityComponents() {
		assertEquals(
			2,
			DetectedStreets
				.toChain2DStream(graphWithTwoConnectivityComponents)
				.count()
		);
	}

}