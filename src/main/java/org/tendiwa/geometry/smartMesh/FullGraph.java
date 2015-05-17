package org.tendiwa.geometry.smartMesh;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.BasicMutableGraph2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This is a Mediator that notifies other {@link SharingSubgraph2D}s of splits in the full
 * graph if that NetworkPart shares an edge that is being split.
 */
final class FullGraph extends BasicMutableGraph2D implements SharingSubgraph2D {

	private final Map<Segment2D, Collection<SharingSubgraph2D>> edgesToSubgraphs = new LinkedHashMap<>();

	FullGraph(
		UndirectedGraph<Point2D, Segment2D> currentFullGraph
	) {
		super(currentFullGraph);
		registerSubgraph(this);
	}

	void registerSubgraph(SharingSubgraph2D subgraph) {
		for (Segment2D edge : subgraph.edgeSet()) {
			shareEdgeWithNetworkPart(edge, subgraph);
		}
	}


	void shareEdgeWithNetworkPart(Segment2D edge, SharingSubgraph2D sharingSubgraph2D) {
		assert sharingSubgraph2D.hasOnlyEdge(edge)
			|| anotherEdgeOfGraphIsInFullGraph(sharingSubgraph2D, edge);
		if (!sharingSubgraph2D.containsEdge(edge)) {
			throw new IllegalArgumentException("Added edge is not contained in graph");
		}
		edgesToSubgraphs
			.computeIfAbsent(edge, (e) -> new ArrayList<>())
			.add(sharingSubgraph2D);
	}

	private boolean anotherEdgeOfGraphIsInFullGraph(Graph<Point2D, Segment2D> graph, Segment2D edge) {
		return graph.edgeSet().stream()
			.filter(e -> !e.equals(edge))
			.findAny()
			.isPresent();
	}

	void splitSharedEdge(CutSegment2D splitEdge) {
		if (!splitEdge.hasBeenCut()) {
			return;
		}
		Collection<SharingSubgraph2D> affectedSubgraphs =
			edgesToSubgraphs.get(
				splitEdge.originalSegment()
			);
		affectedSubgraphs.forEach(part -> part.integrateCutSegment(splitEdge));
		registerNewEdges(
			splitEdge.segmentStream(),
			affectedSubgraphs
		);
	}

	private void registerNewEdges(
		Stream<Segment2D> edges,
		Collection<SharingSubgraph2D> affectedSubgraphs
	) {
		edges
			.map(this::obtainCollectionFor)
			.forEach(collection -> collection.addAll(affectedSubgraphs));
	}

	private Collection<SharingSubgraph2D> obtainCollectionFor(Segment2D segment) {
		assert !edgesToSubgraphs.containsKey(segment);
		Collection<SharingSubgraph2D> collection = new ArrayList<>();
		edgesToSubgraphs.put(segment, collection);
		return collection;
	}

	void integrateForest(InnerNetwork innerNetwork) {
		innerNetwork.whereBranchesStuckIntoCycles().forEach(this::splitSharedEdge);
	}

	@Override
	public FullGraph supergraph() {
		return this;
	}
}