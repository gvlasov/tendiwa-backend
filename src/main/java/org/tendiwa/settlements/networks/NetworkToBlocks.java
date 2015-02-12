package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.extensions.PlanarGraphs;
import org.tendiwa.geometry.extensions.Point2DVertexPositionAdapter;
import org.tendiwa.graphs.MinimumCycleBasis;
import org.tendiwa.graphs.algorithms.SameOrPerpendicularSlopeGraphEdgesPerturbations;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Divides space inside a network into enclosed blocks.
 */
class NetworkToBlocks {
	private final List<SecondaryRoadNetworkBlock> enclosedBlocks;
	private final Set<DirectionFromPoint> filamentEnds;
	private final double snapSize;

	NetworkToBlocks(
		UndirectedGraph<Point2D, Segment2D> relevantNetwork,
		Set<DirectionFromPoint> filamentEnds,
		NetworkGenerationParameters networkGeneratoinParameters
	) {
		this.filamentEnds = filamentEnds;
		this.snapSize = networkGeneratoinParameters.roadSegmentLength
			+ networkGeneratoinParameters.secondaryNetworkSegmentLengthDeviation;

		if (needsLooseEndsClosing()) {
			UndirectedGraph<Point2D, Segment2D> graphWithLooseEnds = PlanarGraphs.copyGraph(relevantNetwork);
			assert filamentEnds.stream().allMatch(end -> graphWithLooseEnds.degreeOf(end.node) == 1);
			GraphLooseEndsCloser
				.withSnapSize(snapSize)
				.withFilamentEnds(filamentEnds)
				.mutateGraph(graphWithLooseEnds);
			relevantNetwork = graphWithLooseEnds;
		}
		SameOrPerpendicularSlopeGraphEdgesPerturbations.perturb(relevantNetwork, 1e-4);

		enclosedBlocks = PlanarGraphs
			.minimumCycleBasis(relevantNetwork)
			.minimalCyclesSet()
			.stream()
			.map(cycle -> new SecondaryRoadNetworkBlock(cycle.vertexList()))
			.collect(toList());
	}

	private boolean needsLooseEndsClosing() {
		return !filamentEnds.isEmpty();
	}

	private boolean areAllVerticesOfDegree1(Set<DirectionFromPoint> filamentEnds, UndirectedGraph<Point2D, Segment2D> r) {
		return filamentEnds.stream().allMatch(e -> r.degreeOf(e.node) == 1);
	}

	public List<SecondaryRoadNetworkBlock> getEnclosedBlocks() {
		return enclosedBlocks;
	}

}
