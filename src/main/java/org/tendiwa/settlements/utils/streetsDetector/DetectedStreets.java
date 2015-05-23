package org.tendiwa.settlements.utils.streetsDetector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.tendiwa.geometry.Chain2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;

import java.util.stream.Stream;

/**
 * Finds chains of {@link org.tendiwa.geometry.Segment2D} to form {@link org.tendiwa.settlements.streets.Street}s
 * among edges of a graph.
 */
public final class DetectedStreets {
	/**
	 * Finds streets made of edges of a planar graph.
	 *
	 * @param cityGraph
	 * 	A planar graph.
	 * @return Streets found in a road graph.
	 */
	public static Stream<Chain2D> toChain2DStream( Graph2D cityGraph ) {
		UndirectedGraph<Point2D, Segment2D> jgraphtGraph = cityGraph.toJgrapht();
		return new ConnectivityInspector<>(jgraphtGraph).connectedSets().stream()
			.map(set -> new ConnectivityComponent<>(jgraphtGraph, set))
			.map(ConnectivityComponent::graph)
			.map(ChainNetwork::new)
			.flatMap(ChainNetwork::chains);
	}
}