package org.tendiwa.settlements.networks;

import org.jgrapht.UndirectedGraph;
import org.tendiwa.geometry.Point2D;
import org.tendiwa.geometry.Segment2D;
import org.tendiwa.settlements.EnclosedBlock;

import java.util.Iterator;

/**
 * Finds out which cycles in a {@link Segment2DSmartMesh} are enclosed within
 * another cycle.
 * <p>
 * An <i>enclosed cycle</i> is the one all of whose points are inside one of {@link org.tendiwa.settlements.networks
 * .RoadsPlanarGraphModel#getCyclesRoadGraph()}'s cycle.
 */
// TODO: Move to a method of NetworkWithinCycle
public final class EnclosedCyclesSet {

	private final UndirectedGraph<Point2D, Segment2D> cyclesRoadGraph;

	/**
	 * @param segment2DSmartMesh
	 * 	A graph model for which enclosed cycles set is to be found.
	 */
	public EnclosedCyclesSet(Segment2DSmartMesh segment2DSmartMesh) {
		cyclesRoadGraph = segment2DSmartMesh.getFullCycleGraph();
	}

	public boolean contains(EnclosedBlock block) {
		/**
		 * A block is considered to be in the enclosed cycles set iff its edges are all contained among split
		 * original edges.
		 */
		Iterator<Point2D> iter = block.iterator();
		if (!iter.hasNext()) {
			return false;
		}

		Point2D first = iter.next();
		Point2D previous = first;
		while (iter.hasNext()) {
			Point2D current = iter.next();
			assert cyclesRoadGraph.containsEdge(previous, current) == cyclesRoadGraph.containsEdge(current, previous);
			if (!cyclesRoadGraph.containsEdge(previous, current)) {
				return false;
			}
			previous = current;
		}
		if (!cyclesRoadGraph.containsEdge(previous, first)) {
			return false;
		}
		return true;
	}
}
