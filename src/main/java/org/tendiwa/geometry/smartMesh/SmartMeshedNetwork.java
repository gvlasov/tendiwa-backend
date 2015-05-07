package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableSet;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawableGraph2D;
import org.tendiwa.geometry.GeometryException;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.ShamosHoeyAlgorithm;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.Mesh2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

import java.awt.Color;
import java.util.Map;
import java.util.Random;

import static org.tendiwa.collections.Collectors.toImmutableSet;

public final class SmartMeshedNetwork implements MeshedNetwork {
	private final ImmutableSet<OriginalMeshCell> networks;
	private final MutableGraph2D fullGraph;
	private final MutableGraph2D fullCycleGraph;
	private ImmutableSet<Mesh2D> meshes;

	SmartMeshedNetwork(
		Graph2D originalGraph,
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
		TestCanvas.canvas.draw(
			new DrawableGraph2D.Thin(
				getFullCycleGraph(),
				Color.red
			)
		);
	}

	private void errorIfGraphIntersectsItself(UndirectedGraph<Point2D, Segment2D> originalGraph) {
		if (ShamosHoeyAlgorithm.areIntersected(originalGraph.edgeSet())) {
			TestCanvas.canvas.draw(
				new DrawableGraph2D.Thin(
					originalGraph,
					Color.cyan
				)
			);
			throw new IllegalArgumentException("Graph intersects itself");
		}
	}

	public ImmutableSet<OriginalMeshCell> networks() {
		return networks;
	}

	@Override
	public ImmutableSet<Graph2D> filaments() {
		return null;
	}

	@Override
	public ImmutableSet<Mesh2D> meshes() {
		return meshes;
	}

	@Override
	public Graph2D fullGraph() {
		return fullGraph;
	}

	@Override
	public Graph2D outerHull() {
		return new OuterHull(this).graph();
	}

	public Graph2D getFullCycleGraph() {
		return fullCycleGraph;
	}

	public ImmutableSet<Segment2D> innerTreeSegmentsEnds() {
		return networks.stream()
			.flatMap(network -> network.innerTreesEndSegments().stream())
			.collect(toImmutableSet());
	}
}
