package org.tendiwa.settlements.networks;

import org.jgrapht.Graph;
import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.util.*;
import java.util.stream.Stream;

final class FullNetwork implements NetworkPart {

	private final Map<Segment2D, Collection<NetworkPart>> edgesToUsers = new LinkedHashMap<>();
	private final Graph2D graph;

	FullNetwork() {
		this.graph = new Graph2D();
	}

	void addEdgeUser(NetworkPart networkPart) {
		Graph2D graph = networkPart.graph();
		for (Segment2D edge : graph.edgeSet()) {
			addNetworkPart(edge, networkPart);
		}
	}

	@Override
	public void notify(CutSegment2D cutSegment) {
		graph.removeEdge(cutSegment.originalSegment());
		graph.integrateCutSegment(cutSegment);
	}

	@Override
	public Graph2D graph() {
		return graph;
	}

	void addNetworkPart(Segment2D edge, NetworkPart networkPart) {
		assert anotherEdgeOfGraphIsInFullGraph(networkPart.graph(), edge);
		if (!networkPart.graph().containsEdge(edge)) {
			throw new IllegalArgumentException("Added edge is not contained in graph");
		}
		edgesToUsers
			.computeIfAbsent(edge, (e) -> new ArrayList<>())
			.add(networkPart);
	}

	private boolean anotherEdgeOfGraphIsInFullGraph(Graph<Point2D, Segment2D> graph, Segment2D edge) {
		return graph.edgeSet().stream()
			.filter(e -> !e.equals(edge))
			.findAny()
			.isPresent();
	}

//	SplitSegment2D splitEdge(Segment2D edgeToSplit, Point2D splitPoint) {
//		SplitSegment2D splitSegment = new SplitSegment2D(edgeToSplit, splitPoint);
//		Collection<Graph2D> graphsBefore = obtainCollectionFor(splitSegment.firstHalf());
//		Collection<Graph2D> graphsAfter = obtainCollectionFor(splitSegment.secondHalf());
//		for (Graph2D graph : edgesToUsers.get(edgeToSplit)) {
//			graph.integrateSplitSegment(splitSegment);
//			graphsBefore.add(graph);
//			graphsAfter.add(graph);
//		}
//		edgesToUsers.remove(edgeToSplit);
//		return splitSegment;
//	}

	void splitEdge(CutSegment2D cutSegment) {
		Segment2D originalSegment = cutSegment.originalSegment();
		Stream<Collection<NetworkPart>> graphHolders = cutSegment.stream()
			.map(this::obtainCollectionFor);
		for (NetworkPart user : edgesToUsers.get(originalSegment)) {
			user.notify(cutSegment);
			graphHolders = graphHolders.peek(gh -> gh.add(user));
		}
	}

	private Collection<NetworkPart> obtainCollectionFor(Segment2D segment) {
		assert !edgesToUsers.containsKey(segment);
		Collection<NetworkPart> collection = new ArrayList<>();
		edgesToUsers.put(segment, collection);
		return collection;
	}
}
