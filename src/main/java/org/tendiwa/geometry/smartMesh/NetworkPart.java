package org.tendiwa.geometry.smartMesh;

import org.tendiwa.geometry.CutSegment2D;
import org.tendiwa.graphs.graphs2d.MutableGraph2D;

interface NetworkPart {
	public default void integrateSplitEdge(CutSegment2D cutSegment) {
		graph().integrateCutSegment(cutSegment);
	}

	public MutableGraph2D graph();
}
