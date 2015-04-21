package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingGraph;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.geometry.graphs2d.Mesh2D;
import org.tendiwa.geometry.smartMesh.algorithms.SegmentNetworkAlgorithms;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.awt.Color;
import java.util.Map;
import java.util.Random;

import static org.tendiwa.collections.Collectors.toImmutableSet;

public final class SmartMesh2D implements Mesh2D {
	private final ImmutableSet<OriginalMeshCell> networks;
	private final MutableGraph2D fullGraph;
	private final MutableGraph2D fullCycleGraph;

	SmartMesh2D(
		UndirectedGraph<Point2D, Segment2D> originalGraph,
		NetworkGenerationParameters parameters,
		Random random
	) {
		errorIfGraphIntersectsItself(originalGraph);

		NetworksProducer networksProducer = new NetworksProducer(
			originalGraph,
			parameters,
			random
		);
		this.networks = networksProducer.stream()
			.collect(toImmutableSet());
		this.fullGraph = networksProducer.fullGraph();
		this.fullCycleGraph = networksProducer.fullCycleGraph();
		if (networks.isEmpty()) {
			throw new GeometryException("A RoadPlanarGraphModel with 0 city networks was made");
		}
		TestCanvas.canvas.draw(getFullCycleGraph(), DrawingGraph.withColorAndAntialiasing(Color.red));
	}

	private void errorIfGraphIntersectsItself(UndirectedGraph<Point2D, Segment2D> originalGraph) {
		if (ShamosHoeyAlgorithm.areIntersected(originalGraph.edgeSet())) {
			TestCanvas.canvas.draw(originalGraph, DrawingGraph.withColorAndAntialiasing(Color.cyan));
			throw new IllegalArgumentException("Graph intersects itself");
		}
	}

	public ImmutableSet<OriginalMeshCell> networks() {
		return networks;
	}

	@Override
	public UndirectedGraph<Point2D, Segment2D> graph() {
		return fullGraph;
	}

	public UndirectedGraph<Point2D, Segment2D> getFullCycleGraph() {
		return fullCycleGraph;
	}

	public Map<OriginalMeshCell, UndirectedGraph<Point2D, Segment2D>> outerCycleEdges() {
		return SegmentNetworkAlgorithms.outerCycleEdges(this);
	}

	public ImmutableSet<Segment2D> innerTreeSegmentsEnds() {
		return networks.stream()
			.flatMap(network -> network.innerTreesEndSegments().stream())
			.collect(toImmutableSet());
	}
}
