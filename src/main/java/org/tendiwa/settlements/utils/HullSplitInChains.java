package org.tendiwa.settlements.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Polyline;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.geometry.graphs2d.Graph2D;
import org.tendiwa.geometry.graphs2d.MeshedNetwork_Wr;
import org.tendiwa.geometry.smartMesh.MeshedNetwork;
import org.tendiwa.graphs.GraphChainTraversal;
import org.tendiwa.settlements.utils.streetsDetector.ConnectivityComponent;

import static org.tendiwa.collections.Collectors.toImmutableList;
import static org.tendiwa.collections.Collectors.toImmutableSet;

final class HullSplitInChains extends MeshedNetwork_Wr {
	protected HullSplitInChains(MeshedNetwork network) {
		super(network);
	}

	public ImmutableList<Polyline> hullChains() {
		Graph2D outerHull = outerHull();
		return outerHull.connectivityComponents()
			.stream()
			.flatMap(
				component ->
					GraphChainTraversal.traverse(component.graph())
						.startingWith(anyHighDegreeVertex(component))
						.stream()
						.map(GraphChainTraversal.NeighborsTriplet::current)
						.collect(Polyline.toPolyline())
						.splitAtPoints(highDegreeVertices(component))
						.stream()
			)
			.collect(toImmutableList());
	}

	private ImmutableSet<Point2D> highDegreeVertices(ConnectivityComponent<Point2D, Segment2D> component) {
		return component.graph().vertexSet()
			.stream()
			.filter(v -> component.graph().degreeOf(v) > 2)
			.collect(toImmutableSet());
	}

	private Point2D anyHighDegreeVertex(ConnectivityComponent<Point2D, Segment2D> component) {
		return component.graph()
			.vertexSet()
			.stream()
			.filter(v -> component.graph().degreeOf(v) > 2)
			.findFirst()
			.get();
	}
}
