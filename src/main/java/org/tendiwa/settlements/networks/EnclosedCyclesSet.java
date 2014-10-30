package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.drawing.TestCanvas;
import org.tendiwa.drawing.extensions.DrawingEnclosedBlock;
import org.tendiwa.drawing.extensions.DrawingPoint2D;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.EnclosedBlock;

import java.awt.Color;
import java.util.Map;

public final class EnclosedCyclesSet {

	private final Map<Point2D, Segment2D> splitEdgesToOriginalEdges;
	private UndirectedGraph<Point2D, Segment2D> lowLevelRoadGraph;

	/**
	 * @param roadsPlanarGraphModel
	 * 	A graph model for which enclosed cycles set is to be found.
	 * @throws java.lang.IllegalArgumentException
	 * 	if {@link org.tendiwa.settlements.networks.RoadsPlanarGraphModel#lowLevelRoadGraph} of
	 * 	{@code roadsPlanarGraphModel} contains vertices of degree other than 2.
	 */
	public EnclosedCyclesSet(RoadsPlanarGraphModel roadsPlanarGraphModel) {
		this.lowLevelRoadGraph = roadsPlanarGraphModel.getLowLevelRoadGraph();
		boolean allVerticesHaveDegree2 = lowLevelRoadGraph.vertexSet()
			.stream()
			.allMatch(v -> lowLevelRoadGraph.degreeOf(v) == 2);
		if (!allVerticesHaveDegree2) {
			throw new IllegalArgumentException(
				"This class can only find enclosed cycles for graph models that have all vertices of degree 2"
			);
		}
		this.splitEdgesToOriginalEdges = roadsPlanarGraphModel.splitEdgesToOriginalEdges();
	}

	public boolean contains(EnclosedBlock block) {
		for (Point2D point : block) {
			if (!lowLevelRoadGraph.containsVertex(point) && !splitEdgesToOriginalEdges.containsKey(point)) {
				return false;
			}
		}
		return true;
	}
}
