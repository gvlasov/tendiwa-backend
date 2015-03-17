package org.tendiwa.settlements.utils.streetsDetector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.tendiwa.collections.DoublyLinkedNode;
import org.tendiwa.geometry.*;

import java.util.stream.Stream;

final class ChainNetwork {
	private final UndirectedGraph<Point2D, Segment2D> cityGraph;

	ChainNetwork(UndirectedGraph<Point2D, Segment2D> cityGraph) {
		assert new ConnectivityInspector<>(cityGraph).isGraphConnected();
		this.cityGraph = cityGraph;
	}

	Stream<Chain2D> chains() {
		ChainAssembly chainAssembly = assembleEdgesToLinkedLists();
		return Stream.concat(
			findMultiSegmentEdges(chainAssembly),
			findSingleSegmentEdges(chainAssembly)
		);
	}

	private Stream<Chain2D> findSingleSegmentEdges(ChainAssembly chainAssembly) {
		return cityGraph.edgeSet().stream()
			.filter(bone -> !chainAssembly.usedBone(bone))
			.map(SingleSegmentChain2D::new);
	}

	private Stream<Chain2D> findMultiSegmentEdges(ChainAssembly chainAssembly) {
		return chainAssembly.nodes().stream()
			.filter(DoublyLinkedNode::isStartOfAChain)
			.map(LinkedListBasedChain2D::new);
	}

	private ChainAssembly assembleEdgesToLinkedLists() {
		ChainAssembly chainAssembly = new ChainAssembly(cityGraph.edgeSet().size());
		cityGraph.vertexSet().stream()
			.map(v -> new RadialJointShape(
				v,
				cityGraph.edgesOf(v)
			))
			.flatMap(RadialJointShape::joints)
			.forEach(chainAssembly::addJoint);
		return chainAssembly;
	}
}
