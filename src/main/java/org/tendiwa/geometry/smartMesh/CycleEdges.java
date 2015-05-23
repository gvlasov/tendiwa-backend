package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.ImmutableCollection;
import org.jgrapht.Graph;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.graphs.graphs2d.SplittableGraph2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

final class CycleEdges {

	private final Map<Segment2D, Collection<SplittableGraph2D>> edgesToSubgraphs = new LinkedHashMap<>();

	CycleEdges(ImmutableCollection<? extends SplittableGraph2D> subgraphs) {
		subgraphs.forEach(this::registerSubgraph);
	}

	void splitSharedEdge(CutSegment2D splitEdge) {
		if (!splitEdge.hasBeenCut()) {
			return;
		}
		Collection<SplittableGraph2D> affectedSubgraphs =
			edgesToSubgraphs.get(
				splitEdge.originalSegment()
			);
		affectedSubgraphs.forEach(subgraph -> subgraph.integrateCutSegment(splitEdge));
		splitEdge.segmentStream()
			.map(this::createCollectionFor)
			.forEach(collection -> collection.addAll(affectedSubgraphs));
	}

	private void registerSubgraph(SplittableGraph2D subgraph) {
		for (Segment2D edge : subgraph.edgeSet()) {
			shareEdgeWithNetworkPart(edge, subgraph);
		}
	}

	private void shareEdgeWithNetworkPart(Segment2D edge, SplittableGraph2D sharingSubgraph2D) {
		assert sharingSubgraph2D.hasOnlyEdge(edge)
			|| anotherEdgeOfGraphIsInFullGraph(sharingSubgraph2D, edge);
		if (!sharingSubgraph2D.containsEdge(edge)) {
			throw new IllegalArgumentException("Added edge is not contained in graph");
		}
		edgesToSubgraphs
			.computeIfAbsent(edge, (e) -> new ArrayList<>())
			.add(sharingSubgraph2D);
	}

	private boolean anotherEdgeOfGraphIsInFullGraph(Graph2D graph, Segment2D edge) {
		return graph.edgeSet().stream()
			.filter(e -> !e.equals(edge))
			.findAny()
			.isPresent();
	}

	private Collection<SplittableGraph2D> createCollectionFor(Segment2D segment) {
		assert !edgesToSubgraphs.containsKey(segment);
		Collection<SplittableGraph2D> collection = new ArrayList<>();
		edgesToSubgraphs.put(segment, collection);
		return collection;
	}

	boolean isShared(Segment2D edge) {
		return edgesToSubgraphs.containsKey(edge);
	}
}