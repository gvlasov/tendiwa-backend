package org.tendiwa.settlements.networks;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.settlements.networks.algorithms.SegmentNetworkAlgorithms;

import java.awt.Color;
import java.util.Map;
import java.util.Random;

import static org.tendiwa.collections.Collectors.toImmutableSet;

/**
 * A geometrical model, for example of a city, on the most basic level: roads (represented as
 * {@link org.tendiwa.geometry.Segment2D})
 * and polygonal empty areas between those roads.
 * <p>
 * This class serves two purposes:
 * <ol>
 * <li>
 * Creates a planar non-self-intersecting graph within minimal cycles of another planar
 * non-self-intersecting graph;
 * </li>
 * <li>
 * Within the constructed graph, finds empty polygonal areas between its edges.
 * </li>
 * </ol>
 * <p>
 * The intended use of this class is to create the geometry of {@link org.tendiwa.settlements.buildings.City}'s roads,
 * and then to find housing quarters between those roads. More generally speaking, this class can build randomized
 * networks inside arbitrary polygonal areas defined by minimal cycles of some planar non-self-intersecting graph.
 */
public final class Segment2DSmartMesh {
	/**
	 * [Kelly section 4.2.2]
	 */
	private final ImmutableSet<NetworkWithinCycle> networks;

	/**
	 * @param originalGraph
	 * 	[Kelly chapter 4.2, figure 38]
	 * 	<p>
	 * 	A graph that defines city's road network topology.
	 * 	How many samples per step should a {@code strategy} try.
	 * 	Angle between two samples, in radians.
	 * @throws IllegalArgumentException
	 * 	If {@code numberOfSamples <= 0} or if {@code deviationAngle == 0 && numberOfSamples >= 1}, or if
	 * 	#originalGraph produced from #originalGraph intersects itself.
	 */
	Segment2DSmartMesh(
		UndirectedGraph<Point2D, Segment2D> originalGraph,
		NetworkGenerationParameters parameters,
		Random random
	) {
		if (ShamosHoeyAlgorithm.areIntersected(originalGraph.edgeSet())) {
			TestCanvas.canvas.draw(originalGraph, DrawingGraph.withColorAndAntialiasing(Color.cyan));
			throw new IllegalArgumentException("Graph intersects itself");
		}

		this.networks = new NetworksProducer(
			originalGraph,
			parameters,
			random
		).stream()
			.collect(toImmutableSet());
		if (networks.isEmpty()) {
			throw new GeometryException("A RoadPlanarGraphModel with 0 city networks was made");
		}
	}

	/**
	 * Returns all CityCells of this City.
	 *
	 * @return All CityCells of this City.
	 */
	public ImmutableSet<NetworkWithinCycle> getNetworks() {
		return networks;
	}

	public UndirectedGraph<Point2D, Segment2D> getFullRoadGraph() {
		return SegmentNetworkAlgorithms.createFullGraph(this);
	}

	public UndirectedGraph<Point2D, Segment2D> getFullCycleGraph() {
		return SegmentNetworkAlgorithms.createFullCycleGraph(this);
	}

	public Map<NetworkWithinCycle, UndirectedGraph<Point2D, Segment2D>> outerCycleEdges(
	) {
		return SegmentNetworkAlgorithms.outerCycleEdges(this);
	}
}
