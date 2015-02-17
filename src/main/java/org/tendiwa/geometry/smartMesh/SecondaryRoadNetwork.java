package org.tendiwa.geometry.smartMesh;

import com.google.common.collect.Sets;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.graphs.graphs2d.Graph2D;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

final class SecondaryRoadNetwork implements NetworkPart {

	private final SegmentInserter segmentInserter;
	private final Graph2D graph;

	private final Collection<OrientedCycle> enclosedCycles;

	SecondaryRoadNetwork(
		FullNetwork fullNetwork,
		NetworkPart splitOriginalMesh,
		OrientedCycle enclosingCycle,
		Collection<OrientedCycle> enclosedCycles,
		NetworkGenerationParameters networkGenerationParameters,
		Random random
	) {
		this.enclosedCycles = enclosedCycles;

		this.graph = new Graph2D();
		this.segmentInserter = new SegmentInserter(
			fullNetwork,
			splitOriginalMesh.graph(),
			this,
			networkGenerationParameters,
			random
		);
		new Forest(
			fullNetwork,
			segmentInserter,
			enclosingCycle,
			networkGenerationParameters,
			random
		).grow();
		addMissingConnectionsWithEnclosedCycles();
	}


	public Graph2D graph() {
		return graph;
	}




	private void addMissingConnectionsWithEnclosedCycles() {
		Set<Point2D> secondaryNetworkVertices = graph.vertexSet();
		for (OrientedCycle cycle : enclosedCycles) {
			Set<Point2D> connections = Sets.intersection(
				cycle.graph().vertexSet(),
				secondaryNetworkVertices
			);
			if (connections.size() == 1) {
				segmentInserter.addMissingConnectionToEnclosedCycle(cycle, connections.iterator().next());
			} else if (connections.size() == 0) {
				segmentInserter.addTwoMissingConnectionsToEnclosedCycle(cycle);
			}
		}
	}
}
